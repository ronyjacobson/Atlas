package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.Result;

import java.util.Map;

/**
 * Created by user on 19/06/2015.
 */
public enum ResultsHolder {
    INSTANCE;
    
    // Last fetched reuslts
    private Map<String, Result> resultMap;
	
    // Last SQL query executed
    private String lastResultQueryExecuted="";

    public String getLastResultQueryExecuted() {
		return lastResultQueryExecuted;
	}

	public void setLastResultQueryExecuted(String lastResultQueryExecuted) {
		this.lastResultQueryExecuted = lastResultQueryExecuted;
	}

	public Map<String, Result> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Result> resultMap) {
        this.resultMap = resultMap;
    }
}
