package il.ac.tau.cs.databases.atlas.db.command.oldCommands;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.ResultsHolder;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.modal.Location;
import il.ac.tau.cs.databases.atlas.core.modal.Result;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.command.base.BaseDBCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by user on 22/05/2015.
 */
@Deprecated
public class SearchResultsByNameQuery extends BaseDBCommand<ArrayList<Result>> {
	private String name;
	private int maxYear = -20000;
	private int minYear= 20000;
	
	private int limitNumOfResults = DBConstants.LIMIT;
	HashMap<String, Result> results = new HashMap<String, Result>();
	PreparedStatement statement = null;
	ResultSet resultSet = null;

	public SearchResultsByNameQuery(String name) {
		this.name = name;
	}

	@Override
	protected ArrayList<Result> innerExecute(Connection con) throws AtlasServerException {
		ArrayList<Result> finalResults = new ArrayList<Result>();

		try {
			innerExecuteByBirths(con, true);
			innerExecuteByBirths(con, false);
			finalResults.addAll(results.values());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AtlasServerException("Error: unable to fetch favorites");
		} finally {
			safelyClose(statement, resultSet);
		}
		
		return finalResults;
	}


	private void innerExecuteByBirths(Connection con, boolean isBirth) throws SQLException {
		String hashIDPref;
		
		if (isBirth) {
			logger.info("Fetching births by name...");
			hashIDPref = "birth";
		} else {
			logger.info("Fetching deaths by name...");
			hashIDPref = "death";
		}
		
		// Make Statement
		String st = String.format("%s\nUNION ALL\n(%s)",
		// Get user oriented results
				makeStatment(true, isBirth),
				// Get all results
				makeStatment(false, isBirth));

		st = "SELECT DISTINCT * FROM (" + st + ") AS results";
		statement = con.prepareStatement(st);

		logger.info(String.format("Executing DB query: %s.",
				statement.toString()));

		// Execute:
		resultSet = statement.executeQuery();

		while (resultSet.next()) {

			// Create fetched Result
			int personID = resultSet.getInt(DBConstants.PERSON_ID_L);
			String name = resultSet.getString(DBConstants.PREF_LABEL_L);
			String geoname = resultSet.getString(DBConstants.GEO_NAME_L);
			java.util.Date bornOn = resultSet
					.getDate(DBConstants.BORN_ON_DATE_L);
			java.util.Date diedOn = resultSet
					.getDate(DBConstants.DIED_ON_DATE_L);
			String locUrl = resultSet.getString("LocURL");
			String personUrl = resultSet.getString("PersonURL");
			String category = resultSet.getString(DBConstants.CATEGORY_NAME_L);
			double lng = resultSet.getDouble(DBConstants.LONG_L);
			double lat = resultSet.getDouble(DBConstants.LAT_L);
			boolean isFemale = resultSet.getBoolean(DBConstants.IS_FEMALE_L);
			Location location = new Location((long)0, geoname, lat, lng, locUrl);
			java.util.Date date = (isBirth ? bornOn : diedOn);

			// Adjust Max year/Min Year to set in timeline
			if (date != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int year = cal.get(Calendar.YEAR);
				if (year < minYear) {
					minYear = year;
				}
				if (year > maxYear) {
					maxYear = year;
				}
			}

			Result res = new Result(String.valueOf(personID), name, location,
					date, isBirth, category, personUrl, isFemale, category);
			
			String hashID = hashIDPref + res.getID();
			
			if (results.containsKey(hashID)) {
				res = results.get(hashID);
				res.setSummary(category + ", " + res.getSummary());
			} else {
				if (isFemale) {
					ResultsHolder.INSTANCE.incNumOfFemales();
				}
				if (isBirth) {
					ResultsHolder.INSTANCE.incNumOfBirths();
				}
				results.put(hashID, res);
			}
		}
	}
	
	
	private String makeStatment(boolean isUserOriented, boolean isBirth) {
		
		String bornOrDiedDate = (isBirth ? DBConstants.Person.BORN_ON_DATE
				: DBConstants.Person.DIED_ON_DATE);
		String bornOrDiedLocation = (isBirth ? DBConstants.Person.BORN_IN_LOCATION
				: DBConstants.Person.DIED_IN_LOCATION);

		
			String select = String.format(
					"SELECT DISTINCT %s, %s, %s, %s, %s, %s as LocURL, %s as PersonURL, %s, %s, %s, %s \n",
					// All labels wanted
					DBConstants.Person.PERSON_ID,
					DBConstants.Person.PREF_LABEL,
					DBConstants.Location.GEO_NAME, 
					DBConstants.Person.BORN_ON_DATE, 
					DBConstants.Person.DIED_ON_DATE,
					DBConstants.Location.WIKI_URL, 
					DBConstants.Person.WIKI_URL,
					DBConstants.Location.LONG, 
					DBConstants.Location.LAT, 
					DBConstants.Person.IS_FEMALE,
					DBConstants.Category.CATEGORY_NAME);
			
			
			String from = 
					String.format(
					"FROM %s, %s, %s, %s, %s",
					// All Tables needed
					DBConstants.Person.TABLE_NAME,
					DBConstants.Location.TABLE_NAME,
					DBConstants.PersonLabels.TABLE_NAME,
					DBConstants.Category.TABLE_NAME,
					DBConstants.PersonHasCategory.TABLE_NAME) + 
					", (SELECT person_ID FROM person_labels WHERE label like '%"+ this.name +"%') as ids"; 
			
			String withFavsFrom = ", " + DBConstants.UserFavorites.TABLE_NAME;
			
			String basicWhere =
					"\n" +
					"WHERE "+ bornOrDiedLocation				 	+" = " + DBConstants.Location.LOCATION_ID			 + " \n" +
					"AND "  + bornOrDiedLocation 					+" <> 'NULL' \n"+ 
					"AND "  + bornOrDiedDate						+" <> 'NULL' \n" +
					"AND "  + DBConstants.Person.PERSON_ID			+" = " + DBConstants.PersonLabels.PERSON_ID			 + " \n" +
					"AND "  + DBConstants.Person.PERSON_ID			+" = " + DBConstants.PersonHasCategory.PERSON_ID 	 + " \n" +
					"AND "  + DBConstants.Category.CATEGORY_ID		+" = " + DBConstants.PersonHasCategory.CATEGORY_ID 	 + " \n" +
					"AND ids.person_ID = person.person_ID"+ " \n";
			
			String withFavoritesWhere = 
					"AND " + DBConstants.Person.PERSON_ID +" = " + DBConstants.UserFavorites.PERSON_ID	 + " \n" +
					"AND " + DBConstants.UserFavorites.USER_ID +" = " + Main.user.getUserID()	 + " \n" ;
			
			String withUserAddedWhere = 
					"AND " + DBConstants.Person.ADDED_BY_USER +" = " + Main.user.getUserID() + " \n";
		
			
			String favsWhere = basicWhere + withFavoritesWhere;
			String userAddedWhere = basicWhere + withUserAddedWhere;
			String limit = "ORDER BY RAND() LIMIT " + this.limitNumOfResults;
		
			if (isUserOriented) {
				String q1 = select + from + withFavsFrom + favsWhere + limit;
				String q2 = select + from + userAddedWhere + limit;
				return "(" + q1 +")"+ "\n" + "UNION \n" + "(" + q2 +")";
			} else {
				return select + from + basicWhere + limit;
			}
		}


	public int getMaxYear() {
		return maxYear;
	}

	public int getMinYear() {
		return minYear;
	}

}