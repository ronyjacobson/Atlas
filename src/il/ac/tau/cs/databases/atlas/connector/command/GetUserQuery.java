package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 22/05/2015.
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
        User FetchedUser = null;
        try {
        	statement = con.prepareStatement(
        			"SELECT * FROM "+ DBConstants.User.TABLE_NAME+
        			" where "+DBConstants.User.USERNAME+" = ?");
        	statement.setString(1, user.getUsername());
        	logger.info(String.format("Executing DB query: %s.", statement.toString()));
        	resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
            	
            	// Validate that no more then 1 users with that UserName exists
            	if (FetchedUser != null) {
            		throw new AtlasServerException(String.format("Error: Found more then 1 user with the username: %s", user.getUsername()));
            	}
            	
            	// Create fetched User
            	String username = resultSet.getString(DBConstants.User.USERNAME);
            	String password = resultSet.getString(DBConstants.User.PASSWORD);
            	Date dateOfBirth = resultSet.getDate(DBConstants.User.BORN_ON_DATE);
            	int locationID = resultSet.getInt(DBConstants.User.BORN_IN_LOCATION);
            	int userID = resultSet.getInt(DBConstants.User.USER_ID);
                FetchedUser = new User(userID, username, password, dateOfBirth, locationID);
            }

        } catch (SQLException e) {
        	// TODO -  handle Exception??
            e.printStackTrace();
        }
        finally {
            safelyClose(statement, resultSet);
        }
        
        logger.info(String.format("Query executed properly.", statement.toString()));
        return FetchedUser;
    }

}
