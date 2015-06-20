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
	
    // Last SQL query executed details
    private String lastResultQueryExecuted="";
    public int numOfLatestResults = 0;
	public int numOfFemaleResults = 0;
	public int numOfBirthResults = 0;
    
    

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

	public void incNumOfResults() {
		this.numOfLatestResults++;
		
	}

	public void incNumOfBirths() {
		this.numOfBirthResults++;
		
	}

	public void incNumOfFemales() {
		this.numOfFemaleResults++;
		
	}
	
	public int getNumOfLatestResults() {
		return numOfLatestResults;
	}

	public int getNumOfFemaleResults() {
		return numOfFemaleResults;
	}

	public int getNumOfBirthResults() {
		return numOfBirthResults;
	}

	public void resetCounters() {
		this.numOfBirthResults=0;
		this.numOfFemaleResults=0;
		this.numOfLatestResults=0;
	}
}
