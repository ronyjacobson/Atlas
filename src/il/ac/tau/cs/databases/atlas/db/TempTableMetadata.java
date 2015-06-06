package il.ac.tau.cs.databases.atlas.db;

import java.util.LinkedHashMap;

public class TempTableMetadata {
    String dataFilePath;
    LinkedHashMap<String, String> fields;

    public TempTableMetadata(String dataFilePath, LinkedHashMap<String, String> fields) {
        this.dataFilePath = dataFilePath;
        this.fields = fields;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public LinkedHashMap<String, String> getFields() {
        return fields;
    }

    public void setFields(LinkedHashMap<String, String> fields) {
        this.fields = fields;
    }
}
