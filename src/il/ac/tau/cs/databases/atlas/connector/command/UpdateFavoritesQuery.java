package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 22/05/2015.
 */
public class UpdateFavoritesQuery extends BaseDBCommand<Void> {
	List<String> favoritesList;


	public UpdateFavoritesQuery(List<String> favoritesList) {
		this.favoritesList= favoritesList;
	}

	@Override
	protected Void innerExecute(Connection con) throws AtlasServerException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			
            con.setAutoCommit(false);
            statement = con.prepareStatement(String.format("INSERT IGNORE INTO %s (%s, %s) VALUES (?,?)",
            		DBConstants.UserFavorites.TABLE_NAME,
            		DBConstants.PERSON_ID_L,
            		DBConstants.USER_ID_L));
             
            for (String fav : favoritesList) {
            	if (fav == null) {
            		continue;
            	}
            	statement.setInt(1, Integer.parseInt(fav));
                statement.setInt(2, Main.user.getUserID());
                statement.addBatch();
            }
            
            System.out.println(String.format("Executing DB query: %s.",
					statement.toString()));
            statement.executeBatch();
            con.commit();
			
		} catch (SQLException e) {
				e.printStackTrace();
				throw new AtlasServerException(e.getMessage());
		} finally {
			safelyClose(statement, resultSet);
		}
		System.out.println("Query executed properly.");
		return null;
	}
}
