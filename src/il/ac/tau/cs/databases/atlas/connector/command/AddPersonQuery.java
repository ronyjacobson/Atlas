package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.exception.PersonExistsError;

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
	private Long birthLocID;
	private Long deathLocID;

	public AddPersonQuery(String name, Integer categoryId, Date birthDate,
			Long birthlocationID, Date deathDate, Long deathlocationID,
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
			
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			resultSet = statement.executeQuery();
			
			resultSet.next();
			if (resultSet.getInt(1) != 0) {
				throw new PersonExistsError(name);
			}
			
			statement.close();
			resultSet.close();

			con.setAutoCommit(false);
			
			// Add the new person to DB
			if (deathDate == null && deathLocID == null) {
			statement = con.prepareStatement(
					String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?)",
							 DBConstants.Person.TABLE_NAME
							 , DBConstants.WIKI_URL_L
							 , DBConstants.BORN_ON_DATE_L
							 , DBConstants.ADDED_BY_USER_L
							 , DBConstants.BORN_IN_LOCATION_L
							 , DBConstants.IS_FEMALE_L
							 , DBConstants.PREF_LABEL_L)
					, new String[]{DBConstants.Person.PERSON_ID});
			} else {
				statement = con.prepareStatement(
						String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
								 DBConstants.Person.TABLE_NAME
								 , DBConstants.WIKI_URL_L
								 , DBConstants.BORN_ON_DATE_L
								 , DBConstants.ADDED_BY_USER_L
								 , DBConstants.BORN_IN_LOCATION_L
								 , DBConstants.IS_FEMALE_L
								 , DBConstants.PREF_LABEL_L
								 , DBConstants.DIED_ON_DATE_L
								 , DBConstants.DIED_IN_LOCATION_L)
						, new String[]{DBConstants.Person.PERSON_ID});

				statement.setDate(7, new java.sql.Date(deathDate.getTime()));
				statement.setLong(8, deathLocID);
			}
			statement.setString(1, wikiLink);
			statement.setDate(2, new java.sql.Date(birthDate.getTime()));
			statement.setInt(3, Main.user.getUserID());
			statement.setLong(4, birthLocID);
			statement.setBoolean(5, isFemale);
			statement.setString(6, name);
			
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			resultSet.next();
            int genID = resultSet.getInt(1);
            statement.close();
            
            // Add Label relation:
            statement = con.prepareStatement(String.format(
            		"INSERT INTO %s (%s, %s) VALUES (?, ?)",
					DBConstants.PersonLabels.TABLE_NAME,
					DBConstants.PERSON_ID_L,
					DBConstants.LABEL_L)
					);            
            statement.setInt(1, genID);
            statement.setString(2, name);
            
            logger.info(String.format("Executing DB query: %s.",
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
			
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			statement.executeUpdate();
			con.commit();

            logger.info("Person added successfully - The generated person ID is: "+ genID);
		} catch (PersonExistsError e) {
    			throw e;
		} catch (SQLException e) {
			throw new AtlasServerException(e.getMessage());
		} finally {
			safelyClose(statement, resultSet);
		}
		logger.info("Query executed properly.");
		return null;
	}
}
