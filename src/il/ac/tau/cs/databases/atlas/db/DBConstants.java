package il.ac.tau.cs.databases.atlas.db;

public class DBConstants {
	
	/* Shared Labels (Between TABLEs)
	 * Here so we will only change a String once when needed
	 */
	public static final int LIMIT = 100;
	public static final String PERSON_ID_L = "person_ID";
	public static final String PERSON_YAGO_ID_L = "yago_ID";
	public static final String BORN_IN_LOCATION_L = "wasBornInLocation";
	public static final String BORN_ON_DATE_L = "wasBornOnDate";
	public static final String USER_ID_L = "user_ID";
	public static final String CATEGORY_ID_L = "category_ID";
	public static final String WIKI_URL_L = "wikiURL";
	public static final int WIKI_URL_SIZE = 200;
	public static final String USERNAME_L = "username";
	public static final int USERNAME_SIZE = 16;
	public static final String PASSWORD_L = "password";
	public static final int PASSWORD_SIZE = 32;
	public static final String DIED_ON_DATE_L = "diedOnDate";
	public static final String GEO_NAME_L = "geo_name";
	public static final int GEO_NAME_SIZE = 200;
	public static final String LONG_L = "latitude";
	public static final String LAT_L = "longitude";
	public static final String LOCATION_ID_L = "location_ID";
	public static final String YAGO_ID_L = "yago_ID";
	public static final String LABEL_L = "label";
	public static final int LABEL_SIZE = 200;
	public static final String PREF_LABEL_L = "prefLabel";
	public static final int PREF_LABEL_SIZE = 200;
	public static final String DIED_IN_LOCATION_L = "diedInLocation";
	public static final String ADDED_BY_USER_L = "addedByUser";
	public static final String IS_FEMALE_L = "isFemale";
	public static final String CATEGORY_NAME_L = "categoryName";
	
	public static class User {
		public static final String TABLE_NAME = "user";
		public static final String USERNAME = TABLE_NAME + "." + USERNAME_L;
		public static final String PASSWORD = TABLE_NAME + "." + PASSWORD_L;
		public static final String BORN_ON_DATE = TABLE_NAME + "." + BORN_ON_DATE_L;
		public static final String BORN_IN_LOCATION = TABLE_NAME + "." + BORN_IN_LOCATION_L;
		public static final String USER_ID = TABLE_NAME + "." + USER_ID_L;
	}
	
	public static class UserFavorites {
		public static final String TABLE_NAME = "user_favorites";
		public static final String USER_ID = TABLE_NAME + "." + USER_ID_L;
		public static final String PERSON_ID = TABLE_NAME + "." + PERSON_ID_L;
	}
	
	public static class PersonHasCategory {
		public static final String TABLE_NAME = "person_has_category";
		public static final String CATEGORY_ID = TABLE_NAME + "." + CATEGORY_ID_L;
		public static final String PERSON_ID = TABLE_NAME + "." + PERSON_ID_L;
	}
	
	public static class Person {
		public static final String TABLE_NAME = "person";
		public static final String PERSON_ID = TABLE_NAME + "." + PERSON_ID_L;
		public static final String WIKI_URL = TABLE_NAME + "." + WIKI_URL_L;
		public static final String DIED_ON_DATE = TABLE_NAME + "." + DIED_ON_DATE_L;
		public static final String BORN_ON_DATE = TABLE_NAME + "." + BORN_ON_DATE_L;
		public static final String BORN_IN_LOCATION = TABLE_NAME + "." + BORN_IN_LOCATION_L;
		public static final String DIED_IN_LOCATION = TABLE_NAME + "." + DIED_IN_LOCATION_L;
		public static final String ADDED_BY_USER = TABLE_NAME + "." + ADDED_BY_USER_L;
		public static final String IS_FEMALE = TABLE_NAME + "." + IS_FEMALE_L;
		public static final String YAGO_ID = TABLE_NAME + "." + PERSON_YAGO_ID_L;
		public static final String PREF_LABEL = TABLE_NAME + "." + PREF_LABEL_L;
	}
	
	public static class Category {
		public static final String TABLE_NAME = "category";
		public static final String CATEGORY_ID = TABLE_NAME + "." + CATEGORY_ID_L;
		public static final String CATEGORY_NAME = TABLE_NAME + "." + CATEGORY_NAME_L;
	}
	
	public static class PersonLabels {
		public static final String TABLE_NAME = "person_labels";
		public static final String PERSON_ID = TABLE_NAME + "." + PERSON_ID_L;
		public static final String LABEL = TABLE_NAME + "." + LABEL_L;	
	}
	
	public static class Location {
		public static final String TABLE_NAME = "location";
		public static final String GEO_NAME = TABLE_NAME + "." + GEO_NAME_L;
		public static final String LONG = TABLE_NAME + "." + LONG_L ;
		public static final String LAT = TABLE_NAME + "." + LAT_L;
		public static final String LOCATION_ID = TABLE_NAME + "." + LOCATION_ID_L;
		public static final String YAGO_ID = TABLE_NAME + "." + YAGO_ID_L;
		public static final String WIKI_URL = TABLE_NAME + "." + WIKI_URL_L;
	}
}
