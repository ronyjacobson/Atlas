package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by user on 22/05/2015.
 */
public class AddPersonQuery extends BaseDBCommand<Void> {
	private String name;
	private Integer categoryId;
	private String wikiLink;
	private boolean isFemale;
	private Date birthDate;
	private Date deathDate;
	private Integer birthLocID;
	private Integer deathLocID;

	public AddPersonQuery(String name, Integer categoryId, Date birthDate,
			Integer birthlocationID, Date deathDate, Integer deathlocationID,
			String wikiLink, boolean isFemale) {
		this.name = name;
		this.categoryId = categoryId;
		this.wikiLink = wikiLink;
		this.isFemale = isFemale;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		this.birthLocID = birthlocationID;
		this.deathLocID = deathlocationID;
	}

	@Override
	protected Void innerExecute(Connection con) throws AtlasServerException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {

			// Assert that this person does not exits
			statement = con.prepareStatement(
					"SELECT COUNT(*) FROM "
					+ DBConstants.PersonLabels.TABLE_NAME
					+ " WHERE label = ?");
			statement.setString(1, name);
			
			System.out.println(String.format("Executing DB query: %s.",
					statement.toString()));
			resultSet = statement.executeQuery();
			
			resultSet.next();
			if (resultSet.getInt(1) != 0) {
				throw new AtlasServerException(String.format("Person %s already exists", name));
			}
			
			statement.close();
			resultSet.close();
			
			// Add the new person to DB
			statement = con.prepareStatement(
					String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?)",
							 DBConstants.Person.TABLE_NAME
							 , DBConstants.WIKI_URL_L
							 , DBConstants.DIED_ON_DATE_L
							 , DBConstants.BORN_ON_DATE_L
							 , DBConstants.ADDED_BY_USER_L
							 , DBConstants.BORN_IN_LOCATION_L
							 , DBConstants.DIED_IN_LOCATION_L
							 , DBConstants.IS_FEMALE_L)
					, new String[]{DBConstants.Person.PERSON_ID});
			statement.setString(1, wikiLink);
			statement.setDate(2, new java.sql.Date(deathDate.getTime()));
			statement.setDate(3, new java.sql.Date(birthDate.getTime()));
			statement.setInt(4, Main.user.getUserID());
			statement.setInt(5, birthLocID);
			statement.setInt(6, deathLocID);
			statement.setBoolean(7, isFemale);
			
			System.out.println(String.format("Executing DB query: %s.",
					statement.toString()));
			
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			resultSet.next();
            int genID = resultSet.getInt(1);
            statement.close();
            
            // Add Label relation:
            statement = con.prepareStatement(String.format(
            		"INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
					DBConstants.PersonLabels.TABLE_NAME,
					DBConstants.PERSON_ID_L,
					DBConstants.LABEL_L,
					DBConstants.IS_PREFERED_L)
					);            
            statement.setInt(1, genID);
            statement.setString(2, name);
            statement.setBoolean(3, true);
            
            System.out.println(String.format("Executing DB query: %s.",
					statement.toString()));
			statement.executeUpdate();
			statement.close();
            
			 // Add category relation:
            statement = con.prepareStatement(String.format(
					"INSERT INTO %s (%s, %s) VALUES(?, ?)",
					DBConstants.PersonHasCategory.TABLE_NAME,
					DBConstants.CATEGORY_ID_L,
					DBConstants.PERSON_ID_L)
					);
			statement.setInt(1, categoryId);
			statement.setInt(2, genID);
			
			System.out.println(String.format("Executing DB query: %s.",
					statement.toString()));
			statement.executeUpdate();

            System.out.println("Person added successfully - The generated person ID is: "+ genID);
			
		} catch (SQLException|AtlasServerException e) {
				e.printStackTrace();
				throw new AtlasServerException(e.getMessage());
		} finally {
			safelyClose(statement, resultSet);
		}
		System.out.println("Query executed properly.");
		return null;
	}
}
