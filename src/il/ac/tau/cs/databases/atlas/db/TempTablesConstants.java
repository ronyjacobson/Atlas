package il.ac.tau.cs.databases.atlas.db;

import java.util.LinkedHashMap;

public class TempTablesConstants {
    public static final LinkedHashMap<String, TempTableMetadata> tempFields = new LinkedHashMap<>();


    static {
        LinkedHashMap<String, String> tempCategoryFields = new LinkedHashMap<>();
        tempCategoryFields.put("yago_person_id", "string");
        tempCategoryFields.put("category_name", "string");
        TempTableMetadata tempCategoryMetadata = new TempTableMetadata(YagoParser.CATEGORIES_INFO_OUT_NAME, tempCategoryFields);
        tempFields.put("tempCategoryTable", tempCategoryMetadata);

        LinkedHashMap<String, String> tempWikiFields = new LinkedHashMap<>();
        tempWikiFields.put("yago_id", "string");
        tempWikiFields.put("wikiUrl", "string");
        TempTableMetadata tempWikiMetadata = new TempTableMetadata(YagoParser.WIKI_INFO_OUT_NAME, tempWikiFields);
        tempFields.put("tempWikiTable", tempWikiMetadata);

        LinkedHashMap<String, String> tempGeoCitiesFields = new LinkedHashMap<>();
        tempGeoCitiesFields.put("geo_id", "int");
        tempGeoCitiesFields.put("location", "string");
        tempGeoCitiesFields.put("latitude", "double");
        tempGeoCitiesFields.put("longitude", "double");
        TempTableMetadata tempGeoCitiesMetadata = new TempTableMetadata(YagoParser.GEO_CITIES_INFO_OUT_NAME, tempGeoCitiesFields);
        tempFields.put("tempGeoCitiesTable", tempGeoCitiesMetadata);

        LinkedHashMap<String, String> tempGeoInfoFields = new LinkedHashMap<>();
        tempGeoInfoFields.put("yago_id", "string");
        tempGeoInfoFields.put("geo_id", "int");
        TempTableMetadata tempGeoInfoMetadata = new TempTableMetadata(YagoParser.GEO_INFO_OUT_NAME, tempGeoInfoFields);
        tempFields.put("tempGeoInfoTable", tempGeoInfoMetadata);

        LinkedHashMap<String, String> tempBornInFields = new LinkedHashMap<>();
        tempBornInFields.put("yago_person_id", "string");
        tempBornInFields.put("yago_location_id", "string");
        TempTableMetadata tempBornInMetadata = new TempTableMetadata(YagoParser.FACTS_BORN_IN_LOCATION_OUT_NAME, tempBornInFields);
        tempFields.put("tempBornInTable", tempBornInMetadata);


        LinkedHashMap<String, String> tempBornOnFields = new LinkedHashMap<>();
        tempBornOnFields.put("yago_person_id", "string");
        tempBornOnFields.put("bornOnData", "date");
        TempTableMetadata tempBornOnMetadata = new TempTableMetadata(YagoParser.DATE_BORN_ON_DATE_OUT_NAME, tempBornOnFields);
        tempFields.put("tempBornOnTable", tempBornOnMetadata);

        LinkedHashMap<String, String> tempDiedInFields = new LinkedHashMap<>();
        tempDiedInFields.put("yago_person_id", "string");
        tempDiedInFields.put("yago_location_id", "string");
        TempTableMetadata tempDiedInMetadata = new TempTableMetadata(YagoParser.FACTS_DIED_IN_LOCATION_OUT_NAME, tempDiedInFields);
        tempFields.put("tempDiedInTable", tempDiedInMetadata);

        LinkedHashMap<String, String> tempDiedOnFields = new LinkedHashMap<>();
        tempDiedOnFields.put("yago_person_id", "string");
        tempDiedOnFields.put("diedOnData", "date");
        TempTableMetadata tempDiedOnMetadata = new TempTableMetadata(YagoParser.DATE_DIED_ON_DATE_OUT_NAME, tempDiedOnFields);
        tempFields.put("tempDiedOnTable", tempDiedOnMetadata);

        LinkedHashMap<String, String> tempGenderFields = new LinkedHashMap<>();
        tempGenderFields.put("yago_person_id", "string");
        tempGenderFields.put("is_female", "bool");
        TempTableMetadata tempGenderMetadata = new TempTableMetadata(YagoParser.FACTS_GENDER_OUT_NAME, tempGenderFields);
        tempFields.put("tempGenderTable", tempGenderMetadata);

        LinkedHashMap<String, String> tempLabelsFields = new LinkedHashMap<>();
        tempLabelsFields.put("yago_person_id", "string");
        tempLabelsFields.put("label", "string");
        TempTableMetadata tempLabelsMetadata = new TempTableMetadata(YagoParser.LABELS_INFO_OUT_NAME, tempLabelsFields);
        tempFields.put("tempLabelsTable", tempLabelsMetadata);

        LinkedHashMap<String, String> tempPrefLabelsFields = new LinkedHashMap<>();
        tempPrefLabelsFields.put("yago_person_id", "string");
        tempPrefLabelsFields.put("pref_label", "string");
        TempTableMetadata tempPrefLabelsMetadata = new TempTableMetadata(YagoParser.PREF_LABELS_INFO_OUT_NAME, tempPrefLabelsFields);
        tempFields.put("tempPrefLabelsTable", tempPrefLabelsMetadata);
    }

}
