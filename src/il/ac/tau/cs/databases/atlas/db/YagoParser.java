package il.ac.tau.cs.databases.atlas.db;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YagoParser {

    public static final String GEO_CITIES_INFO_OUT_NAME = "geo_cities_info.tsv";
    public static final String GEO_INFO_OUT_NAME = "geo_info.tsv";
    public static final String WIKI_INFO_OUT_NAME = "wiki_info.tsv";
    public static final String LABELS_INFO_OUT_NAME = "labels_info.tsv";
    public static final String GEONAMES_URL_REGEX = "http://sws.geonames.org/([0-9]+)";
    public static final String DATE_REGEX = "[0-9][0-9][0-9][0-9]-[#0-9][#0-9]-[#0-9][#0-9]";

    private final File yagoDateFile;
    private final File yagoLocationFile;
    private final File yagoCategoryFile;
    private final File yagoLabelsFile;
    private final File yagoWikiFile;
    private final File yagoGeonamesFile;
    private final File geonamesCitiesFile;
    private final String outputPath;
    private Set<String> dateTypes;
    private Set<String> locationTypes;
    private Set<String> categoryTypes;
    private Set<String> wikiTypes;
    private Set<String> labelTypes;

    private String concatToOutPath(String outFileName) {
        return outputPath + File.separator + outFileName;
    }

    public YagoParser(File yagoDateFile, File yagoLocationFile, File yagoCategoryFile, File yagoLabelsFile, File yagoWikiFile, File yagoGeonamesFile, File geonamesCitiesFile, String parserOutputPath) {
        this.yagoDateFile = yagoDateFile;
        this.yagoLocationFile = yagoLocationFile;
        this.yagoCategoryFile = yagoCategoryFile;
        this.yagoLabelsFile = yagoLabelsFile;
        this.yagoWikiFile = yagoWikiFile;
        this.yagoGeonamesFile = yagoGeonamesFile;
        this.geonamesCitiesFile = geonamesCitiesFile;
        this.outputPath = parserOutputPath;

        dateTypes = new HashSet<>();
        dateTypes.add("<wasBornOnDate>");
        dateTypes.add("<diedOnDate>");

        locationTypes = new HashSet<>();
        locationTypes.add("<wasBornIn>");
        locationTypes.add("<diedIn>");
        locationTypes.add("<hasGender>");

        categoryTypes = new HashSet<>();
        categoryTypes.add("<wordnet_scientist_110560637>");
        categoryTypes.add("<wordnet_philosopher_110423589>");
        categoryTypes.add("<wordnet_politician_110450303>");
        categoryTypes.add("<wordnet_composer_109947232>");
        categoryTypes.add("<wordnet_football_player_110101634>");
        categoryTypes.add("<wordnet_monarchist_110327824>");
        categoryTypes.add("<wordnet_poet_110444194>");
        categoryTypes.add("<wordnet_medalist_110305062>");
        categoryTypes.add("<wordnet_city_108524735>");

        wikiTypes = new HashSet<>();
        wikiTypes.add("<hasWikipediaUrl>");

        labelTypes = new HashSet<>();
        labelTypes.add("skos:prefLabel");
    }

    public void parseYagoDateFile(File yagoDateFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoDateFile));
        Pattern p = Pattern.compile(DATE_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (dateTypes.contains(cols[2])) {
                File outfile = new File(concatToOutPath("date_" + cols[2] + ".tsv"));
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                Matcher m = p.matcher(cols[3]);
                if (m.find()) {
                    pw.println(cols[0] + "\t" + cols[1] + "\t" + m.group());
                }
                pw.close();
            }
        }
        br.close();
    }

    public void parseYagoLocationFile(File yagoLocationFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLocationFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (locationTypes.contains(cols[2])) {
                File outfile = new File(concatToOutPath("location_" + cols[2] + ".tsv"));
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(cols[0] + "\t" + cols[1] + "\t" + cols[3]);
                pw.close();
            }
        }
        br.close();
    }

    public void parseYagoCategoryFile(File yagoCategoryFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoCategoryFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (categoryTypes.contains(cols[3])) {
                File outfile = new File(concatToOutPath("category_" + cols[3] + ".tsv"));
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(cols[0] + "\t" + cols[1]);
                pw.close();
            }
        }
        br.close();
    }

    public void parseYagoWikiFile(File yagoWiKiFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoWiKiFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (wikiTypes.contains(cols[1])) {
                File outfile = new File(concatToOutPath(WIKI_INFO_OUT_NAME));
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(cols[0] + "\t" + cols[2]);
                pw.close();
            }
        }
        br.close();
    }


    public void parseYagoGeonamesFile(File yagoGeonamesFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoGeonamesFile));
        Pattern p = Pattern.compile(GEONAMES_URL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            File outfile = new File(concatToOutPath(GEO_INFO_OUT_NAME));
            FileWriter fw = new FileWriter(outfile, true);
            PrintWriter pw = new PrintWriter(fw, true);
            Matcher m = p.matcher(cols[2]);
            if (m.find()) {
                pw.println(cols[0] + "\t" + m.group(1));
            }
            pw.close();
        }
        br.close();
    }

    public void parseGeonamesCitiesFile(File geoCitiesFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(geoCitiesFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            File outfile = new File(concatToOutPath(GEO_CITIES_INFO_OUT_NAME));
            FileWriter fw = new FileWriter(outfile, true);
            PrintWriter pw = new PrintWriter(fw, true);
            int[] columns = {0, 1, 2, 4};
            for (int i : columns) {
                pw.print(cols[i] + "\t");
            }
            pw.println(cols[5]);
            pw.close();
        }
        br.close();
    }

    public void parseYagoLabelsFile(File yagoLabelsFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLabelsFile));
        Pattern p = Pattern.compile("\"(.*)\"@eng");
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (labelTypes.contains(cols[2])) {
                File outfile = new File(LABELS_INFO_OUT_NAME);
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                Matcher m = p.matcher(cols[3]);
                if (m.find()) {
                    pw.println(cols[1] + "\t" + m.group(1));
                }
                pw.close();
            }
        }
        br.close();
    }

    public void parseFiles() throws IOException {
        validateFiles();
        System.out.println("Parser started");
        System.out.print("Parsing dates..");
        parseYagoDateFile(yagoDateFile);
        System.out.print("   Done\n");
        System.out.print("Parsing YAGO locations..");
        parseYagoLocationFile(yagoLocationFile);
        System.out.print("   Done\n");
        System.out.print("Parsing YAGO categories..");
        parseYagoCategoryFile(yagoCategoryFile);
        System.out.print("   Done\n");
        System.out.print("Parsing YAGO labels..");
        parseYagoLabelsFile(yagoLabelsFile);
        System.out.print("Parsing YAGO wikipedia info..");
        parseYagoWikiFile(yagoWikiFile);
        System.out.print("   Done\n");
        System.out.print("Parsing YAGO geonames info..");
        parseYagoGeonamesFile(yagoGeonamesFile);
        System.out.print("   Done\n");
        System.out.print("Parsing Geonames cities info..");
        parseGeonamesCitiesFile(geonamesCitiesFile);
        System.out.print("   Done\n");
        System.out.println("Parsing complete");
    }

    public static void main(String[] args) throws IOException {
        YagoParser yagoParser = new YagoParser(
                new File("/Users/admin/Downloads/yagoDateFacts.tsv"),
                new File("/Users/admin/Downloads/yagoFacts.tsv"),
                new File("/Users/admin/Downloads/yagoTransitiveType.tsv"),
                new File("/Users/admin/Downloads/yagoLabels.tsv"),
                new File("/Users/admin/Downloads/yagoWikipediaInfo.tsv"),
                new File("/Users/admin/Downloads/yagoGeonamesEntityIds.tsv"),
                new File("/Users/admin/Downloads/cities1000.txt"), "/Users/admin/Downloads");
        yagoParser.validateFiles();
        //yagoParser.parseFiles();
    }

    private boolean validateFiles() {
        System.out.println("Validating Files");
        return validateFile(yagoDateFile)
        && validateFile(yagoLocationFile)
        && validateFile(yagoCategoryFile)
        && validateFile(yagoLabelsFile)
        && validateFile(yagoWikiFile)
        && validateFile(yagoGeonamesFile)
        && validateFile(geonamesCitiesFile);
    }

    private boolean validateFile(File datafile) {
        if (datafile.exists() && !datafile.isDirectory() && datafile.canRead()) {
            return true;
        }
        return false;
    }

}
