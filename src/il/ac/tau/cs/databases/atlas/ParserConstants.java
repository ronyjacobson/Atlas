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
        CATEGORY_TYPES.put("<wordnet_journalist_110224578>", 9);
        CATEGORY_TYPES.put("<wordnet_musician_110339966>", 10);
        CATEGORY_TYPES.put("<wordnet_athlete_109820263>", 11);
        CATEGORY_TYPES.put("<wordnet_soldier_110622053>", 12);
        CATEGORY_TYPES.put("<wordnet_legislator_110253995>", 13);
        CATEGORY_TYPES.put("<wordnet_painter_110391653>", 14);
        CATEGORY_TYPES.put("<wordnet_writer_110794014>", 15);
        CATEGORY_TYPES.put("<wordnet_entertainer_109616922>", 16);
        CATEGORY_TYPES.put("<wordnet_comedian_109940146>", 17);
        CATEGORY_TYPES.put("<wordnet_performer_110415638>", 18);
        CATEGORY_TYPES.put("<wordnet_military_officer_110317007>", 19);
        CATEGORY_TYPES.put("<wordnet_novelist_110363573>", 20);
        CATEGORY_TYPES.put("<wordnet_singer_110599806>", 21);
        CATEGORY_TYPES.put("<wordnet_entertainer_109616922>", 22);
        CATEGORY_TYPES.put("<wordnet_doctor_110020890>", 23);
        CATEGORY_TYPES.put("<wordnet_engineer_109615807>", 24);
        CATEGORY_TYPES.put("<wordnet_actor_109765278>", 25);
        CATEGORY_TYPES.put("<wordnet_film_maker_110088390>", 26);
        CATEGORY_TYPES.put("<wordnet_historian_110177150>", 27);
        CATEGORY_TYPES.put("<wordnet_businessperson_109882716>", 28);
        CATEGORY_TYPES.put("<wordnet_sailor_110546633>", 29);
        CATEGORY_TYPES.put("<wordnet_revolutionist_110527334>", 30);

    }
}
