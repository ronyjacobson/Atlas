package il.ac.tau.cs.databases.atlas.connector.command.NewCommands;

import il.ac.tau.cs.databases.atlas.db.Result;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.util.Map;

/**
 * Created by user on 22/05/2015.
 */
public class GetGoResultsQuery extends GetResultsGeneralQuery {
	int startYear;
	int endYear;
	String category;
	
	public GetGoResultsQuery(int startYear, int endYear, String category) {
		super();
		this.startYear = startYear;
		this.endYear = endYear;
		this.category = category;
	}

	@Override
	protected String makeWhereStmt() {
		return super.makeWhereStmt() +
				" 	AND category.categoryName = '"+category+"' \n" +
				"	AND ((YEAR(person.wasBornOnDate) >= '"+ startYear +
				"' AND YEAR(person.wasBornOnDate) <= '"+ endYear +
				"') OR (YEAR(person.diedOnDate) >= '"+ startYear +
				"' AND YEAR(person.diedOnDate) <= '"+ endYear +"'))\n";
	}
	
	@Override
	protected boolean ExtraValidateResult(Result result) {
		int year = result.getYear();
		return ((year >= startYear) && (year <= endYear)); 
	}

}
