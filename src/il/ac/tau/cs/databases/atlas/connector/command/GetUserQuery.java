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
        			DBConstants.User.TABLE_NAME +
        			" WHERE "+  DBConstants.USERNAME_L+" = ? ");
        	
        	statement.setString(1, user.getUsername());
        	logger.info(String.format("Executing DB query:\n %s.", statement.toString()));
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
            	Long locationID = resultSet.getLong(DBConstants.BORN_IN_LOCATION_L);
            	int userID = resultSet.getInt(DBConstants.USER_ID_L);
            	boolean isFemail = resultSet.getBoolean(DBConstants.IS_FEMALE_L);
				fetchedUser = new User(userID, username, password, dateOfBirth, locationID, isFemail);
               
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
