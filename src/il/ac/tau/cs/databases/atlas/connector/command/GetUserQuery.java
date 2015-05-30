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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22/05/2015.
 */
public class GetUserQuery extends BaseDBCommand<List<User>> {
    private User user;

    public GetUserQuery(User user) {
        this.user = user;
    }

    @Override
    protected List<User> innerExecute(Connection con) throws AtlasServerException {
    	PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<User>();
        try {
        	statement = con.prepareStatement(
        			"SELECT * FROM "+ DBConstants.User.TABLE_NAME+
        			" where "+DBConstants.User.USERNAME+" = ?");
        	statement.setString(1, user.getUsername());
        	logger.info(String.format("Executing DB query: %s.", statement.toString()));
        	resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
            	String username = resultSet.getString(DBConstants.User.USERNAME);
            	String password = resultSet.getString(DBConstants.User.PASSWORD);
            	Date dateOfBirth = resultSet.getDate(DBConstants.User.BORN_ON_DATE);
                User newUser = new User(username, password, dateOfBirth, null);
                users.add(newUser);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            safelyClose(statement, resultSet);
        }
        
        // Validate that no more then 1 users with that username exists
        if (users.size() > 1) {
        	throw new AtlasServerException("[DB ERROR] - Found more then 1 user with the username: "+ user.getUsername() +"!");
        }
        logger.info(String.format("Query executed properly.", statement.toString()));
        return users;
    }

}
