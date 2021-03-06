package il.ac.tau.cs.databases.atlas.db.command;

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
 * Query for updating and editing an existing person from the DB.
 *
 */
public class UpdatePersonQuery extends BaseDBCommand<Void> {
	private int personId;
	private String name;
	private String wikiLink;
	private boolean isFemale;
	private Date birthDate;
	private Date deathDate;
	private Long birthLocID;
	private Long deathLocID;
	private boolean checkIfPersonExists;

	public UpdatePersonQuery(
			int personId, String name, Date birthDate,
			Long birthlocationID, Date deathDate, Long deathlocationID,
			String wikiLink, boolean isFemale, boolean checkIfPersonExists) {
		this.personId = personId;
		this.name = name;
		this.wikiLink = wikiLink;
		this.isFemale = isFemale;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		this.birthLocID = birthlocationID;
		this.deathLocID = deathlocationID;
		this.checkIfPersonExists = checkIfPersonExists;
	}

	@Override
	protected Void innerExecute(Connection con) throws AtlasServerException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE " + DBConstants.Person.TABLE_NAME +
						" SET wikiURL = ?, diedOnDate = ?, wasBornOnDate = ?, " +
						"wasBornInLocation = ?, diedInLocation = ?, isFemale = ?, " +
						"prefLabel = ? WHERE person_ID = ?")) {

			if (checkIfPersonExists) {
				statement = con.prepareStatement(
						"SELECT COUNT(*) FROM "
								+ DBConstants.Person.TABLE_NAME
								+ " WHERE prefLabel = ?");
				statement.setString(1, name);

				logStatement(statement);
				resultSet = statement.executeQuery();

				resultSet.next();
				if (resultSet.getInt(1) != 0) {
					throw new PersonExistsError(name);
				}

				statement.close();
				resultSet.close();
			}

			// start update person
			con.setAutoCommit(false);

			String wikiUrl = wikiLink;
			if (wikiUrl == null) {
				pstmt.setNull(1, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(1, wikiUrl);
			}

			if (deathDate == null) {
				pstmt.setNull(2, java.sql.Types.DATE);
			} else {
				pstmt.setDate(2, new java.sql.Date(deathDate.getTime()));
			}

			pstmt.setDate(3, new java.sql.Date(birthDate.getTime()));

			pstmt.setLong(4, birthLocID);

			if (deathLocID == null) {
				pstmt.setNull(5, java.sql.Types.BIGINT);
			} else {
				pstmt.setLong(5, deathLocID);
			}

			pstmt.setBoolean(6, isFemale);
			pstmt.setString(7, name);
			pstmt.setInt(8, personId);

			logStatement(pstmt);
			pstmt.executeUpdate();
			pstmt.close();

			// Set label
			statement = con.prepareStatement(String.format(
							"INSERT IGNORE INTO %s (%s, %s) VALUES (?, ?)",
							DBConstants.PersonLabels.TABLE_NAME,
							DBConstants.PERSON_ID_L,
							DBConstants.LABEL_L)
			);
			statement.setInt(1, personId);
			statement.setString(2, name);
			statement.executeUpdate();

			logStatement(statement);

			con.commit();

		} catch (SQLException e) {
			logger.error("Failed to update '" + DBConstants.Person.TABLE_NAME + "' table", e);
			rollback(con);
			throw new AtlasServerException("Failed to update '" + DBConstants.Person.TABLE_NAME + "' table");
		} finally {
			safelyClose(statement, resultSet);
			safelyResetAutoCommit(con);
		}
		logger.info("Query executed properly.");
		return null;
	}

	private void logStatement(PreparedStatement pstmt) {
		logger.info(String.format("Executing DB query: %s.", pstmt.toString()));
	}
}
