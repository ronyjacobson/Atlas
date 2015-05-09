package il.ac.tau.cs.databases.atlas.db;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YagoParser {

    public static void parseDateFile(String inputFilename, Set<String> types) throws IOException {
        File inputFile = new File(inputFilename);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (types.contains(cols[2])) {
                File outfile = new File("date_" + cols[2] + ".tsv");
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                Pattern p = Pattern.compile("[0-9][0-9][0-9][0-9]-[#0-9][#0-9]-[#0-9][#0-9]");
                Matcher m = p.matcher(cols[3]);
                if (m.find()) {
                    pw.println(cols[0] + "\t" + cols[1] + "\t" + m.group());
                }
                pw.close();
            }
        }
        br.close();
    }

    public static void parseLocationFile(String inputFilename, Set<String> types) throws IOException {
        File inputFile = new File(inputFilename);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (types.contains(cols[2])) {
                File outfile = new File("location_" + cols[2] + ".tsv");
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(cols[0] + "\t" + cols[1] + "\t" + cols[3]);
                pw.close();
            }
        }
        br.close();
    }

    public static void parseCategoryFile(String inputFilename, Set<String> types) throws IOException {
        File inputFile = new File(inputFilename);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (types.contains(cols[3])) {
                File outfile = new File("category_" + cols[3] + ".tsv");
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(cols[0] + "\t" + cols[1]);
                pw.close();
            }
        }
        br.close();
    }

    public static void parseWikiFile(String inputFilename, Set<String> types) throws IOException {
        File inputFile = new File(inputFilename);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (types.contains(cols[1])) {
                File outfile = new File("wiki_info.tsv");
                FileWriter fw = new FileWriter(outfile, true);
                PrintWriter pw = new PrintWriter(fw, true);
                pw.println(cols[0] + "\t" + cols[2]);
                pw.close();
            }
        }
        br.close();
    }


    public static void parseGeonamesFile(String inputFilename) throws IOException {
        File inputFile = new File(inputFilename);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            File outfile = new File("geo_info.tsv");
            FileWriter fw = new FileWriter(outfile, true);
            PrintWriter pw = new PrintWriter(fw, true);
            Pattern p = Pattern.compile("http://sws.geonames.org/([0-9]+)");
            Matcher m = p.matcher(cols[2]);
            if (m.find()) {
                pw.println(cols[0] + "\t" + m.group(1));
            }
            pw.close();
        }
        br.close();
    }

    public static void main(String[] args) {

        Set<String> dateTypes = new HashSet<>();
        dateTypes.add("<wasBornOnDate>");
        dateTypes.add("<diedOnDate>");

        Set<String> locationTypes = new HashSet<>();
        locationTypes.add("<wasBornIn>");
        locationTypes.add("<diedIn>");
        locationTypes.add("<hasGender>");

        Set<String> categoryTypes = new HashSet<>();
        categoryTypes.add("<wordnet_scientist_110560637>");
        categoryTypes.add("<wordnet_philosopher_110423589>");
        categoryTypes.add("<wordnet_politician_110450303>");
        categoryTypes.add("<wordnet_composer_109947232>");
        categoryTypes.add("<wordnet_football_player_110101634>");
        categoryTypes.add("<wordnet_monarchist_110327824>");
        categoryTypes.add("<wordnet_poet_110444194>");
        categoryTypes.add("<wordnet_medalist_110305062>");
        categoryTypes.add("<wordnet_city_108524735>");

        Set<String> wikiTypes = new HashSet<>();
        wikiTypes.add("<hasWikipediaUrl>");

        try {
            System.out.println("Parser started");
            System.out.print("Parsing dates..");
            parseDateFile("/Users/admin/Downloads/yagoDateFacts.tsv", dateTypes);
            System.out.print("   Done\n");
            System.out.print("Parsing locations..");
            parseLocationFile("/Users/admin/Downloads/yagoFacts.tsv", locationTypes);
            System.out.print("   Done\n");
            System.out.print("Parsing categories..");
            parseCategoryFile("/Users/admin/Downloads/yagoTransitiveType.tsv", categoryTypes);
            System.out.print("   Done\n");
            System.out.print("Parsing wikipedia info..");
            parseWikiFile("/Users/admin/Downloads/yagoWikipediaInfo.tsv", wikiTypes);
            System.out.print("   Done\n");
            System.out.print("Parsing geonames info..");
            parseGeonamesFile("/Users/admin/Downloads/yagoGeonamesEntityIds.tsv");
            System.out.print("   Done\n");
            System.out.println("Parsing complete");
        } catch (IOException e) {
            System.out.println("Parsing failed");
            e.printStackTrace();
        }
    }

}
