package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.YagoParser;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.*;
import java.sql.*;
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
    }


    @Override
    protected Boolean innerExecute(Connection con) throws AtlasServerException {
        // GUI should check if files exist
        YagoParser yagoParser = new YagoParser(yagoDateFile, yagoLocationFile, yagoCategoryFile, yagoLabelsFile, yagoWikiFile, yagoGeonamesFile, geonamesCitiesFile, parserOutputPath);
        try {
            yagoParser.parseFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progress.getAndIncrement(); // Increment progress for progress bar

        // create temporary table to connect yago places and geonames
        final long timeOfInitiation = System.currentTimeMillis();
        String yagoToGeoTableName = "temp_yago_to_geo" + timeOfInitiation;
        createGeoToYagoTempTable(con, yagoToGeoTableName);
        progress.getAndIncrement();

        // Insert data table to DB

        insertLocations(con, yagoToGeoTableName);
        dropTable(con, yagoToGeoTableName);
        progress.getAndIncrement();

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
            pstmt = con.prepareStatement("LOAD DATA LOCAL INFILE ? INTO TABLE ?");
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
                "REPLACE INTO `location`" +
                "SET `geo_name` = ?, " +
                "`latitude` = ?, " +
                "`longtitude` = ?, " +
                "`wikiURL` = ?, " +
                "`geo_ID` = ?, " +
                "`yago_ID` = ?");) {

            con.setAutoCommit(false); // make this into a transaction
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


            con.commit();
            System.out.println("The table `location` has been filled!");

        } catch(SQLException | IOException e){
            e.printStackTrace();
            throw new AtlasServerException("Failed to insert locations");
        } finally{
            safelyClose(br);
        }
    }

    private void createTempTable(Connection con, String tableName, String dataFilePath) throws AtlasServerException {
        PreparedStatement pstmt = null;
        System.out.println("creating temp table " + tableName);
        try {
            pstmt = con.prepareStatement("LOAD DATA INFILE '" + dataFilePath +
                    "' REPLACE INTO TABLE " + tableName + " FIELDS TERMINATED BY '\\t'");
            pstmt.executeUpdate();
            /*
            pstmt = con.prepareStatement("LOAD DATA INFILE '?' REPLACE INTO TABLE ? FIELDS TERMINATED BY '\\t'");
            pstmt.setString(1, dataFilePath);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
            */
            // TODO: Etan - which should we use?
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException("Failed to create temp table '" + tableName + "'");
        } finally {
            safelyClose(pstmt);
        }
    }

    private String concatToOutPath(String fileName) {
        return parserOutputPath + File.pathSeparator + fileName;
    }
}
