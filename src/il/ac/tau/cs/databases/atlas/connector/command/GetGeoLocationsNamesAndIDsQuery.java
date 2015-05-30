package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 22/05/2015.
 */
public class GetGeoLocationsNamesAndIDsQuery extends BaseDBCommand<Void> {

    public GetGeoLocationsNamesAndIDsQuery() {}

    
	@Override
    protected Void innerExecute(Connection con) {
    	PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
        	statement = con.prepareStatement(
        			"SELECT "+ DBConstants.Location.GEO_NAME + "," + DBConstants.Location.GEO_ID +
        			" FROM "+ DBConstants.Location.TABLE_NAME);
        	logger.info(String.format("Executing DB query: %s.", statement.toString()));
        	resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
            	// Create fetched Location
            	String name = resultSet.getString(DBConstants.Location.GEO_NAME );
            	int id = resultSet.getInt(DBConstants.Location.GEO_ID);
            	Queries.locationsMap.put(name, id);
            	Queries.locationsNames.add(name);
            }

        } catch (SQLException e) {
        	// TODO -  handle Exception??
            e.printStackTrace();
        }
        finally {
            safelyClose(statement, resultSet);
        }
        
        logger.info(String.format("Query executed properly.", statement.toString()));
		return null;
    }

}
