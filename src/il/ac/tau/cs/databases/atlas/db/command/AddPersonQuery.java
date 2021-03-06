package il.ac.tau.cs.databases.atlas.db.command;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.exception.PersonExistsError;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.command.base.BaseDBCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Transaction for adding a new person to the DB.
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
			
			 /** Verify that this person does not exist **/
			statement = con.prepareStatement(
					"SELECT COUNT(*) FROM "
					+ DBConstants.Person.TABLE_NAME
					+ " WHERE prefLabel = ?");
			statement.setString(1, name);
			
			// Log sql statement
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			
			// Exectue statement
			resultSet = statement.executeQuery();
			
			// Check if number of persons with same name is 0!
			
			resultSet.next();
			if (resultSet.getInt(1) != 0) {
				throw new PersonExistsError(name);
			}
			statement.close();
			resultSet.close();

			 /** Prepare Transaction for inserting the person **/
			con.setAutoCommit(false);
			
			// Insert to person table
			if (deathDate == null && deathLocID == null) {
				// If this person does not have death details
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
				// If this person has death details
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
			
			// Log sql query
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			
			// Exectue
			statement.executeUpdate();
			
			//Get generated ID for other insertions.
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
            
            // Log sql query
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
			
			// Log sql query
			logger.info(String.format("Executing DB query: %s.",
					statement.toString()));
			statement.executeUpdate();
			
			// Complete transaction - commit if no errors occurred.
			con.commit();

            logger.info("Person added successfully - The generated person ID is: "+ genID);
		} catch (PersonExistsError e) {
			logger.error("Person exists error", e);
    			throw e;
		} catch (SQLException e) {
			logger.error("Sql error", e);
			rollback(con);
			throw new AtlasServerException(e.getMessage());
		} finally {
			safelyClose(statement, resultSet);
			safelyResetAutoCommit(con);
		}
		logger.info("Query executed properly.");
		return null;
	}

}
