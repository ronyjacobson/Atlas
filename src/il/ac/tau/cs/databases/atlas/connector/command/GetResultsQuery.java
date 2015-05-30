package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.Location;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by user on 22/05/2015.
 */
public class GetResultsQuery extends BaseDBCommand<ArrayList<Result>> {
	String name;
	int startYear;
	int endYear;
	String category;
	boolean byName;

	public GetResultsQuery(String name) {
		this.name = name;
		this.byName = true;
	}

	public GetResultsQuery(int startYear, int endYear, String category,
			boolean byName) {
		this.startYear = startYear;
		this.endYear = endYear;
		this.category = category;
		this.byName = false;
	}

	@Override
	protected ArrayList<Result> innerExecute(Connection con)
			throws AtlasServerException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<Result> results = new ArrayList<Result>();

		try {
			if (byName) {
				buildNameStmt(con, statement);
			} else {
				buildNormalStmt(con, statement);
			}

			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			resultSet = statement.executeQuery();

			while (resultSet.next()) {

				// Validate that no more then 1 users with that UserName exists
				if (FetchedUser != null) {
					throw new AtlasServerException(
							String.format(
									"Error: Found more then 1 user with the username: %s",
									user.getUsername()));
				}

				// Create fetched User
				String username = resultSet
						.getString(DBConstants.User.USERNAME);
				String password = resultSet
						.getString(DBConstants.User.PASSWORD);
				Date dateOfBirth = resultSet
						.getDate(DBConstants.User.BORN_ON_DATE);
				int locationID = resultSet
						.getInt(DBConstants.User.BORN_IN_LOCATION);
				int userID = resultSet.getInt(DBConstants.User.USER_ID);
				FetchedUser = new User(userID, username, password, dateOfBirth,
						locationID);
			}

		} catch (SQLException e) {
			// TODO - handle Exception??
			e.printStackTrace();
		} finally {
			safelyClose(statement, resultSet);
		}

		logger.info(String.format("Query executed properly.",
				statement.toString()));
		return null;
	}

	private void buildNormalStmt(Connection con, PreparedStatement statement) {
		statement = con.prepareStatement(
		"SELECT * FROM " + DBConstants.User.TABLE_NAME + " where "
				+ DBConstants.User.USERNAME + " = ?");
		statement.setString(1, user.getUsername());

	}

	private void buildNameStmt(Connection con, PreparedStatement statement) {
		//String name, Location location, Date date, boolean isBirth, String summary, String wikiLink
		//LOCATION: int id, String name, double lat, double lng
		statement = con.prepareStatement(
		"SELECT "+ DBConstants.Category.CATEGORY_NAME + "," + DBConstants.Location.TABLE_NAME + "," + 
		" FROM " + DBConstants.Category.TABLE_NAME + "," + DBConstants.Location.TABLE_NAME + "," + DBConstants.Person.TABLE_NAME + "," + 
		" WHERE "
						+ DBConstants.User.USERNAME + " = ?");
				statement.setString(1, user.getUsername());
				
	}

}
