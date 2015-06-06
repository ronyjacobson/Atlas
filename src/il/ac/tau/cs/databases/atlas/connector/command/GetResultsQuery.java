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
public class GetResultsQuery extends BaseDBCommand<ArrayList<Result>> {
	String name;
	int startYear;
	int endYear;
	String category;
	boolean byName;
	int limitNumOfResults;
	boolean userOriented;
	boolean isBirth;

	public GetResultsQuery(String name,boolean userOriented, boolean isBirth) {
		this.name = name;
		this.byName = true;
		this.userOriented = userOriented;
		this.isBirth = isBirth;
	}

	public GetResultsQuery(int startYear, int endYear, String category,
			boolean byName, boolean userOriented, boolean isBirth) {
		this.startYear = startYear;
		this.endYear = endYear;
		this.category = category;
		this.byName = false;
		this.userOriented= userOriented;
		this.isBirth = isBirth;
	}

	@Override
	protected ArrayList<Result> innerExecute(Connection con)
			throws AtlasServerException {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ArrayList<Result> results = new ArrayList<Result>();

		try {
			statement = con.prepareStatement(makeNormalStatment(byName, isBirth, userOriented));
			
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				
				// Create fetched User
				int personID = resultSet.getInt(DBConstants.Person.PERSON_ID);
				String name = resultSet.getString(DBConstants.PersonLabels.LABEL);
				String geoname = resultSet.getString(DBConstants.Location.GEO_NAME); 
				java.util.Date bornOn = resultSet.getDate(DBConstants.Person.BORN_ON_DATE); 
				java.util.Date diedOn =resultSet.getDate(DBConstants.Person.DIED_ON_DATE);
				String locUrl= resultSet.getString(DBConstants.Location.WIKI_URL);
				String personUrl = resultSet.getString(DBConstants.Person.WIKI_URL);
				double lng = resultSet.getDouble(DBConstants.Location.LONG);
				double lat = resultSet.getDouble(DBConstants.Location.LAT);
				boolean isFemale = resultSet.getBoolean(DBConstants.Person.IS_FEMALE);
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

		logger.info(String.format("Query executed properly.",
				statement.toString()));
		return results;
	}
	
	
	private String makeNormalStatment(boolean byName, boolean isBirth, boolean isUserOriented) {
		if (!byName) {
		String bornOrDiedDate = (isBirth ? DBConstants.Person.BORN_ON_DATE : DBConstants.Person.DIED_ON_DATE);
		
		String select = String.format(
				"SELECT (%s, %s, %s, %s, %s, %s as LocURL, %s as PersonURL, %s, %s, %s \n",
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
		
		String basicFrom = 
				String.format(
				"FROM %s, %s, %s ,%s, %s",
				// All Tables needed
				DBConstants.Person.TABLE_NAME,
				DBConstants.Location.TABLE_NAME,
				DBConstants.PersonHasCategory.TABLE_NAME,
				DBConstants.PersonLabels.TABLE_NAME,
				DBConstants.Category.TABLE_NAME);

		String withFavoritesTable = ", "+DBConstants.UserFavorites.TABLE_NAME;
		
		String favoritesFrom = basicFrom + withFavoritesTable;
		
		String basicWhere =
				"\n" +
				"WHERE "+ DBConstants.Category.CATEGORY_NAME		+" = '"+ this.category						 + "' \n"+
				"AND "  + DBConstants.PersonHasCategory.CATEGORY_ID +" = " + DBConstants.Category.CATEGORY_ID	 + " \n" +
				"AND "  + DBConstants.PersonHasCategory.PERSON_ID 	+" = " + DBConstants.Person.PERSON_ID 	  	 + " \n" +
				"AND "  + DBConstants.Person.DIED_IN_LOCATION 		+" = " + DBConstants.Location.GEO_ID		 + " \n" +
				"AND "  + DBConstants.Person.PERSON_ID				+" = " + DBConstants.PersonLabels.PERSON_ID	 + " \n" +
				"AND "  + DBConstants.PersonLabels.IS_PREFERED	    +" = '1' \n" +
				"AND year("+ bornOrDiedDate +") >= '"+this.startYear+"' \n" +
				"AND year("+ bornOrDiedDate +") <= '"+this.endYear  +"' \n";
		
		String withoutFavoritesAndUserAddedWhere = 
				"AND " + DBConstants.Person.PERSON_ID +" <> " + DBConstants.UserFavorites.PERSON_ID	 + " \n" +
				"AND " + DBConstants.PersonHasCategory.CATEGORY_ID +" <> " + Main.user.getUserID()	 + " \n" +
				"AND " + DBConstants.Person.ADDED_BY_USER +" <> " + Main.user.getUserID() + " \n";
		
		String withFavoritesWhere = 
				"AND " + DBConstants.Person.PERSON_ID +" = " + DBConstants.UserFavorites.PERSON_ID	 + " \n" +
				"AND " + DBConstants.PersonHasCategory.CATEGORY_ID +" = " + Main.user.getUserID()	 + " \n" ;
		String withUserAddedWhere = 
				"AND " + DBConstants.Person.ADDED_BY_USER +" = " + Main.user.getUserID() + " \n";
	
		
		String FavsWhere = basicWhere + withFavoritesWhere;
		String UserAddedWhere = basicWhere + withUserAddedWhere;
		String limit = "limit " + this.limitNumOfResults;
		
		if (isUserOriented) {
			String q1 = select + favoritesFrom + FavsWhere + limit;
			String q2 = select + basicFrom + UserAddedWhere + limit;
			return q1 + "\n UNION \n" + q2;
		} else {
			return select + basicFrom + basicWhere + withoutFavoritesAndUserAddedWhere + limit;
		}
		} else {
			//TODO byName
			return "";
		}
	}

}
