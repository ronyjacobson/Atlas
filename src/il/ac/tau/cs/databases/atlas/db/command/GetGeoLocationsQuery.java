package il.ac.tau.cs.databases.atlas.db.command;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.queries.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 22/05/2015.
 */
public class GetGeoLocationsQuery extends BaseDBCommand<Void> {

    public GetGeoLocationsQuery() {}

    
	@Override
    protected Void innerExecute(Connection con) throws AtlasServerException {
    	PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
        	statement = con.prepareStatement(
        			"SELECT "+ DBConstants.Location.GEO_NAME + "," + DBConstants.Location.LOCATION_ID +
        			" FROM "+ DBConstants.Location.TABLE_NAME + " ORDER BY "+ DBConstants.Location.GEO_NAME);
        	logger.info(String.format("Executing DB query: %s.", statement.toString()));
        	resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
            	// Create fetched Location
            	String name = resultSet.getString(DBConstants.Location.GEO_NAME);
            	long id = resultSet.getLong(DBConstants.Location.LOCATION_ID);
            	Queries.locationsMap.put(name, id);
            	Queries.locationsNames.add(name);
            }

        } catch (SQLException e) {
            logger.error("", e);
            throw new AtlasServerException(e.getMessage());
        }
        finally {
            safelyClose(statement, resultSet);
        }
        
        logger.info(String.format("Query executed properly.", statement.toString()));
		return null;
    }

}
