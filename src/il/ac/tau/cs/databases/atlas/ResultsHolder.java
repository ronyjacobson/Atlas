package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.db.Result;

import java.util.Map;

/**
 * Created by user on 19/06/2015.
 */
public enum ResultsHolder {
    INSTANCE;

    private Map<String, Result> resultMap;

    public Map<String, Result> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Result> resultMap) {
        this.resultMap = resultMap;
    }
}
