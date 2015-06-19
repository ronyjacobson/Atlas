package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.*;
import java.util.Date;

public class UpdatePersonQuery extends BaseDBCommand<Void> {
	private int personId;
	private String name;
	private String wikiLink;
	private boolean isFemale;
	private Date birthDate;
	private Date deathDate;
	private Long birthLocID;
	private Long deathLocID;

	public UpdatePersonQuery(int personId, String name, Date birthDate,
							 Long birthlocationID, Date deathDate, Long deathlocationID,
							 String wikiLink, boolean isFemale) {
		this.personId = personId;
		this.name = name;
		this.wikiLink = wikiLink;
		this.isFemale = isFemale;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		this.birthLocID = birthlocationID;
		this.deathLocID = deathlocationID;
	}

	@Override
	protected Void innerExecute(Connection con) throws AtlasServerException {
		try (PreparedStatement pstmt = con
				.prepareStatement("UPDATE person SET wikiURL = ?, diedOnDate = ?, wasBornOnDate = ?, wasBornInLocation = ?, diedInLocation = ?, isFemale = ?, prefLabel = ? WHERE person_ID = ?")) {
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

			logger.info(String.format("Executing DB query: %s.",
					pstmt.toString()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Failed to update 'person' table", e);
			throw new AtlasServerException("Failed to update 'person' table");
		}
		logger.info("Query executed properly.");
		return null;
	}
}
