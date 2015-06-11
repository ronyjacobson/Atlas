package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.State;
import il.ac.tau.cs.databases.atlas.connector.ConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.TempTableMetadata;
import il.ac.tau.cs.databases.atlas.db.TempTablesConstants;
import il.ac.tau.cs.databases.atlas.db.YagoParser;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
        this.timestamp = System.currentTimeMillis();
    }


    @Override
    protected Boolean innerExecute(Connection con) throws AtlasServerException {
        // GUI should check if files exist
        YagoParser yagoParser = new YagoParser(yagoDateFile, yagoLocationFile, yagoCategoryFile, yagoLabelsFile, yagoWikiFile, yagoGeonamesFile, geonamesCitiesFile, parserOutputPath);

        /*
        try {
            yagoParser.parseFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        progress.getAndIncrement(); // Increment progress for progress bar

        // parse yago datafile into temp tsv
        createTempTables(con);
        // create empty temp table

        // load parsed tsv file into temp table and remove it
        //loadDataIntoTempTable(con, "category_temp_table", concatToOutPath(YagoParser.CATEGORIES_INFO_OUT_NAME));

        // create final tables if not exists

        // combine temp tables into final tables (transaction?)

        // create temporary table to connect yago places and geonames
        /*final long timeOfInitiation = System.currentTimeMillis();
        String yagoToGeoTableName = "temp_yago_to_geo" + timeOfInitiation;
        createGeoToYagoTempTable(con, yagoToGeoTableName);
        progress.getAndIncrement();

        // Insert data table to DB

        insertLocations(con, yagoToGeoTableName);
        dropTable(con, yagoToGeoTableName);
        progress.getAndIncrement();
        */
        return true;
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

    private void createGeoToYagoTempTable(Connection con, String tempTableName) throws AtlasServerException {
        String pathToParsedGeoInfo = concatToOutPath(YagoParser.GEO_INFO_OUT_NAME);
        BufferedReader br = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + tempTableName +"(" +
                    "yago_ID VARCHAR(100) NOT NULL, " +
                    "geo_ID INT NOT NULL, " +
                    "PRIMARY KEY geo_ID" +
                    ")");
            pstmt.executeUpdate();
            System.out.println(tempTableName + " is created");
            pstmt = con.prepareStatement("LOAD DATA LOCAL INFILE `?` INTO TABLE `?` FIELDS TERMINATED BY `\\t`");
            pstmt.setString(1, pathToParsedGeoInfo);
            pstmt.setString(2, tempTableName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create GeoToYago temporary table");
        } finally {
            safelyClose(pstmt);
        }
    }

    private void insertLocations(Connection con, String yagoToGeoTableName) throws AtlasServerException {
        File parsedGeoCitiesInfo = new File(concatToOutPath(YagoParser.GEO_CITIES_INFO_OUT_NAME));
        BufferedReader br = null;
        // Replace into so that rows will be updated or created if not exist
        try (PreparedStatement pstmt = con.prepareStatement(
                "REPLACE INTO `templocation`" +
                "SET `geo_name` = ?, " +
                "`latitude` = ?, " +
                "`longtitude` = ?, " +
                "`wikiURL` = ?, " +
                "`geo_ID` = ?, " +
                "`yago_ID` = ?")) {

            //con.setAutoCommit(false); // make this into a transaction
            br = new BufferedReader(new FileReader(parsedGeoCitiesInfo));
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.trim().split("\\t");
                pstmt.setString(1, cols[2]);
                pstmt.setDouble(2, Double.parseDouble(cols[3]));
                pstmt.setDouble(3, Double.parseDouble(cols[4]));
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setInt(5, Integer.parseInt(cols[0]));
                pstmt.setNull(6, Types.VARCHAR); // FIXME: this column should be nullable! After all of the insertions, the temp table and this table should be unified
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            // TODO: unify temp table with this table


            //con.commit();
            System.out.println("The table `location` has been filled!");

        } catch(SQLException | IOException e){
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert locations");
        } finally{
            safelyClose(br);
        }
    }

    private void createTempTables(Connection con) throws AtlasServerException {
        LinkedHashMap<String, TempTableMetadata> tempFields = TempTablesConstants.tempFields;
        int i = 0;
        for (String tableName : tempFields.keySet()) {
            if (i++ < 5) {
                continue;
            }
            // TODO: revert
            TempTableMetadata tempTableMetadata = tempFields.get(tableName);
            createEmptyTempTable(con, tableName + "1433944465995", tempTableMetadata.getFields());
            loadDataIntoTempTable(con, tableName + "1433944465995", concatToOutPath(tempTableMetadata.getDataFilePath()));
        }
    }

    private void loadDataIntoTempTable(Connection con, String tableName, String dataFilePath) throws AtlasServerException {
        System.out.println("populating temp table " + tableName);
        /*try (PreparedStatement pstmt = con.prepareStatement(
                "LOAD DATA LOCAL INFILE ? REPLACE INTO TABLE ? FIELDS TERMINATED BY '\\t'")) {
            /*
            pstmt = con.prepareStatement("LOAD DATA INFILE '" + dataFilePath +
                    "' REPLACE INTO TABLE " + tableName + " FIELDS TERMINATED BY '\\t'");
            pstmt.executeUpdate();

            pstmt.setString(1, dataFilePath);
            pstmt.setString(2, tableName);
            System.out.println(pstmt.toString());
            pstmt.executeUpdate();

        }
        */
        StringBuilder sb = new StringBuilder();
        sb.append("LOAD DATA LOCAL INFILE  '" + dataFilePath + "'IGNORE INTO TABLE " + tableName + " FIELDS TERMINATED BY '\\t'");
        try (Statement stmt = con.createStatement()) {
            System.out.println("actual CMD is: " + sb.toString());
            stmt.execute(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to load into temp table '" + tableName + "'");
        }
    }

    private void createEmptyTempTable(Connection con, String tableName, LinkedHashMap<String, String> fields) throws AtlasServerException {
        System.out.println("creating empty temp table " + tableName);
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
