package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.DBQueries;
import il.ac.tau.cs.databases.atlas.db.Location;
import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by user on 22/05/2015.
 */
public class GetFavoritesResultsQuery extends BaseDBCommand<ArrayList<Result>> {
	private boolean isBirth;
	private int limitNumOfResults = 100;

	public GetFavoritesResultsQuery(boolean isBirth) {
		this.isBirth = isBirth;
	}

	@Override
	protected ArrayList<Result> innerExecute(Connection con) throws AtlasServerException {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		HashMap<String, Result> results = new HashMap<String, Result>();

		try {
			String st =	makeStatment();
			
			statement = con.prepareStatement(st);
			
			System.out.println(String.format("Executing DB query: %s.",
					statement.toString()));
			resultSet = statement.executeQuery();
			

			while (resultSet.next()) {
				// Create fetched Result
				int personID = resultSet.getInt(DBConstants.PERSON_ID_L);
				String name = resultSet.getString(DBConstants.LABEL_L);
				String geoname = resultSet.getString(DBConstants.GEO_NAME_L); 
				java.util.Date bornOn = resultSet.getDate(DBConstants.BORN_ON_DATE_L); 
				java.util.Date diedOn =resultSet.getDate(DBConstants.DIED_ON_DATE_L);
				String locUrl= resultSet.getString("LocURL");
				String personUrl = resultSet.getString("PersonURL");
				String category = resultSet.getString(DBConstants.CATEGORY_NAME_L);
				double lng = resultSet.getDouble(DBConstants.LONG_L);
				double lat = resultSet.getDouble(DBConstants.LAT_L);
				boolean isFemale = resultSet.getBoolean(DBConstants.IS_FEMALE_L);
				if (isFemale) {
					DBQueries.amountOfFemaleResults ++;
				}
				Location location = new Location(0, geoname, lat, lng, locUrl);
				java.util.Date date = (isBirth ? bornOn : diedOn);
				
				Result res = new Result(String.valueOf(personID), name, location, date, isBirth, category, personUrl, isFemale, category);
				if (results.containsKey(res.getID())) {
					res= results.get(res.getID());
					res.setSummary(category + ", " + res.getSummary());
				} else {
					results.put(res.getID(), res);
				}
			}

		} catch (SQLException e) { 
			// TODO - handle Exception??
			e.printStackTrace();
		} finally {
			safelyClose(statement, resultSet);
		}

		System.out.println(String.format("Query executed properly.",
				statement.toString()));
		
		ArrayList<Result> finalResults = new ArrayList<Result>(results.values());
		return finalResults;
	}
	
	
	private String makeStatment() {
		
		String select = String.format(
					"SELECT DISTINCT %s, %s, %s, %s, %s, %s as LocURL, %s as PersonURL, %s, %s, %s, %s \n",
					// All labels wanted
					DBConstants.Person.PERSON_ID,
					DBConstants.PersonLabels.LABEL,
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
					"FROM %s, %s, %s, %s, %s, %s",
					// All Tables needed
					DBConstants.Person.TABLE_NAME,
					DBConstants.Location.TABLE_NAME,
					DBConstants.PersonLabels.TABLE_NAME,
					DBConstants.UserFavorites.TABLE_NAME,
					DBConstants.Category.TABLE_NAME,
					DBConstants.PersonHasCategory.TABLE_NAME);
			
			String where =
					"\n" +
					"WHERE "+ DBConstants.Person.BORN_IN_LOCATION 		+" = " + DBConstants.Location.GEO_ID		 + " \n" +
					"AND "  + DBConstants.Person.PERSON_ID				+" = " + DBConstants.PersonLabels.PERSON_ID	 + " \n" +
					"AND "  + DBConstants.PersonLabels.IS_PREFERED	    +" = '1' \n"+
					"AND " + DBConstants.Person.PERSON_ID 				+" = " + DBConstants.UserFavorites.PERSON_ID + " \n" +
					"AND " + DBConstants.UserFavorites.USER_ID 			+" = " + Main.user.getUserID()				 + " \n" ;
			
			String limit = "limit " + this.limitNumOfResults;
			
			return select + from + where + limit;
			
	}

}
