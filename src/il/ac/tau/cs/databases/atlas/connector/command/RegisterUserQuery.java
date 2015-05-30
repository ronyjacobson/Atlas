package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

/**
 * Created by user on 22/05/2015.
 */
public class RegisterUserQuery extends BaseDBCommand<User> {
	private User user;

	public RegisterUserQuery(User user) {
		this.user = user;
	}

	@Override
	protected User innerExecute(Connection con) throws AtlasServerException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {

			statement = con.prepareStatement("INSERT INTO "
					+ DBConstants.User.TABLE_NAME + " (`"
					+ DBConstants.User.USERNAME + "`, `"
					+ DBConstants.User.PASSWORD + "`, `"
					+ DBConstants.User.BORN_IN_LOCATION + "`, `"
					+ DBConstants.User.BORN_ON_DATE + "`) VALUES (?, ?, ?, ?)", new String[]{DBConstants.User.USER_ID});
			
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			statement.setInt(3, user.getLocationID());
			statement.setDate(4, new java.sql.Date(user.getDateOfBirth().getTime()));

			System.out.println(String.format("Executing DB query: %s.", statement.toString()));
			
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			resultSet.next();
            int genID = resultSet.getInt(1);
            user.setUserID(genID);
            System.out.println("User added successfully - The generated user ID is: "+ genID);

		} catch (SQLException e) {
			Integer errorCode = e.getErrorCode();
			if (errorCode.equals(1062)) {
				// This error code represents that there is already a username with the desired value
				String msg = String.format("Error - Duplicate values where found when trying to reguter username %s", user.getUsername());
				System.out.println(msg);
				return null;
			} else {
				//TODO- handle Exception?
				e.printStackTrace();
			}
			return null;
		} finally {
			safelyClose(statement, resultSet);
		}

		System.out.println(String.format("Query executed properly.",statement.toString()));
		return user;
	}

}
