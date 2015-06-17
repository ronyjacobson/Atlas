package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.TempTablesConstants;
import il.ac.tau.cs.databases.atlas.db.YagoParser;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.parsing.YagoLocation;
import il.ac.tau.cs.databases.atlas.parsing.YagoPerson;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseFilesCommand extends BaseDBCommand<Boolean>{
    private File yagoDateFile;
    private File yagoLocationFile;
    private File yagoCategoryFile;
    private File yagoLabelsFile;
    private File yagoWikiFile;
    private File yagoGeonamesFile;
    private File geonamesCitiesFile;
    private String parserOutputPath;
    private AtomicInteger progress;
    private long timestamp;
    private YagoParser yagoParser;

    public ParseFilesCommand(File yagoDateFile, File yagoLocationFile, File yagoCategoryFile, File yagoLabelsFile, File yagoWikiFile, File yagoGeonamesFile, File geonamesCitiesFile, String parserOutputPath, AtomicInteger progress) {
        this.yagoDateFile = yagoDateFile;
        this.yagoLocationFile = yagoLocationFile;
        this.yagoCategoryFile = yagoCategoryFile;
        this.yagoLabelsFile = yagoLabelsFile;
        this.yagoWikiFile = yagoWikiFile;
        this.yagoGeonamesFile = yagoGeonamesFile;
        this.geonamesCitiesFile = geonamesCitiesFile;
        this.parserOutputPath = parserOutputPath;
        this.progress = progress;
        //this.timestamp = System.currentTimeMillis();
        this.timestamp = 1433944465995l; // TODO: revert
        this.yagoParser = new YagoParser(yagoDateFile, yagoLocationFile, yagoCategoryFile, yagoLabelsFile, yagoWikiFile, yagoGeonamesFile, geonamesCitiesFile, parserOutputPath);
    }


    @Override
    protected Boolean innerExecute(Connection con) throws AtlasServerException {
        // GUI should check if files exist


        try {
            //yagoParser.parseFiles('a', 'e');
            yagoParser.parseFiles('f', 'k');
            //yagoParser.parseFiles('l', 'p');
            //yagoParser.parseFiles('q', 'z');
        } catch (IOException e) {
            e.printStackTrace();
        }

        //progress.getAndIncrement(); // Increment progress for progress bar

        // parse yago datafile into temp tsv

        // createTempTables(con);
        // create empty temp table

        // create final tables if not exists

        // combine temp tables into final tables (transaction?)

        // create temporary table to connect yago places and geonames

        //progress.getAndIncrement();

        createLocationsTable(con);
        int addedByUser = addYagoUser(con);
        // Insert data table to DB
        /*
        createCategoriesTable(con);

        */
        createPersonTable(con, addedByUser);
        //createPersonHasCategory(con);
        //createPersonLabelsTable(con);

        return true;
    }

    private void createPersonHasCategory(Connection con) throws AtlasServerException {
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE person_has_category SELECT");
        sb.append(" person.person_ID,");
        sb.append(" category.category_ID,");
        sb.append(" FROM");
        sb.append(" tempCategoryTable" + timestamp + " tc_t,");
        sb.append(" category, person,");
        sb.append(" WHERE");
        sb.append(" category.categoryName = tc_t.category_name");
        sb.append(" AND tc_t.yago_person_id = person.yago_ID");
        try (Statement stmt = con.createStatement()) {
            System.out.println("actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into 'person' table");
        }
    }

    private void dropTable(Connection con, String yagoToGeoTableName) throws AtlasServerException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("DROP TABLE " + yagoToGeoTableName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Could not drop temp table");
        } finally {
            safelyClose(stmt);
        }
    }

    private void createCategoriesTable(Connection con) throws AtlasServerException {
        try (PreparedStatement pstmt = con
                .prepareStatement("REPLACE INTO category(category_ID,categoryName) VALUES(?,?)")) {
            Pattern p = Pattern.compile(YagoParser.CATEGORY_REGEX);
            int i = 0;
            for (String categoryType : YagoParser.categoryTypes) {
                Matcher m = p.matcher(categoryType);
                if (m.find()) {
                    pstmt.setInt(1, i++);
                    pstmt.setString(2, m.group(1));
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into `categories` table");
        }
    }

    private void createPersonLabelsTable(Connection con) throws AtlasServerException {
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE person_labels SELECT");
        sb.append(" label, person_ID");
        sb.append(" FROM tempLabelsTable" + timestamp + " tl_t, person");
        sb.append(" WHERE person.yago_ID=tl_t.yago_person_id");
        try (Statement stmt = con.createStatement()) {
            System.out.println("actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into 'person_labels' table");
        }
    }

    private void createTempLifetimeTable(Connection con) throws AtlasServerException {
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO tempLifetimeTable" + timestamp + " (yago_person_id, bornOnDate, diedOnDate, bornIn, diedIn) VALUES(?,?,?,?,?)")) {
            int i = 0;
            for (long yagoId : personsMap.keySet()) {
                YagoPerson yagoPerson = personsMap.get(yagoId);
                Long bornInLocation = yagoPerson.getBornInLocation();
                Long diedInLocation = yagoPerson.getDiedInLocation();
                java.sql.Date bornOnDate = yagoPerson.getBornOnDate();
                java.sql.Date diedOnDate = yagoPerson.getDiedOnDate();

                if (bornInLocation == null || bornOnDate == null) {
                    continue;
                }

                pstmt.setLong(1, yagoId);

                pstmt.setDate(2, bornOnDate);

                if (diedOnDate == null) {
                    pstmt.setNull(3, java.sql.Types.DATE);
                } else {
                    pstmt.setDate(3, diedOnDate);
                }

                pstmt.setLong(4, bornInLocation);

                if (diedInLocation == null) {
                    pstmt.setNull(5, java.sql.Types.BIGINT);
                } else {
                    pstmt.setLong(5, diedInLocation);
                }

                pstmt.addBatch();

                if (++i % 10000 == 0) {
                    System.out.println("record #" + i);
                }
            }
            pstmt.executeBatch();
            pstmt.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into 'lifetime' temp table");
        }
    }

    private void createPersonTable(Connection con, int addedByUser) throws AtlasServerException {
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO person(wikiURL, diedOnDate, wasBornOnDate, addedByUser, wasBornInLocation, diedInLocation, isFemale, yago_ID, prefLabel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (YagoPerson yagoPerson : yagoParser.getPersonsMap().values()) {
                String wikiUrl = yagoPerson.getWikiUrl();
                if (wikiUrl == null) {
                    pstmt.setNull(1, java.sql.Types.VARCHAR);
                } else {
                    pstmt.setString(1, wikiUrl);
                }

                Date diedOnDate = yagoPerson.getDiedOnDate();
                if (diedOnDate == null) {
                    pstmt.setNull(2, java.sql.Types.DATE);
                } else {
                    pstmt.setDate(2, diedOnDate);
                }

                pstmt.setDate(3, yagoPerson.getBornOnDate());

                pstmt.setInt(4, addedByUser);

                pstmt.setLong(5, yagoPerson.getBornInLocation());

                Long diedInLocation = yagoPerson.getDiedInLocation();
                if (diedInLocation == null) {
                    pstmt.setNull(6, java.sql.Types.BIGINT);
                } else {
                    pstmt.setLong(6, diedInLocation);
                }

                pstmt.setBoolean(7, yagoPerson.isFemale());
                pstmt.setLong(8, yagoPerson.getYagoId());
                pstmt.setString(9, yagoPerson.getPrefLabel());

                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create 'person' table");
        }
    }

    private void createPersonTableOld(Connection con, int addedByUser) throws AtlasServerException {
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE person SELECT");
        sb.append(" gender_t.person_ID,");
        sb.append(" wiki_t.wikiURL,");
        sb.append(" died_on_t.diedOnDate,");
        sb.append(" born_on_t.bornOnDate,");
        sb.append(" " + addedByUser + ",");
        sb.append(" born_in_t.wasBornInLocation,");
        sb.append(" died_in_t.diedInLocation,");
        sb.append(" gender_t.is_female,");
        sb.append(" gender_t.yago_person_id,");
        sb.append(" labels_t.pref_label");
        sb.append(" FROM");
        sb.append(" tempWikiTable" + timestamp + " wiki_t,");
        sb.append(" tempBornInTable" + timestamp + " born_in_t,");
        sb.append(" tempBornOnTable" + timestamp + " born_on_t,");
        sb.append(" tempDiedOnTable" + timestamp + " died_on_t,");
        sb.append(" tempDiedInTable" + timestamp + " died_in_t,");
        sb.append(" tempGenderTable" + timestamp + " gender_t");
        sb.append(" tempPrefLabelsTable" + timestamp + " labels_t");
        sb.append(" WHERE");
        sb.append(" died_in_t.yago_person_id = wiki_t.yago_id");
        sb.append(" AND died_in_t.yago_person_id = born_in_t.yago_person_id");
        sb.append(" AND died_in_t.yago_person_id = born_on_t.yago_person_id");
        sb.append(" AND died_in_t.yago_person_id = died_on_t.yago_person_id");
        sb.append(" AND died_in_t.yago_person_id = died_in_t.yago_person_id");
        sb.append(" AND died_in_t.yago_person_id = gender_t.yago_person_id");
        sb.append(" AND died_in_t.yago_person_id = labels_t.yago_person_id");
        try (Statement stmt = con.createStatement()) {
            System.out.println("actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into 'person' table");
        }
    }

    private int addYagoUser(Connection con) throws AtlasServerException {
        int newUserId;
        ResultSet rs = null;
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT IGNORE INTO user(username, password, wasBornInLocation) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "YAGO");
            pstmt.setString(2, "yagohasnopassword");
            long locationId = yagoParser.getLocationsMap().entrySet().iterator().next().getValue().getLocationId();
            pstmt.setLong(3, locationId);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            rs.next();
            newUserId = rs.getInt(1);
            System.out.println("YAGO user created with id=" + newUserId);
            return newUserId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create YAGO user");
        } finally {
            safelyClose(rs);
        }
    }

    private void createLocationTable(Connection con) throws AtlasServerException {
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE location SELECT");
        sb.append(" gct.location, gct.latitude, gct.longitude, wikit.wikiUrl, gct.geo_id, git.yago_id");
        sb.append(" FROM tempGeoInfoTable" + timestamp + " git, tempGeoCitiesTable" + timestamp + " gct, tempWikiTable" + timestamp + " wikit");
        sb.append(" WHERE gct.geo_id=git.geo_id AND git.yago_id=wikit.yago_id");
        try (Statement stmt = con.createStatement()) {
            System.out.println("actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into 'location' table");
        }
    }

    private void createLocationsTable(Connection con) throws AtlasServerException {
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO location(geo_name, latitude, longitude, wikiURL, location_ID) VALUES (?, ?, ?, ?, ?)")) {
            for (YagoLocation yagoLocation : yagoParser.getLocationsMap().values()) {
                pstmt.setString(1, yagoLocation.getName());
                pstmt.setDouble(2, yagoLocation.getLatitude());
                pstmt.setDouble(3, yagoLocation.getLongitude());
                pstmt.setString(4, yagoLocation.getWikiUrl());
                pstmt.setLong(5, yagoLocation.getLocationId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create 'location' table");
        }
    }

    private void createTempTables(Connection con) throws AtlasServerException {
        /*
        LinkedHashMap<String, TempTableMetadata> tempFields = TempTablesConstants.tempFields;
        for (String tableName : tempFields.keySet()) {
            TempTableMetadata tempTableMetadata = tempFields.get(tableName);
            createEmptyTempTable(con, tableName + timestamp, tempTableMetadata.getFields());
            loadDataIntoTempTable(con, tableName + timestamp, concatToOutPath(tempTableMetadata.getDataFilePath()));
        }*/ // TODO: uncomment

        // create empty tempLifetimeTable
        LinkedHashMap<String, String> tempLifetimeFields = TempTablesConstants.tempLifetimeFields;
        createEmptyTempTable(con, "tempLifetimeTable" + timestamp, tempLifetimeFields);
        // populate it
        createTempLifetimeTable(con);
    }

    private void loadDataIntoTempTable(Connection con, String tableName, String dataFilePath) throws AtlasServerException {
        System.out.println("populating temp table " + tableName);
        StringBuilder sb = new StringBuilder();
        sb.append("LOAD DATA LOCAL INFILE '" + dataFilePath + "'IGNORE INTO TABLE " + tableName + " FIELDS TERMINATED BY '\\t'");
        try (Statement stmt = con.createStatement()) {
            System.out.println("Actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to load into temp table '" + tableName + "'");
        }
    }

    private void createEmptyTempTable(Connection con, String tableName, LinkedHashMap<String, String> fields) throws AtlasServerException {
        System.out.println("Creating empty temp table " + tableName);
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + tableName);
        sb.append(schemaToString(fields));
        try (Statement stmt = con.createStatement()) {
            System.out.println("actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create temp table '" + tableName + "'");
        }
    }


    // last entry should be the primary key, e.g: PRIMARY KEY geo_ID
    private String schemaToString(Map<String, String> schema) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String name : schema.keySet()) {
            String type = schema.get(name);
            sb.append(name);
            sb.append(" ");
            sb.append(type);
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    public static void main(String[] args) throws AtlasServerException {
        DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06","localhost", "3305", "DbMysql06");
        ParseFilesCommand cmd = new ParseFilesCommand(new File("/Users/admin/Downloads/yagoDateFacts.tsv"),
                new File("/Users/admin/Downloads/yagoFacts.tsv"),
                new File("/Users/admin/Downloads/yagoTransitiveType.tsv"),
                new File("/Users/admin/Downloads/yagoLabels.tsv"),
                new File("/Users/admin/Downloads/yagoWikipediaInfo.tsv"),
                new File("/Users/admin/Downloads/yagoGeonamesEntityIds.tsv"),
                new File("/Users/admin/Downloads/cities1000.txt"),
                "/Users/admin/Downloads/Test",
                new AtomicInteger(0));
        cmd.execute();
    }

    private String concatToOutPath(String fileName) {
        return parserOutputPath + File.separator + fileName;
    }
}
