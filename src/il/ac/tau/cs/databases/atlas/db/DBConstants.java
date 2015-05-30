package il.ac.tau.cs.databases.atlas.db;

public class DBConstants {
	
	/* Shared Labels (Between schemas)
	 * Here so we will only change a String once when needed
	 */
	private static final String PERSON_ID = "person_ID";
	private static final String BORN_IN_LOCATION = "wasBornInLocation";
	private static final String BORN_ON_DATE = "wasBornOnDate";
	private static final String USER_ID = "user_ID";
	private static final String CATEGORY_ID = "category_ID";
	private static final String WIKI_URL = "wikiURL";
	
	public class UserSchema {
		public static final String SCHEMA_NAME = "user";
		public static final String USERNAME_FIELD = "username";
		public static final String PASSWORD_FIELD = "password";
		public static final String BORN_ON_DATE_FIELD = BORN_ON_DATE;
		public static final String BORN_IN_LOCATION_FIELD = BORN_IN_LOCATION;
		public static final String USER_ID_FIELD = USER_ID;
	}
	
	public class UserFavoritesSchema {
		public static final String SCHEMA_NAME = "user_favorites";
		public static final String USER_ID_FIELD = USER_ID;
		public static final String PERSON_ID_FIELD = PERSON_ID;
	}
	
	public class PersonHasCategorySchema {
		public static final String SCHEMA_NAME = "person_has_category";
		public static final String CATEGORY_ID_FIELD = CATEGORY_ID;
		public static final String PERSON_ID_FIELD = PERSON_ID;
	}
	
	public class PersonSchema {
		public static final String SCHEMA_NAME = "person";
		public static final String PERSON_ID_FIELD = "person_ID";
		public static final String WIKI_URL_FIELD = WIKI_URL;
		public static final String DIED_ON_DATE_FIELD = "diedOnDate";
		public static final String BORN_ON_DATE_FIELD = BORN_ON_DATE;
		public static final String BORN_IN_LOCATION_FIELD = BORN_IN_LOCATION;
		public static final String DIED_IN_LOCATION_FIELD = "diedInLocation";
		public static final String ADDED_BY_USER_FIELD = "addedByUser";
		public static final String IS_FEMALE_FIELD = "isFemale";
	}
	
	public class CategorySchema {
		public static final String SCHEMA_NAME = "category";
		public static final String CATEGORY_ID_FIELD = CATEGORY_ID;
		public static final String CATEGORY_NAME_FIELD = "categoryName";
	}
	
	public class PersonLabelsSchema {
		public static final String SCHEMA_NAME = "pesron_labels";
		public static final String PERSON_ID_FIELD = PERSON_ID;
		public static final String IS_PREFERED_FIELD = "isPref";	
	}
	
	public class LocationSchema {
		public static final String SCHEMA_NAME = "location";
		public static final String GEO_NAME_FIELD = "geo_name";
		public static final String LONG_FIELD = "latitue";
		public static final String LAT_FIELD = "longtitude";
		public static final String WIKI_URL_FIELD = WIKI_URL;
		public static final String GEO_ID_FIELD = "geo_ID";
		public static final String YAGO_ID_FIELD = "yago_ID";
	}
}
