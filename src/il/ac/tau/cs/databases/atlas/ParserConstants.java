package il.ac.tau.cs.databases.atlas;

import java.util.HashMap;
import java.util.Map;

public class ParserConstants {
    public static final String YAGO_DATE_FACTS_TSV = "yagoDateFacts.tsv";
    public static final String YAGO_FACTS_TSV = "yagoFacts.tsv";
    public static final String YAGO_TRANSITIVE_TYPE_TSV = "yagoTransitiveType.tsv";
    public static final String YAGO_LABELS_TSV = "yagoLabels.tsv";
    public static final String YAGO_WIKIPEDIA_INFO_TSV = "yagoWikipediaInfo.tsv";
    public static final String YAGO_LITERAL_FACTS_TSV = "yagoLiteralFacts.tsv";
    public static final String YAGO_GEONAMES_ENTITY_IDS_TSV = "yagoGeonamesEntityIds.tsv";
    public static final String CITIES1000_TXT = "cities1000.txt";
    public static final String[] REQUIRED_FILES = {
            YAGO_DATE_FACTS_TSV,
            YAGO_FACTS_TSV,
            YAGO_TRANSITIVE_TYPE_TSV,
            YAGO_LABELS_TSV,
            YAGO_WIKIPEDIA_INFO_TSV,
            YAGO_LITERAL_FACTS_TSV,
            YAGO_GEONAMES_ENTITY_IDS_TSV,
            CITIES1000_TXT};

    public static final String GEONAMES_URL_REGEX = "http://sws.geonames.org/([0-9]+)";
    public static final String DATE_REGEX = "[0-9][0-9][0-9][0-9]-[#0-9][#0-9]-[#0-9][#0-9]";
    public static final String CATEGORY_REGEX = "<wordnet_(.+)_[0-9]+>";
    public static final String LABEL_REGEX = "\"(.*)\"((@eng)?)";
    public static final String WIKI_REGEX = "<http://en\\.wikipedia\\.org/wiki/(.*)>";

    public static final Map<String, Integer> CATEGORY_TYPES = new HashMap<>();
    static {
        CATEGORY_TYPES.put("<wordnet_scientist_110560637>", 1);
        CATEGORY_TYPES.put("<wordnet_philosopher_110423589>", 2);
        CATEGORY_TYPES.put("<wordnet_politician_110450303>", 3);
        CATEGORY_TYPES.put("<wordnet_composer_109947232>", 4);
        CATEGORY_TYPES.put("<wordnet_football_player_110101634>", 5);
        CATEGORY_TYPES.put("<wordnet_monarchist_110327824>", 6);
        CATEGORY_TYPES.put("<wordnet_poet_110444194>", 7);
        CATEGORY_TYPES.put("<wordnet_medalist_110305062>", 8);
    }
}
