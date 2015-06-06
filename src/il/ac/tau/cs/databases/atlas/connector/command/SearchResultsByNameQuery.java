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

/**
 * Created by user on 22/05/2015.
 */
public class SearchResultsByNameQuery extends BaseDBCommand<ArrayList<Result>> {
	String name;
	boolean isBirth;
	int limitNumOfResults = 100;

	public SearchResultsByNameQuery(String name, boolean isBirth) {
		this.name = name;
		this.isBirth = isBirth;
	}

	@Override
	protected ArrayList<Result> innerExecute(Connection con) throws AtlasServerException {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<Result> results = new ArrayList<Result>();

		try {
			String st = String.format("%s\nUNION\n%s",
					// Get user oriented results
					makeStatment(true),
					// Get all results
					makeStatment(false));
			
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
				double lng = resultSet.getDouble(DBConstants.LONG_L);
				double lat = resultSet.getDouble(DBConstants.LAT_L);
				boolean isFemale = resultSet.getBoolean(DBConstants.IS_FEMALE_L);
				if (isFemale) {
					DBQueries.amountOfFemaleResults ++;
				}
				Location location = new Location(0, geoname, lat, lng, locUrl);
				java.util.Date date = (isBirth ? bornOn : diedOn);
				Result res = new Result(String.valueOf(personID), name, location, date, isBirth, personUrl, isFemale);
				results.add(res);
			}

		} catch (SQLException e) {
			// TODO - handle Exception??
			e.printStackTrace();
		} finally {
			safelyClose(statement, resultSet);
		}

		System.out.println(String.format("Query executed properly.",
				statement.toString()));
		return results;
	}
	
	
	private String makeStatment(boolean isUserOriented) {
			
			String select = String.format(
					"SELECT DISTINCT %s, %s, %s, %s, %s, %s as LocURL, %s as PersonURL, %s, %s, %s \n",
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
					DBConstants.Person.IS_FEMALE);
			
			
			String from = 
					String.format(
					"FROM %s, %s, %s, %s",
					// All Tables needed
					DBConstants.Person.TABLE_NAME,
					DBConstants.Location.TABLE_NAME,
					DBConstants.PersonLabels.TABLE_NAME,
					DBConstants.UserFavorites.TABLE_NAME) + 
					", (SELECT person_ID FROM person_labels WHERE label like '%"+ this.name +"%') as ids"; 
			
			String basicWhere =
					"\n" +
					"WHERE "+ DBConstants.Person.DIED_IN_LOCATION 		+" = " + DBConstants.Location.GEO_ID		 + " \n" +
					"AND "  + DBConstants.Person.PERSON_ID				+" = " + DBConstants.PersonLabels.PERSON_ID	 + " \n" +
					"AND "  + DBConstants.PersonLabels.IS_PREFERED	    +" = '1' \n" +
					"AND ids.person_ID = person.person_ID"+ " \n";
			
			String withFavoritesWhere = 
					"AND " + DBConstants.Person.PERSON_ID +" = " + DBConstants.UserFavorites.PERSON_ID	 + " \n" +
					"AND " + DBConstants.UserFavorites.USER_ID +" = " + Main.user.getUserID()	 + " \n" ;
			
			String withUserAddedWhere = 
					"AND " + DBConstants.Person.ADDED_BY_USER +" = " + Main.user.getUserID() + " \n";
		
			
			String favsWhere = basicWhere + withFavoritesWhere;
			String userAddedWhere = basicWhere + withUserAddedWhere;
			String limit = "limit " + this.limitNumOfResults;
		
			if (isUserOriented) {
				String q1 = select + from + favsWhere + limit;
				String q2 = select + from + userAddedWhere + limit;
				return q1 + "\n" + "UNION \n" + q2;
			} else {
				return select + from + basicWhere + limit;
			}
		}
	

}
