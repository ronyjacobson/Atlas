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
					+ DBConstants.User.BORN_ON_DATE + "`) VALUES (?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, user.getUsername());
			statement.setString(2, user.getUsername());
			statement.setInt(3, user.getLocationID());
			statement.setDate(4, new java.sql.Date(user.getDateOfBirth().getTime()));

			logger.info(String.format("Executing DB query: %s.", statement.toString()));
			
			resultSet = statement.getGeneratedKeys();
			
			resultSet.next();
            int genID = resultSet.getInt(1);
            user.setUserID(genID);
            logger.info("User added successfully - The generated user ID is: "+ genID);

		} catch (SQLException e) {
			Integer errorCode = e.getErrorCode();
			if (errorCode.equals(1062)) {
				// This error code represents that there is already a username with the desired value
				String msg = "Duplicate values where found for unique username has been catched. Changes did not save.";
				throw new AtlasServerException(msg);
			} else {
				//TODO- handle Exception?
			}
			return null;
		} finally {
			safelyClose(statement, resultSet);
		}

		logger.info(String.format("Query executed properly.",statement.toString()));
		return user;
	}

}
