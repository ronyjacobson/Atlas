package il.ac.tau.cs.databases.atlas.connector.command.NewCommands;

import il.ac.tau.cs.databases.atlas.ResultsHolder;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.Location;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 22/05/2015.
 */
public class GetResultsGeneralQuery extends BaseDBCommand<Map<String, Result>> {
	int limitNumOfResults = DBConstants.LIMIT;
	PreparedStatement statement = null;
	ResultSet resultSet = null;

	HashMap<String, Result> results = new HashMap<String, Result>();
	private int minYear = 3000;
	private int maxYear = 0;

	@Override
	protected Map<String, Result> innerExecute(Connection con)
			throws AtlasServerException {
		logger.info("Generating General Results Query...");
		try {

			String stmt = makeStmt();
			statement = con.prepareStatement(stmt);

			logger.info(String.format("Executing DB query:\n%s.", stmt));

			// Save Query for later Statistics fetching
			ResultsHolder.INSTANCE.setLastResultQueryExecuted(stmt);

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				// Create fetched Result

				// Prepare person
				String personID = String.valueOf(resultSet
						.getInt(DBConstants.PERSON_ID_L));
				String name = resultSet.getString(DBConstants.PREF_LABEL_L);
				String category = resultSet
						.getString(DBConstants.CATEGORY_NAME_L);
				java.util.Date bornOn = resultSet
						.getDate(DBConstants.BORN_ON_DATE_L);
				java.util.Date diedOn = resultSet
						.getDate(DBConstants.DIED_ON_DATE_L);
				String personUrl = resultSet.getString("PersonURL");
				boolean isFemale = resultSet
						.getBoolean(DBConstants.IS_FEMALE_L);

				// Prepare location
				// Birth:
				Long b_locationID = resultSet
						.getLong(DBConstants.BORN_IN_LOCATION_L);
				String b_geoname = resultSet.getString("b_"
						+ DBConstants.GEO_NAME_L);
				String b_locUrl = resultSet.getString("b_LocURL");
				double b_lng = resultSet.getDouble("b_" + DBConstants.LONG_L);
				double b_lat = resultSet.getDouble("b_" + DBConstants.LAT_L);
				Location b_location = new Location(b_locationID, b_geoname,
						b_lat, b_lng, b_locUrl);

				// Death:
				Long d_locationID = resultSet
						.getLong(DBConstants.DIED_IN_LOCATION_L);
				String d_geoname = resultSet.getString("d_"
						+ DBConstants.GEO_NAME_L);
				String d_locUrl = resultSet.getString("d_LocURL");
				double d_lng = resultSet.getDouble("d_" + DBConstants.LONG_L);
				double d_lat = resultSet.getDouble("d_" + DBConstants.LAT_L);
				Location d_location = new Location(d_locationID, d_geoname,
						d_lat, d_lng, d_locUrl);

				Result birthResult = new Result(personID, name, b_location,
						bornOn, true, category, personUrl, isFemale, category);

				// Set validity of result- whether show it on map or nor
				birthResult.setValidResult(ExtraValidateResult(birthResult));
				// Statistics:
				setStatistics(birthResult);

				// Add to result set
				addToHash("b" + personID, birthResult, results);

				if (diedOn != null) {
					Result deathResult = new Result(personID, name, d_location,
							diedOn, false, category, personUrl, isFemale,
							category);
					// Set validity of result- whether show it on map or nor
					deathResult
							.setValidResult(ExtraValidateResult(deathResult));
					// Statistics:
					setStatistics(deathResult);

					// Add to results set
					addToHash("d" + personID, deathResult, results);
				}

			}
			logger.info("Query executed successfully.");

		} catch (SQLException e) {
			e.printStackTrace();
			throw new AtlasServerException("Error: unable to fetch results");
		} finally {
			safelyClose(statement, resultSet);
		}

		ResultsHolder.INSTANCE.setResultMap(results);
		return results;

	}

	private void setStatistics(Result result) {
		ResultsHolder.INSTANCE.incNumOfResults();
		if (result.isBirth()) {
			ResultsHolder.INSTANCE.incNumOfBirths();
		}
		if (result.isFemale()) {
			ResultsHolder.INSTANCE.incNumOfFemales();
		}

	}

	/**
	 * We always pull death and birth details (so we can edit a person), but
	 * sometimes only one of them needs to show on map (like search by years) or
	 * we need to get more details.
	 * 
	 * @return true if the result is valid to show on map.
	 */
	protected boolean ExtraValidateResult(Result result) {
		return true;
	}

	protected void setMaxMinYear(Result result) {
		int year = result.getYear();
		if (year < minYear) {
			minYear = year;
		}
		if (year > maxYear) {
			maxYear = year;
		}
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(int maxYear) {
		this.maxYear = maxYear;
	}

	protected String makeStmt() {
		String select = makeSelectStmt();
		String from = makeFromStmt();
		String where = makeWhereStmt();
		String rest = makeRestOfStmt();
		return select + from + where + rest;
	}

	private void addToHash(String hashID, Result result,
			HashMap<String, Result> results) {
		Result res = results.get(hashID);
		if (res != null) {
			// Update summary
			result.setSummary(result.getCategory() + ", " + res.getSummary());
		}
		// Put the result or replace the existing one:
		results.put(hashID, result);
	}

	protected String makeSelectStmt() {
		return "SELECT DISTINCT \n" + "	person.person_ID, \n"
				+ "	person.addedByUser, \n" + "	person.prefLabel, \n"
				+ "	person.wasBornOnDate, \n" + "	person.diedOnDate, \n"
				+ "	person.wasBornInLocation, \n"
				+ "	person.diedInLocation, \n"
				+ "	person.wikiURL AS PersonURL, \n" + "	person.isFemale, \n"
				+ "	category.categoryName, \n"
				+ "	b_location.geo_name AS b_geo_name, \n"
				+ "	b_location.wikiURL AS b_LocURL, \n"
				+ "	b_location.longitude AS b_longitude, \n"
				+ "	b_location.latitude AS b_latitude, \n"
				+ "	location.geo_name AS d_geo_name, \n"
				+ "	location.wikiURL AS d_LocURL, \n"
				+ "	location.longitude AS d_longitude, \n"
				+ "	location.latitude AS d_latitude\n";
	}

	protected String makeFromStmt() {
		return "FROM \n"
				+ "	location b_location, \n"
				+ "	category \n"
				+ "	    JOIN \n"
				+ "	person_has_category ON category.category_ID = person_has_category.category_ID, \n"
				+ "	person \n"
				+ "	    LEFT OUTER JOIN \n"
				+ "	location ON person.diedInLocation = location.location_ID \n";
		// , EXTRA
	}

	protected String makeWhereStmt() {
		return "WHERE \n"
				+ "		person.person_ID = person_has_category.person_ID \n"
				+ "		AND person.wasBornInLocation = b_location.location_ID \n";
		// AND MORE CONDITIONS
	}

	protected String makeRestOfStmt() {
		return "ORDER BY RAND() LIMIT " + DBConstants.LIMIT;
	}

}
