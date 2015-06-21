package il.ac.tau.cs.databases.atlas.db.command;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.command.base.BaseProgressDBCommand;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.db.connection.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.parsing.ParserConstants;
import il.ac.tau.cs.databases.atlas.parsing.YagoLocation;
import il.ac.tau.cs.databases.atlas.parsing.YagoParser;
import il.ac.tau.cs.databases.atlas.parsing.YagoPerson;
import il.ac.tau.cs.databases.atlas.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    protected String getSuccessMessage() {
        return "Update finished, please restart the program to sync with the update.";
    }

    @Override
    protected void runProgressCmd(Connection con) throws AtlasServerException {

        yagoParser.setProgressUpdater(progressUpdater);
        try {
            logger.info("Parser started");
            int numberOfSteps = 12;
            int step = 0;
            progressLogger(++step, numberOfSteps, "Parsing YAGO literal facts info ..");
            yagoParser.parseYagoLiteralFacts(files.get(ParserConstants.YAGO_LITERAL_FACTS_TSV));
            progressLogger(++step, numberOfSteps, "Parsing YAGO labels for locations ..");
            yagoParser.parseYagoLabelsFile(files.get(ParserConstants.YAGO_LABELS_TSV), true);
            progressLogger(++step, numberOfSteps, "Parsing YAGO geonames info ..");
            yagoParser.parseYagoGeonamesFile(files.get(ParserConstants.YAGO_GEONAMES_ENTITY_IDS_TSV));
            progressLogger(++step, numberOfSteps, "Parsing Geonames cities info ..");
            yagoParser.parseGeonamesCitiesFile(files.get(ParserConstants.CITIES1000_TXT));
            progressLogger(++step, numberOfSteps, "Filtering out locations with incomplete records ..");
            yagoParser.validateLocationsMap();
            progressLogger(++step, numberOfSteps, "Parsing YAGO dates ..");
            yagoParser.parseYagoDateFile(files.get(ParserConstants.YAGO_DATE_FACTS_TSV));
            progressLogger(++step, numberOfSteps, "Parsing YAGO locations facts ..");
            yagoParser.parseYagoLocationFile(files.get(ParserConstants.YAGO_FACTS_TSV));
            progressLogger(++step, numberOfSteps, "Filtering out persons without birth date/place ..");
            yagoParser.validatePersonsMap();
            progressLogger(++step, numberOfSteps, "Filtering out unused locations ..");
            yagoParser.removeUnusedLocation();
            progressLogger(++step, numberOfSteps, "Parsing YAGO categories ..");
            yagoParser.parseYagoCategoryFile(files.get(ParserConstants.YAGO_TRANSITIVE_TYPE_TSV));
            progressLogger(++step, numberOfSteps, "Parsing YAGO labels for persons ..");
            yagoParser.parseYagoLabelsFile(files.get(ParserConstants.YAGO_LABELS_TSV), false);
            progressLogger(++step, numberOfSteps, "Parsing YAGO wikipedia info ..");
            yagoParser.parseYagoWikiFile(files.get(ParserConstants.YAGO_WIKIPEDIA_INFO_TSV));
            progressLogger(++step, numberOfSteps, "Filtering out persons without prefLabel or no labels at all ..");
            yagoParser.ensureCategoriesLabelsAndLocations();
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

    private void progressLogger(int step, int numberOfSteps, String msg) throws AtlasServerException {
        String outMsg = "Step " + step + "/" + numberOfSteps + ": " + msg;
        progressLogger(outMsg);
    }

    private void progressLogger(String msg) throws AtlasServerException {
        progressUpdater.resetProgress();
        logger.info(msg);
        progressUpdater.updateHeader(msg);
    }

    public void updateProgress(long numberOfRecords, long recordNumber) throws AtlasServerException {
        progressUpdater.updateProgress((int) (recordNumber * 100 / numberOfRecords), recordNumber + "/" + numberOfRecords + " processed");
    }

    private void createCategoriesTable(Connection con) throws AtlasServerException {
        progressLogger("Populating '" + DBConstants.Category.TABLE_NAME + "' table (2/5)...");
        progressUpdater.updateProgress(50, "Creating categories in DB ..");
        try (PreparedStatement pstmt = con.prepareStatement(
                getInsertIntoOnDuplicateKeyUpdateSql(
                        DBConstants.Category.TABLE_NAME,
                        DBConstants.Category.FIELDS_LIST))) {
            Pattern p = Pattern.compile(ParserConstants.CATEGORY_REGEX);
            for (Entry<String, Integer> categoryEntry : ParserConstants.CATEGORY_TYPES.entrySet()) {
                Matcher m = p.matcher(categoryEntry.getKey());
                if (m.find()) {
                    pstmt.setInt(1, categoryEntry.getValue());
                    pstmt.setString(2, prettifyCategoryName(m.group(1)));
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            logger.error("Failed to populate '" + DBConstants.Category.TABLE_NAME + "' table", e);
            throw new AtlasServerException("Failed to populate '" + DBConstants.Category.TABLE_NAME + "' table");
        }
        logger.info("'" + DBConstants.Category.TABLE_NAME + "' table populated successfully");
    }

    private String prettifyCategoryName(String cat) {
        String[] words = cat.split("_");
        String output = "";
        for (String word : words) {
            output += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
        }
        return output.trim();
    }

    private void createPersonLabelsTable(Connection con) throws AtlasServerException {
        progressLogger("Populating '" + DBConstants.PersonLabels.TABLE_NAME + "' table (5/5)...");
        progressUpdater.updateProgress(50, "Updating labels in DB ..");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        try (PreparedStatement pstmt = con.prepareStatement(
                getInsertIntoOnDuplicateKeyUpdateSql(
                        DBConstants.PersonLabels.TABLE_NAME,
                        DBConstants.PersonLabels.FIELDS_LIST))) {
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
            logger.error("Failed to populate '" + DBConstants.PersonLabels.TABLE_NAME + "' table", e);
            throw new AtlasServerException("Failed to populate '" + DBConstants.PersonLabels.TABLE_NAME + "' table");
        }
        logger.info("'" + DBConstants.PersonLabels.TABLE_NAME + "' table populated successfully");
    }

    private void createPersonHasCategoryTable(Connection con) throws AtlasServerException {
        progressLogger("Populating '" + DBConstants.PersonHasCategory.TABLE_NAME + "' table (4/5)...");
        progressUpdater.updateProgress(50, "Updating categories in DB ..");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        try (PreparedStatement pstmt = con.prepareStatement(
                getInsertIntoOnDuplicateKeyUpdateSql(
                        DBConstants.PersonHasCategory.TABLE_NAME,
                        DBConstants.PersonHasCategory.FIELDS_LIST))) {
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
            logger.error("Failed to populate '" + DBConstants.PersonHasCategory.TABLE_NAME + "' table", e);
            throw new AtlasServerException("Failed to populate '" + DBConstants.PersonHasCategory.TABLE_NAME + "' table");
        }
        logger.info("'" + DBConstants.PersonHasCategory.TABLE_NAME + "' table populated successfully");
    }

    private void createPersonTable(Connection con, int addedByUser) throws AtlasServerException {
        progressLogger("Populating '" + DBConstants.Person.TABLE_NAME + "' table (3/5)...");
        Map<Long, YagoPerson> personsMap = yagoParser.getPersonsMap();
        logger.info("Total number of records to be inserted: " + personsMap.size() + " persons");
        ResultSet rs = null;
        Statement stmt = null;

        progressUpdater.updateProgress(0, "Uploading persons to DB ..");
        long interval = personsMap.size() / 100;
        long recordCount = 0;

        try (PreparedStatement pstmt = con.prepareStatement(
                "INSERT INTO person(wikiURL, diedOnDate, wasBornOnDate, addedByUser, " +
                        "wasBornInLocation, diedInLocation, isFemale, yago_ID, prefLabel) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                        "wikiURL=VALUES(wikiURL), diedOnDate=VALUES(diedOnDate), " +
                        "wasBornOnDate=VALUES(wasBornOnDate), addedByUser=VALUES(addedByUser), " +
                        "wasBornInLocation=VALUES(wasBornInLocation), diedInLocation=VALUES(diedInLocation), " +
                        "isFemale=VALUES(isFemale), prefLabel=VALUES(prefLabel)",
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
                    rs = stmt.executeQuery("SELECT person_ID FROM " + DBConstants.Person.TABLE_NAME + " WHERE yago_ID=" + yagoPerson.getYagoId());
                    rs.next();
                }
                int personId = rs.getInt(1);
                yagoPerson.setPersonId(personId);
                if (++recordCount % interval == 0) {
                    updateProgress(personsMap.size(), recordCount);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to populate '" + DBConstants.Person.TABLE_NAME + "' table", e);
            throw new AtlasServerException("Failed to populate '" + DBConstants.Person.TABLE_NAME + "' table");
        } finally {
            safelyClose(rs, stmt);
        }
        logger.info("'" + DBConstants.Person.TABLE_NAME + "' table populated successfully");
    }

    private int addYagoUser(Connection con) throws AtlasServerException {
        logger.info("Setting YAGO user for adding all Yago persons ..");
        int newUserId;
        ResultSet rs = null;
        try (PreparedStatement pstmt = con.prepareStatement(
                "INSERT INTO user(username, password, wasBornInLocation, isFemale) " +
                        "VALUES (?, ?, ?, 0) ON DUPLICATE KEY UPDATE " +
                        "username=VALUES(username), password=VALUES(password), " +
                        "wasBornInLocation=VALUES(wasBornInLocation), isFemale=1-isFemale",
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "YAGO");
            pstmt.setString(2, "yagohasnopassword");
            long locationId = yagoParser.getLocationsMap().entrySet().iterator().next().getValue().getLocationId();
            pstmt.setLong(3, locationId);
            logger.info(String.format("Executing DB query: %s.",
                    pstmt.toString()));
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            rs.next();
            newUserId = rs.getInt(1);
            logger.info("Query executed properly, YAGO user id=" + newUserId);
            return newUserId;
        } catch (SQLException e) {
            logger.error("Failed to create YAGO user", e);
            throw new AtlasServerException("Failed to create YAGO user");
        } finally {
            safelyClose(rs);
        }
    }

    private void createLocationsTable(Connection con) throws AtlasServerException {
        progressLogger("Populating '" + DBConstants.Location.TABLE_NAME + "' table (1/5)...");
        progressUpdater.updateProgress(50, "Updating locations in DB ..");
        Map<Long, YagoLocation> locationsMap = yagoParser.getLocationsMap();
        logger.info("Total number of records to be inserted: " + locationsMap.size() + " locations");
        try (PreparedStatement pstmt = con.prepareStatement(
                getInsertIntoOnDuplicateKeyUpdateSql(
                        DBConstants.Location.TABLE_NAME,
                        DBConstants.Location.FIELDS_LIST))) {
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
            logger.error("Failed to populate '" + DBConstants.Location.TABLE_NAME + "' table", e);
            throw new AtlasServerException("Failed to populate '" + DBConstants.Location.TABLE_NAME + "' table");
        }
        logger.info("'" + DBConstants.Location.TABLE_NAME + "' table populated successfully");
    }

    /**
     * @return "INSERT INTO table(f_1,..,f_n) VALUES (?,..,?) ON DUPLICATE KEY UPDATE f_1=VALUE(f_1),..,f_n=VALUE(f_n)"
     */
    private String getInsertIntoOnDuplicateKeyUpdateSql(String tableName, List<String> fields) {
        StringBuilder sb = new StringBuilder("INSERT INTO " + tableName + "(");
        sb.append(StringUtils.concatWithCommas(fields));
        sb.append(") VALUES (");
        sb.append(StringUtils.concatWithCommas("?", fields.size()));
        sb.append(") ON DUPLICATE KEY UPDATE ");
        sb.append(StringUtils.concatValuesWithCommas(fields));
        return sb.toString();
    }

    public static void main(String[] args) throws AtlasServerException {
        DynamicConnectionPool dynamicConnectionPool = new DynamicConnectionPool();
        dynamicConnectionPool.initialize("DbMysql06", "DbMysql06", "localhost", "3306", "DbMysql06");
        ConnectionPoolHolder.INSTANCE.set(dynamicConnectionPool);

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
