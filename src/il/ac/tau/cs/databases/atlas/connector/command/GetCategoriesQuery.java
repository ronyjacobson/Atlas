package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.db.Queries;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by user on 22/05/2015.
 */
public class GetCategoriesQuery extends BaseDBCommand<ArrayList<String>> {

    public GetCategoriesQuery() {
    }

	@Override
    protected ArrayList<String> innerExecute(Connection con) throws AtlasServerException {
    	PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<String> categories = new ArrayList<String>();
        try {
        	statement = con.prepareStatement(
        			"SELECT *"+ " FROM "+ DBConstants.Category.TABLE_NAME);
        	logger.info(String.format("Executing DB query: %s.", statement.toString()));
        	resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
            	String name = resultSet.getString(DBConstants.CATEGORY_NAME_L);
            	int id = resultSet.getInt(DBConstants.CATEGORY_ID_L);
            	Queries.categoriesMap.put(name, id);
            	categories.add(name);
            }

        } catch (SQLException e) {
        	// TODO -  handle Exception??
            e.printStackTrace();
        }
        finally {
            safelyClose(statement, resultSet);
        }
        
        logger.info(String.format("Query executed properly.", statement.toString()));
        return categories;
    }

}
