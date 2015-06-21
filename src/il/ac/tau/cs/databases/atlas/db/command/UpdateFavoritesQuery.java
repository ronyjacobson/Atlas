package il.ac.tau.cs.databases.atlas.db.command;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.command.base.BaseDBCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Transaction query to sync the users favorites.
 * This query will remove or add person's to the current user's favorites as he selected.
 */
public class UpdateFavoritesQuery extends BaseDBCommand<Void> {
	List<String> favoritesList;
	List<String> removeFromFavoritesList;

	public UpdateFavoritesQuery(List<String> favoritesList, List<String> removeFromFavorits) {
		this.favoritesList= favoritesList;
		this.removeFromFavoritesList= removeFromFavorits;
	}

	@Override
	protected Void innerExecute(Connection con) throws AtlasServerException {
		PreparedStatement addStmt = null;
		PreparedStatement removeStmt = null;
		try {
			// Generate add to favorites stmt
            con.setAutoCommit(false);
            addStmt = con.prepareStatement(String.format("INSERT IGNORE INTO %s (%s, %s) VALUES (?,?)",
            		DBConstants.UserFavorites.TABLE_NAME,
            		DBConstants.PERSON_ID_L,
            		DBConstants.USER_ID_L));
             
            for (String fav : favoritesList) {
            	if (fav == null || fav.equals("")) {
            		continue;
            	}
            	addStmt.setInt(1, Integer.parseInt(fav));
                addStmt.setInt(2, Main.user.getUserID());
                addStmt.addBatch();
            }
            
            logger.info(String.format("Executing DB query:\n %s.",
					addStmt.toString()));
            addStmt.executeBatch();
            
            // Generate remove from favorites stmt
            removeStmt = con.prepareStatement(String.format(
            		"DELETE FROM %s WHERE %s=? AND %s=?",
            		DBConstants.UserFavorites.TABLE_NAME,
            		DBConstants.PERSON_ID_L,
            		DBConstants.USER_ID_L));
             
            for (String rmFav : removeFromFavoritesList) {
            	if (rmFav == null || rmFav.equals("")) {
            		continue;
            	}
            	removeStmt.setInt(1, Integer.parseInt(rmFav));
            	removeStmt.setInt(2, Main.user.getUserID());
            	removeStmt.addBatch();
            }
            logger.info(String.format("Executing DB query:\n %s.",
            		removeStmt.toString()));
            removeStmt.executeBatch();
            
            con.commit();
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			rollback(con);
			throw new AtlasServerException(e.getMessage());
		} finally {
			safelyClose(addStmt, removeStmt);
			safelyResetAutoCommit(con);
		}
		logger.info("Query executed properly.");
		return null;
	}

}
