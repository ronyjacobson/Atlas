package il.ac.tau.cs.databases.atlas.db.command.NewCommands;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.core.modal.Result;
import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;

import java.sql.Connection;
import java.util.Map;

/**
 * Created by user on 22/05/2015.
 */
public class GetNewFavoritesResultsQuery extends GetResultsGeneralQuery {

	protected Map<String, Result> innerExecute(Connection con)
			throws AtlasServerException {
		return super.innerExecute(con);
	}

	@Override
	protected String makeFromStmt() {
		return super.makeFromStmt() + "	, user_favorites \n";
	}

	@Override
	protected String makeWhereStmt() {
		return super.makeWhereStmt() + 
				"		AND user_favorites.user_ID = '"+ Main.user.getUserID() + "'\n" 
				+ "		AND person.person_ID = user_favorites.person_ID\n";
	}
	
	@Override
	protected boolean ExtraValidateResult(Result result) {
		// Adjust Max year/Min Year to set in timeline
		setMaxMinYear(result);
		return true;
	}



}
