package il.ac.tau.cs.databases.atlas.parser;


import org.json.JSONObject;

import java.io.*;

public class GeoParser {
    private JSONObject json;

    public GeoParser(String jsonStr) {
        json = new JSONObject(jsonStr);
    }

    public float getLangtitude() {
        return Float.parseFloat(json.getString("lng"));
    }

    public float getLongtitude() {
        return Float.parseFloat(json.getString("lat"));
    }

    public String getWikiLink() {
        return json.getString("wikipediaURL");
    }

    public static void main(String[] args) throws IOException {
        File file = new File("res/bizantion.json");
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String json = new String(data, "UTF-8");

        final GeoParser geoParser = new GeoParser(json);
        System.out.println(geoParser.getLangtitude());
        System.out.println(geoParser.getLongtitude());
        System.out.println(geoParser.getWikiLink());
    }
}
