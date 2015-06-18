package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.ParserConstants;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseProgressDBCommand;
import il.ac.tau.cs.databases.atlas.db.YagoParser;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.parsing.YagoLocation;
import il.ac.tau.cs.databases.atlas.parsing.YagoPerson;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseFilesCommand extends BaseProgressDBCommand {
    private YagoParser yagoParser;
    private Map<String, File> files;

    public ParseFilesCommand(Map<String, File> files) {
        yagoParser = new YagoParser();
        this.files = files;
    }

    @Override
    protected String getDisplayLabel() {
        return "Press 'Start' to begin";
    }

    @Override
    protected String getFrameLabel() {
        return "Yago Updater";
    }

    @Override
    protected void runProgressCmd(Connection con) throws AtlasServerException {

        yagoParser.setProgressUpdater(progressUpdater);
        try {
            logger.info("Parser started");
            int numberOfSteps = 12;
            int step = 0;
            progressLogger(++step, numberOfSteps, "Parsing YAGO literal facts info..");
            yagoParser.parseYagoLiteralFacts(files.get(ParserConstants.YAGO_LITERAL_FACTS_TSV));
            progressLogger(++step, numberOfSteps, "Parsing YAGO labels for locations..");
            yagoParser.parseYagoLabelsFile(files.get(ParserConstants.YAGO_LABELS_TSV), true);
            progressLogger(++step, numberOfSteps, "Parsing YAGO geonames info..");
            yagoParser.parseYagoGeonamesFile(files.get(ParserConstants.YAGO_GEONAMES_ENTITY_IDS_TSV));
            progressLogger(++step, numberOfSteps, "Parsing Geonames cities info..");
            yagoParser.parseGeonamesCitiesFile(files.get(ParserConstants.CITIES1000_TXT));
            progressLogger(++step, numberOfSteps, "Validating locations..");
            yagoParser.validateLocationsMap();
            progressLogger(++step, numberOfSteps, "Parsing YAGO dates..");
            yagoParser.parseYagoDateFile(files.get(ParserConstants.YAGO_DATE_FACTS_TSV));
            progressLogger(++step, numberOfSteps, "Parsing YAGO locations facts..");
            yagoParser.parseYagoLocationFile(files.get(ParserConstants.YAGO_FACTS_TSV));
            progressLogger(++step, numberOfSteps, "Filtering out persons without birth date/place..");
            yagoParser.validatePersonsMap();
            progressLogger(++step, numberOfSteps, "Parsing YAGO categories..");
            yagoParser.parseYagoCategoryFile(files.get(ParserConstants.YAGO_TRANSITIVE_TYPE_TSV));
            progressLogger(++step, numberOfSteps, "Parsing YAGO labels for persons..");
            yagoParser.parseYagoLabelsFile(files.get(ParserConstants.YAGO_LABELS_TSV), false);
            progressLogger(++step, numberOfSteps, "Parsing YAGO wikipedia info..");
            yagoParser.parseYagoWikiFile(files.get(ParserConstants.YAGO_WIKIPEDIA_INFO_TSV));
            progressLogger(++step, numberOfSteps, "Ensuring labels..");
            yagoParser.ensureLabels(); // remove persons without prefLabel or no labels at all
            logger.info("Parsing complete");
        } catch (IOException e) {
            logger.error("", e);
            throw new AtlasServerException(e.getMessage());
        }

        createLocationsTable(con);
        int addedByUser = addYagoUser(con);
        createCategoriesTable(con);
        createPersonTable(con, addedByUser);
        createPersonHasCategoryTable(con);
        createPersonLabelsTable(con);
    }

    private void progressLogger(int step, int numberOfSteps, String msg) {
        String outMsg = "Step " + step + "/" + numberOfSteps + ": " + msg;
        progressLogger(outMsg);
    }

    private void progressLogger(String msg) {
        progressUpdater.resetProgress();
        logger.info(msg);
        progressUpdater.updateHeader(msg);
    }

    public void updateProgress(long numberOfRecords, long recordNumber) {
        progressUpdater.updateProgress((int) (recordNumber * 100 / numberOfRecords), recordNumber + "/" + numberOfRecords + " processed");
    }

    private void createCategoriesTable(Connection con) throws AtlasServerException {
        progressLogger("Populating 'categories' table");
        progressUpdater.updateProgress(50, "Creating categories in DB");
        try (PreparedStatement pstmt = con
                .prepareStatement("REPLACE INTO category(category_ID,categoryName) VALUES(?,?)")) {
            Pattern p = Pattern.compile(YagoParser.CATEGORY_REGEX);
            for (Entry<String, Integer> categoryEntry : yagoParser.getCategoryTypes().entrySet()) {
                Matcher m = p.matcher(categoryEntry.getKey());
                if (m.find()) {
                    pstmt.setInt(1, categoryEntry.getValue());
                    pstmt.setString(2, m.group(1));
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Failed to populate 'categories' table", e);
            throw new AtlasServerException("Failed to populate `categories` table");
        }
        logger.info("'categories' table populated successfully");
    }

    private void createPersonLabelsTable(Connection con) throws AtlasServerException {
        progressLogger("Populating 'person_labels' table");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        progressUpdater.updateProgress(50, "Updating labels in DB");
        try (PreparedStatement pstmt = con
                .prepareStatement("REPLACE INTO person_labels(label, person_ID) VALUES (?, ?)")) {
            for (YagoPerson yagoPerson : personsMap.values()) {
                int personId = yagoPerson.getPersonId();
                for (String label : yagoPerson.getLabels()) {
                    pstmt.setString(1, label);
                    pstmt.setInt(2, personId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Failed to populate 'person_labels' table", e);
            throw new AtlasServerException("Failed to populate 'person_labels' table");
        }
        logger.info("'person_labels' table populated successfully");
    }

    private void createPersonHasCategoryTable(Connection con) throws AtlasServerException {
        progressLogger("Populating 'person_has_category' table");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        progressUpdater.updateProgress(50, "Updating categories in DB");
        try (PreparedStatement pstmt = con
                .prepareStatement("REPLACE INTO person_has_category(person_ID, category_ID) VALUES (?, ?)")) {
            for (YagoPerson yagoPerson : personsMap.values()) {
                int personId = yagoPerson.getPersonId();
                for (Integer categoryId : yagoPerson.getCategories()) {
                    pstmt.setInt(1, personId);
                    pstmt.setInt(2, categoryId);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Failed to populate 'person_has_category' table", e);
            throw new AtlasServerException("Failed to populate 'person_has_category' table");
        }
        logger.info("'person_has_category' table populated successfully");
    }

    private void createPersonTable(Connection con, int addedByUser) throws AtlasServerException {
        progressLogger("Populating 'person' table");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        logger.info("Total number of records to be inserted: " + personsMap.size() + " persons");

        ResultSet rs = null;
        Statement stmt = null;

        progressUpdater.updateProgress(0, "Uploading persons..");
        long interval = personsMap.size() / 100;
        long recordCount = 0;

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
                if (! rs.next()) {
                    stmt = con.createStatement();
                    rs = stmt.executeQuery("SELECT person_ID FROM person WHERE yago_ID=" + yagoPerson.getYagoId());
                }
                int personId = rs.getInt(1);
                yagoPerson.setPersonId(personId);
                if (++recordCount % interval == 0) {
                    updateProgress(personsMap.size(), recordCount);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to populate 'person' table", e);
            throw new AtlasServerException("Failed to populate 'person' table");
        } finally {
            safelyClose(rs, stmt);
        }
        logger.info("'person' table populated successfully");
    }

    private int addYagoUser(Connection con) throws AtlasServerException {
        logger.info("Setting YAGO user for adding all Yago persons");
        int newUserId;
        ResultSet rs = null;
        try (PreparedStatement pstmt = con
                .prepareStatement("INSERT INTO user(username, password, wasBornInLocation, isFemale) VALUES (?, ?, ?, 0) ON DUPLICATE KEY UPDATE username=VALUES(username), password=VALUES(password), wasBornInLocation=VALUES(wasBornInLocation), isFemale=1-isFemale",
                        Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "YAGO");
            pstmt.setString(2, "yagohasnopassword");
            long locationId = yagoParser.getLocationsMap().entrySet().iterator().next().getValue().getLocationId();
            pstmt.setLong(3, locationId);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            rs.next();
            newUserId = rs.getInt(1);
            logger.info("YAGO user id=" + newUserId);
            return newUserId;
        } catch (SQLException e) {
            logger.error("Failed to create YAGO user", e);
            throw new AtlasServerException("Failed to create YAGO user");
        } finally {
            safelyClose(rs);
        }
    }

    private void createLocationsTable(Connection con) throws AtlasServerException {
        progressLogger("Populating 'location' table");
        progressUpdater.updateProgress(50, "Updating locations in DB");
        Map<Long, YagoLocation> locationsMap = yagoParser.getLocationsMap();
        logger.info("Total number of records to be inserted: " + locationsMap.size() + " locations");
        try (PreparedStatement pstmt = con
                .prepareStatement("REPLACE INTO location(geo_name, latitude, longitude, wikiURL, location_ID) VALUES (?, ?, ?, ?, ?)")) {
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
            logger.error("Failed to populate 'location' table", e);
            throw new AtlasServerException("Failed to populate 'location' table");
        }
        logger.info("'location' table populated successfully");
    }

    public static void main(String[] args) throws AtlasServerException {
        DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06","localhost", "3306", "DbMysql06");
        Map<String, File> files = new HashMap<>();
        files.put(ParserConstants.YAGO_DATE_FACTS_TSV, new File("/Users/admin/Downloads/yagoDateFacts.tsv"));
        files.put(ParserConstants.YAGO_LITERAL_FACTS_TSV, new File("/Users/admin/Downloads/yagoLiteralFacts.tsv"));
        files.put(ParserConstants.YAGO_LABELS_TSV, new File("/Users/admin/Downloads/yagoLabels.tsv"));
        files.put(ParserConstants.YAGO_FACTS_TSV, new File("/Users/admin/Downloads/yagoFacts.tsv"));
        files.put(ParserConstants.YAGO_TRANSITIVE_TYPE_TSV, new File("/Users/admin/Downloads/yagoTransitiveType.tsv"));
        files.put(ParserConstants.YAGO_WIKIPEDIA_INFO_TSV, new File("/Users/admin/Downloads/yagoWikipediaInfo.tsv"));
        files.put(ParserConstants.YAGO_GEONAMES_ENTITY_IDS_TSV, new File("/Users/admin/Downloads/yagoGeonamesEntityIds.tsv"));
        files.put(ParserConstants.CITIES1000_TXT, new File("/Users/admin/Downloads/cities1000.txt"));
        new ParseFilesCommand(files).execute();
    }
}
