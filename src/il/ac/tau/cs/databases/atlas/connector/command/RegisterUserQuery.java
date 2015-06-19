package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
					+ DBConstants.USERNAME_L + "`, `"
					+ DBConstants.PASSWORD_L + "`, `"
					+ DBConstants.BORN_IN_LOCATION_L + "`, `"
					+ DBConstants.IS_FEMALE_L + "`, `"
					+ DBConstants.BORN_ON_DATE_L + "`) VALUES (?, ?, ?, ?, ?)", new String[]{DBConstants.User.USER_ID});
			
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
			if (user.getLocationID() != null) {
				statement.setLong(3, user.getLocationID());
			} else {
				statement.setNull(3, java.sql.Types.BIGINT);
			}
			statement.setBoolean(4, user.isFemale());
			statement.setDate(5, new java.sql.Date(user.getDateOfBirth().getTime()));

			logger.info(String.format("Executing DB query: %s.", statement.toString()));
			
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			resultSet.next();
            int genID = resultSet.getInt(1);
            user.setUserID(genID);
            logger.info("User added successfully - The generated user ID is: "+ genID);

		} catch (SQLException e) {
			Integer errorCode = e.getErrorCode();
			if (errorCode.equals(1062)) {
				// This error code represents that there is already a username with the desired value
				String msg = String.format("Username %s already taken.", user.getUsername());
				throw new AtlasServerException(msg);
			} else {
				//TODO- handle Exception?
				e.printStackTrace();
				throw new AtlasServerException(e.getMessage());
			}
		} finally {
			safelyClose(statement, resultSet);
		}
		logger.info("Query executed properly.");
		return user;
	}

}
