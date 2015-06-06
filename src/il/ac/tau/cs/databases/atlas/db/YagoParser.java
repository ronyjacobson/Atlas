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
    public static final String CATEGORIES_INFO_OUT_NAME = "categories_info.tsv";
    public static final String DATE_BORN_ON_DATE_OUT_NAME = "date_bornOnDate.tsv";
    public static final String DATE_DIED_ON_DATE_OUT_NAME = "date_diedOnDate.tsv";
    public static final String FACTS_GENDER_OUT_NAME = "facts_gender.tsv";
    public static final String FACTS_BORN_IN_LOCATION_OUT_NAME = "facts_born_in_location.tsv";
    public static final String FACTS_DIED_IN_LOCATION_OUT_NAME = "facts_died_in_location.tsv";
    public static final String LABELS_INFO_OUT_NAME = "labels_info.tsv";
    public static final String PREF_LABELS_INFO_OUT_NAME = "pref_labels_info.tsv";

    public static final String GEONAMES_URL_REGEX = "http://sws.geonames.org/([0-9]+)";
    public static final String DATE_REGEX = "[0-9][0-9][0-9][0-9]-[#0-9][#0-9]-[#0-9][#0-9]";
    public static final String CATEGORY_REGEX = "<wordnet_(.+)_[0-9]+>";
    public static final String LABEL_REGEX = "\"(.*)\"((@eng)?)";

    private final File yagoDateFile;
    private final File yagoLocationFile;
    private final File yagoCategoryFile;
    private final File yagoLabelsFile;
    private final File yagoWikiFile;
    private final File yagoGeonamesFile;
    private final File geonamesCitiesFile;
    private final String outputPath;

    private Set<String> categoryTypes;

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

        categoryTypes = new HashSet<>();
        categoryTypes.add("<wordnet_scientist_110560637>");
        categoryTypes.add("<wordnet_philosopher_110423589>");
        categoryTypes.add("<wordnet_politician_110450303>");
        categoryTypes.add("<wordnet_composer_109947232>");
        categoryTypes.add("<wordnet_football_player_110101634>");
        categoryTypes.add("<wordnet_monarchist_110327824>");
        categoryTypes.add("<wordnet_poet_110444194>");
        categoryTypes.add("<wordnet_medalist_110305062>");
    }

    // handles yagoDateFacts
    public void parseYagoDateFile(File yagoDateFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoDateFile));
        Pattern pattern = Pattern.compile(DATE_REGEX);

        File bornOnDateOutfile = new File(concatToOutPath(DATE_BORN_ON_DATE_OUT_NAME));
        PrintWriter bornOnPw = new PrintWriter(new FileWriter(bornOnDateOutfile, true), true);

        File diedOnDatefile = new File(concatToOutPath(DATE_DIED_ON_DATE_OUT_NAME));
        PrintWriter diedOnPw = new PrintWriter(new FileWriter(diedOnDatefile, true), true);

        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 5) {
                continue;
            }
            final Matcher matcher = pattern.matcher(cols[3]);
            if ("<wasBornOnDate>".equals(cols[2]) && matcher.find()) {
                bornOnPw.println(cols[1] + "\t" + matcher.group().replace('#', '0'));
            } else if ("<diedOnDate>".equals(cols[2]) && matcher.find()) {
                diedOnPw.println(cols[1] + "\t" + matcher.group().replace('#', '0'));
            }
        }
        bornOnPw.close();
        diedOnPw.close();
        br.close();
    }

    // handles yagoFacts
    public void parseYagoLocationFile(File yagoLocationFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLocationFile));
        File genderOutfile = new File(concatToOutPath(FACTS_GENDER_OUT_NAME));
        PrintWriter genderPw = new PrintWriter(new FileWriter(genderOutfile, true), true);

        File bornInOutfile = new File(concatToOutPath(FACTS_BORN_IN_LOCATION_OUT_NAME));
        PrintWriter bornInPw = new PrintWriter(new FileWriter(bornInOutfile, true), true);

        File diedInOutfile = new File(concatToOutPath(FACTS_DIED_IN_LOCATION_OUT_NAME));
        PrintWriter diedInPw = new PrintWriter(new FileWriter(diedInOutfile, true), true);

        String line;
        String currentValue;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }
            if ("<hasGender>".equals(cols[2])) {
                if ("<female>".equals(cols[3])) {
                    currentValue = "true";
                } else {
                    currentValue = "false";
                }
                genderPw.println(cols[1] + "\t" + currentValue);
            } else if ("<wasBornIn>".equals(cols[2])) {
                bornInPw.println(cols[1] + "\t" + cols[3]);
            } else if ("<diedIn>".equals(cols[2])) {
                diedInPw.println(cols[1] + "\t" + cols[3]);
            }
        }
        genderPw.close();
        bornInPw.close();
        diedInPw.close();
        br.close();
    }

    // handles yagoTransitiveType
    public void parseYagoCategoryFile(File yagoCategoryFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoCategoryFile));
        File outfile = new File(concatToOutPath(CATEGORIES_INFO_OUT_NAME));
        FileWriter fw = new FileWriter(outfile, true);
        PrintWriter pw = new PrintWriter(fw, true);
        Pattern p = Pattern.compile(CATEGORY_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }
            if (categoryTypes.contains(cols[3])) {
                Matcher m = p.matcher(cols[3]);
                if (m.find()) {
                    pw.println(cols[1] + "\t" + m.group(1));
                }

            }
        }
        pw.close();
        br.close();
    }

    // handles yagoWikipediaInfo
    public void parseYagoWikiFile(File yagoWiKiFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoWiKiFile));
        File outfile = new File(concatToOutPath(WIKI_INFO_OUT_NAME));
        FileWriter fw = new FileWriter(outfile, true);
        PrintWriter pw = new PrintWriter(fw, true);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 3) {
                continue;
            }
            if ("<hasWikipediaUrl>".equals(cols[1])) {
                pw.println(cols[0] + "\t" + cols[2].substring(1,cols[2].length()-1));
            }
        }
        pw.close();
        br.close();
    }


    // handles yagoGeonamesEntityIds
    public void parseYagoGeonamesFile(File yagoGeonamesFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoGeonamesFile));
        File outfile = new File(concatToOutPath(GEO_INFO_OUT_NAME));
        FileWriter fw = new FileWriter(outfile, true);
        PrintWriter pw = new PrintWriter(fw, true);
        Pattern p = Pattern.compile(GEONAMES_URL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 3) {
                continue;
            }
            Matcher m = p.matcher(cols[2]);
            if (m.find()) {
                pw.println(cols[0] + "\t" + m.group(1));
            }
        }
        pw.close();
        br.close();
    }

    // handles cities1000
    public void parseGeonamesCitiesFile(File geoCitiesFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(geoCitiesFile));
        File outfile = new File(concatToOutPath(GEO_CITIES_INFO_OUT_NAME));
        FileWriter fw = new FileWriter(outfile, true);
        PrintWriter pw = new PrintWriter(fw, true);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            int[] columns = {0, 2, 4};
            for (int i : columns) {
                pw.print(cols[i] + "\t");
            }
            pw.println(cols[5]);
        }
        pw.close();
        br.close();
    }

    // handles yagoLabels
    public void parseYagoLabelsFile(File yagoLabelsFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLabelsFile));
        File labelOutfile = new File(concatToOutPath(LABELS_INFO_OUT_NAME));
        File prefLabelOutfile = new File(concatToOutPath(PREF_LABELS_INFO_OUT_NAME));
        PrintWriter labelPw = new PrintWriter(new FileWriter(labelOutfile, true), true);
        PrintWriter prefLabelPw = new PrintWriter(new FileWriter(prefLabelOutfile, true), true);

        Pattern p = Pattern.compile(LABEL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }
            Matcher m = p.matcher(cols[3]);
            if (m.matches()) {
                if ("skos:prefLabel".equals(cols[2])) {
                    prefLabelPw.println(cols[1] + "\t" + m.group(1));
                } else if ("rdfs:label".equals(cols[2])) {
                    labelPw.println(cols[1] + "\t" + m.group(1));
                }
            }
        }
        prefLabelPw.close();
        labelPw.close();
        br.close();
    }

    public void parseFiles() throws IOException {
        System.out.println("Parser started");
        /*if (!validateFiles()) {
            System.out.println("Terminating parser");
//            throw new IOException("Bad input file");
        }*/
        System.out.print("Parsing YAGO dates..");
        //parseYagoDateFile(yagoDateFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO locations..");
        //parseYagoLocationFile(yagoLocationFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO categories..");
        //parseYagoCategoryFile(yagoCategoryFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO labels..");
        parseYagoLabelsFile(yagoLabelsFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO wikipedia info..");
        //parseYagoWikiFile(yagoWikiFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO geonames info..");
        //parseYagoGeonamesFile(yagoGeonamesFile);
        System.out.println("   Done");
        System.out.print("Parsing Geonames cities info..");
        //parseGeonamesCitiesFile(geonamesCitiesFile);
        System.out.println("   Done");
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
                new File("/Users/admin/Downloads/cities1000.txt"), "/Users/admin/Downloads/Test");
        yagoParser.parseFiles();
    }

    private boolean validateFiles() {
        System.out.println("Datafiles Validation Started");
        return validateFile(yagoDateFile)
                && validateFile(yagoLocationFile)
                && validateFile(yagoCategoryFile)
                && validateFile(yagoLabelsFile)
                && validateFile(yagoWikiFile)
                && validateFile(yagoGeonamesFile)
                && validateFile(geonamesCitiesFile);
    }

    private boolean validateFile(File datafile) {
        System.out.print("Validating file: " + datafile.getName() + "..");
        if (datafile.exists() && !datafile.isDirectory() && datafile.canRead()) {
            System.out.println("   Done");
            return true;
        }
        System.out.println("   Error");
        return false;
    }

}
