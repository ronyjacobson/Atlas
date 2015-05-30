package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        Statement statement = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<User>();
        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(
            		"SELECT * from " + DBConstants.UserSchema.SCHEMA_NAME +
            		" Where " + DBConstants.UserSchema.USERNAME_FIELD + " = \"" + user.getUsername() + "\"");
            while (resultSet.next()) {
            	String username = resultSet.getString(DBConstants.UserSchema.USERNAME_FIELD);
            	String password = resultSet.getString(DBConstants.UserSchema.PASSWORD_FIELD);
            	Date dateOfBirth = resultSet.getDate(DBConstants.UserSchema.BORN_ON_DATE_FIELD);
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
        
        return users;
    }

}
