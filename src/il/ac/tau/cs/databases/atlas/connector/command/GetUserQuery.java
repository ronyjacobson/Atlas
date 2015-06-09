package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.Location;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A query to get a registered user and his location from the DB.
 * Will return null if the user does not exists in the DB. 
 */
public class GetUserQuery extends BaseDBCommand<User> {
    private User user;

    public GetUserQuery(User user) {
        this.user = user;
    }

	@Override
    protected User innerExecute(Connection con) throws AtlasServerException {
    	PreparedStatement statement = null;
        ResultSet resultSet = null;
        User fetchedUser = null;
        try {
        	statement = con.prepareStatement(
        			"SELECT * FROM "+
        						DBConstants.User.TABLE_NAME + ", " +
        						DBConstants.Location.TABLE_NAME +
        			" WHERE "+ DBConstants.USERNAME_L+" = ? AND "+ DBConstants.BORN_IN_LOCATION_L +" = "+DBConstants.GEO_ID_L);
        	statement.setString(1, user.getUsername());
        	logger.info(String.format("Executing DB query: %s.", statement.toString()));
        	resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
            	
            	// Validate that no more then 1 users with that UserName exists
            	if (fetchedUser != null) {
            		throw new AtlasServerException(String.format("Error: Found more then 1 user with the username: %s", user.getUsername()));
            	}
            	
            	// Create fetched User
            	String username = resultSet.getString(DBConstants.USERNAME_L);
            	String password = resultSet.getString(DBConstants.PASSWORD_L);
            	Date dateOfBirth = resultSet.getDate(DBConstants.BORN_ON_DATE_L);
            	int locationID = resultSet.getInt(DBConstants.BORN_IN_LOCATION_L);
            	int userID = resultSet.getInt(DBConstants.USER_ID_L);
            	String locationName = resultSet.getString(DBConstants.GEO_NAME_L);
            	double lng = resultSet.getDouble(DBConstants.LONG_L);
				double lat = resultSet.getDouble(DBConstants.LAT_L);
				String locUrl= resultSet.getString(DBConstants.WIKI_URL_L);
            	Location loc = new Location(locationID, locationName, lat, lng, locUrl);
                fetchedUser = new User(userID, username, password, dateOfBirth, locationID);
                fetchedUser.setLocation(loc);
            }

        } catch (SQLException e) {
        	// TODO -  handle Exception??
            e.printStackTrace();
        }
        finally {
            safelyClose(statement, resultSet);
        }
        
        logger.info(String.format("Query executed properly.", statement.toString()));
        return fetchedUser;
    }

}
