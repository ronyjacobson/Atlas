package il.ac.tau.cs.databases.atlas.db;

public class DBConstants {
	
	/* Shared Labels (Between TABLEs)
	 * Here so we will only change a String once when needed
	 */
	private static final String PERSON_ID_LABEL = ".person_ID";
	private static final String BORN_IN_LOCATION_LABEL = ".wasBornInLocation";
	private static final String BORN_ON_DATE_LABEL = ".wasBornOnDate";
	private static final String USER_ID_LABEL = ".user_ID";
	private static final String CATEGORY_ID_LABEL = ".category_ID";
	private static final String WIKI_URL_LABEL = ".wikiURL";
	
	public static class User {
		public static final String TABLE_NAME = "user";
		public static final String USERNAME = TABLE_NAME+".username";
		public static final String PASSWORD = TABLE_NAME+".password";
		public static final String BORN_ON_DATE = TABLE_NAME+BORN_ON_DATE_LABEL;
		public static final String BORN_IN_LOCATION = TABLE_NAME+BORN_IN_LOCATION_LABEL;
		public static final String USER_ID = TABLE_NAME+USER_ID_LABEL;
	}
	
	public static class UserFavorites {
		public static final String TABLE_NAME = "user_favorites";
		public static final String USER_ID = TABLE_NAME+USER_ID_LABEL;
		public static final String PERSON_ID = TABLE_NAME+PERSON_ID_LABEL;
	}
	
	public static class PersonHasCategory {
		public static final String TABLE_NAME = "person_has_category";
		public static final String CATEGORY_ID = TABLE_NAME+CATEGORY_ID_LABEL;
		public static final String PERSON_ID = TABLE_NAME+PERSON_ID_LABEL;
	}
	
	public static class Person {
		public static final String TABLE_NAME = "person";
		public static final String PERSON_ID = TABLE_NAME+".person_ID";
		public static final String WIKI_URL = TABLE_NAME+WIKI_URL_LABEL;
		public static final String DIED_ON_DATE = TABLE_NAME+".diedOnDate";
		public static final String BORN_ON_DATE = TABLE_NAME+BORN_ON_DATE_LABEL;
		public static final String BORN_IN_LOCATION = TABLE_NAME+BORN_IN_LOCATION_LABEL;
		public static final String DIED_IN_LOCATION = TABLE_NAME+".diedInLocation";
		public static final String ADDED_BY_USER = TABLE_NAME+".addedByUser";
		public static final String IS_FEMALE = TABLE_NAME+".isFemale";
	}
	
	public static class Category {
		public static final String TABLE_NAME = "category";
		public static final String CATEGORY_ID = TABLE_NAME+CATEGORY_ID_LABEL;
		public static final String CATEGORY_NAME = TABLE_NAME+".categoryName";
	}
	
	public static class PersonLabels {
		public static final String TABLE_NAME = "pesron_labels";
		public static final String PERSON_ID = TABLE_NAME+PERSON_ID_LABEL;
		public static final String LABEL = TABLE_NAME+".label";
		public static final String IS_PREFERED = TABLE_NAME+".isPref";	
	}
	
	public static class Location {
		public static final String TABLE_NAME = "location";
		public static final String GEO_NAME = TABLE_NAME+".geo_name";
		public static final String LONG = TABLE_NAME+".latitue";
		public static final String LAT = TABLE_NAME+".longtitude";
		public static final String WIKI_URL = TABLE_NAME+WIKI_URL_LABEL;
		public static final String GEO_ID = TABLE_NAME+".geo_ID";
		public static final String YAGO_ID = TABLE_NAME+".yago_ID";
	}
}
