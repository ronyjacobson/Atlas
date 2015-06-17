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
            yagoParser.parseFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //progress.getAndIncrement(); // Increment progress for progress bar


        // createTempTables(con);

        //progress.getAndIncrement();

        createLocationsTable(con);
        int addedByUser = addYagoUser(con); // TODO: should only happen in the initial setup
        // Insert data table to DB

        createCategoriesTable(con);
        createPersonTable(con, addedByUser);
        createPersonHasCategoryTable(con);
        createPersonLabelsTable(con);

        return true;
    }

    private void createCategoriesTable(Connection con) throws AtlasServerException {
        logger.info("Creating 'categories' table");
        try (PreparedStatement pstmt = con
                .prepareStatement("REPLACE INTO category(category_ID,categoryName) VALUES(?,?)")) {
            Pattern p = Pattern.compile(YagoParser.CATEGORY_REGEX);
            for (Entry<String, Integer> categoryEntry : YagoParser.categoryTypes.entrySet()) {
                Matcher m = p.matcher(categoryEntry.getKey());
                if (m.find()) {
                    pstmt.setInt(1, categoryEntry.getValue());
                    pstmt.setString(2, m.group(1));
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert into `categories` table");
        }
        logger.info("'categories' table created successfully");
    }

    private void createPersonLabelsTable(Connection con) throws AtlasServerException {
        logger.info("Creating 'person_labels' table");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO person_labels(label, person_ID) VALUES (?, ?)")) {
            for (YagoPerson yagoPerson : personsMap.values()) {
                int personId = yagoPerson.getPersonId();
                for (String label : yagoPerson.getLabels()) {
                    pstmt.setString(1, label);
                    pstmt.setInt(2, personId);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create 'person_labels' table");
        }
        logger.info("'person_labels' table created successfully");
    }

    private void createPersonHasCategoryTable(Connection con) throws AtlasServerException {
        logger.info("Creating 'person_has_category' table");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO person_has_category(person_ID, category_ID) VALUES (?, ?)")) {
            for (YagoPerson yagoPerson : personsMap.values()) {
                int personId = yagoPerson.getPersonId();
                for (Integer categoryId : yagoPerson.getCategories()) {
                    pstmt.setInt(1, personId);
                    pstmt.setInt(2, categoryId);
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create 'person_has_category' table");
        }
        logger.info("'person_has_category' table created successfully");
    }

    private void createPersonTable(Connection con, int addedByUser) throws AtlasServerException {
        logger.info("Creating 'person' table");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        logger.info("Total number of records to be inserted: " + personsMap.size() + " persons");

        ResultSet rs = null;

        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO person(wikiURL, diedOnDate, wasBornOnDate, addedByUser, wasBornInLocation, diedInLocation, isFemale, yago_ID, prefLabel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE wikiURL=VALUES(wikiURL), diedOnDate=VALUES(diedOnDate), wasBornOnDate=VALUES(wasBornOnDate), addedByUser=VALUES(addedByUser), wasBornInLocation=VALUES(wasBornInLocation), diedInLocation=VALUES(diedInLocation), isFemale=VALUES(isFemale), prefLabel=VALUES(prefLabel)",
                        Statement.RETURN_GENERATED_KEYS)) {
            for (YagoPerson yagoPerson : personsMap.values()) {
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

                pstmt.executeUpdate();
                rs = pstmt.getGeneratedKeys();
                rs.next();
                int personId = rs.getInt(1);
                yagoPerson.setPersonId(personId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create 'person' table");
        } finally {
            safelyClose(rs);
        }
        logger.info("'person' table created successfully");
    }

    private int addYagoUser(Connection con) throws AtlasServerException {
        logger.info("creating initial YAGO user for adding all Yago persons");
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
            logger.info("YAGO user created with id=" + newUserId);
            return newUserId;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create YAGO user");
        } finally {
            safelyClose(rs);
        }
    }

    private void createLocationsTable(Connection con) throws AtlasServerException {
        logger.info("Creating 'location' table");
        Map<Long, YagoLocation> locationsMap = yagoParser.getLocationsMap();
        logger.info("Total number of records to be inserted: " + locationsMap.size() + " locations");
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO location(geo_name, latitude, longitude, wikiURL, location_ID) VALUES (?, ?, ?, ?, ?)")) {
            for (YagoLocation yagoLocation : locationsMap.values()) {
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
        logger.info("'location' table created successfully");
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
}
