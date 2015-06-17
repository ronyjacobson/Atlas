package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.parsing.YagoLocation;
import il.ac.tau.cs.databases.atlas.parsing.YagoPerson;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YagoParser {

    // TODO: consider changing to private members
    public static final String GEONAMES_URL_REGEX = "http://sws.geonames.org/([0-9]+)";
    public static final String DATE_REGEX = "[0-9][0-9][0-9][0-9]-[#0-9][#0-9]-[#0-9][#0-9]";
    public static final String CATEGORY_REGEX = "<wordnet_(.+)_[0-9]+>";
    public static final String LABEL_REGEX = "\"(.*)\"((@eng)?)";
    public static final String WIKI_REGEX = "<http://en\\.wikipedia\\.org/wiki/(.*)>";

    public static final Set<String> categoryTypes = new HashSet<>();

    private final File yagoDateFile;
    private final File yagoLocationFile;
    private final File yagoCategoryFile;
    private final File yagoLabelsFile;
    private final File yagoWikiFile;
    private final File yagoGeonamesFile;
    private final File geonamesCitiesFile;
    private final String outputPath;

    private Map<Long, YagoPerson> personsMap; // yagoId -> Person
    private Map<Long, YagoLocation> locationsMap; // geoId -> Location
    private Map<Long, Long> yagoToGeoMap;

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

        personsMap = new HashMap<>();
        locationsMap = new HashMap<>();
        yagoToGeoMap = new HashMap<>();

        categoryTypes.add("<wordnet_scientist_110560637>");
        categoryTypes.add("<wordnet_philosopher_110423589>");
        categoryTypes.add("<wordnet_politician_110450303>");
        categoryTypes.add("<wordnet_composer_109947232>");
        categoryTypes.add("<wordnet_football_player_110101634>");
        categoryTypes.add("<wordnet_monarchist_110327824>");
        categoryTypes.add("<wordnet_poet_110444194>");
        categoryTypes.add("<wordnet_medalist_110305062>");
    }

    public Map<Long, YagoPerson> getPersonsMap() {
        return personsMap;
    }

    public Map<Long, YagoLocation> getLocationsMap() {
        return locationsMap;
    }

    // handles yagoDateFacts
    public void parseYagoDateFile(File yagoDateFile, char from, char to) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoDateFile));
        Pattern pattern = Pattern.compile(DATE_REGEX);

        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 5) {
                continue;
            }
            /*
            char c = Character.toLowerCase(cols[1].charAt(1));
            if (!(c >= from && c <= to)) {
                continue;
            }*/

            long yagoId = yagoIdToHash(cols[1]);
            String factType = cols[2];
            String factValue = cols[3];
            java.sql.Date foundDate;
            final Matcher matcher = pattern.matcher(factValue);
            if (matcher.find()) {
                try {
                    foundDate = java.sql.Date.valueOf(matcher.group().replace("##", "01").replace("00", "01"));
                } catch (java.lang.IllegalArgumentException e) {
                    continue;
                }
                if ("<wasBornOnDate>".equals(factType)) {
                    ensureYagoIdToYagoPerson(yagoId).setBornOnDate(foundDate);
                } else if ("<diedOnDate>".equals(factType)) {
                    ensureYagoIdToYagoPerson(yagoId).setDiedOnDate(foundDate);
                }
            }
        }
        br.close();
    }

    // handles yagoFacts
    public void parseYagoLocationFile(File yagoLocationFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLocationFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }

            long yagoId = yagoIdToHash(cols[1]);
            String factType = cols[2];
            String factValue = cols[3];
            YagoPerson yagoPerson = personsMap.get(yagoId);
            if ("<hasGender>".equals(factType) && yagoPerson != null) {
                if ("<female>".equals(factValue)) {
                    yagoPerson.setIsFemale(true);
                }
            } else if ("<wasBornIn>".equals(factType) && yagoPerson != null) {
                yagoPerson.setBornInLocation(yagoIdToHash(factValue));
            } else if ("<diedIn>".equals(factType) && yagoPerson != null) {
                yagoPerson.setDiedInLocation(yagoIdToHash(factValue));
            }
        }
        br.close();
    }

    // TODO: not sure about this.. removing while iterating
    private void validatePersonsMap() {
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPerson()) {
                it.remove();
            }
        }
    }

    private void ensureLabels() {
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPersonLabels()) {
                it.remove();
            }
        }
    }

    private void validateLocationsMap() {
        for (Iterator<Entry<Long, YagoLocation>> it = locationsMap.entrySet().iterator(); it.hasNext(); ) {
            Entry<Long, YagoLocation> entry = it.next();
            if (! entry.getValue().isValidLocation()) {
                it.remove();
            }
        }
    }

    // TODO: find a better alternative?
    private long yagoIdToHash(String yagoId) {
        String cleanYagoId = yagoId.substring(1,yagoId.length()-1);
        long hash=7;
        for (int i=0; i < cleanYagoId.length(); i++) {
            hash = hash * 31 + cleanYagoId.charAt(i);
        }
        return hash;
    }

    private YagoPerson ensureYagoIdToYagoPerson(long yagoId) {
        YagoPerson yagoPerson = personsMap.get(yagoId);
        if (yagoPerson == null) {
            yagoPerson = new YagoPerson(yagoId);
            personsMap.put(yagoId, yagoPerson);
        }
        return yagoPerson;
    }

    private YagoLocation ensureGeoIdToYagoLocation(long geoId) {
        YagoLocation yagoLocation = locationsMap.get(geoId);
        if (yagoLocation == null) {
            yagoLocation = new YagoLocation();
            locationsMap.put(geoId, yagoLocation);
        }
        return yagoLocation;
    }

    // handles yagoTransitiveType
    public void parseYagoCategoryFile(File yagoCategoryFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoCategoryFile));
        Pattern p = Pattern.compile(CATEGORY_REGEX);

        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }

            String foundCategory = cols[3];
            long yagoId = yagoIdToHash(cols[1]);
            if (categoryTypes.contains(foundCategory)) {
                Matcher m = p.matcher(foundCategory);
                YagoPerson yagoPerson = personsMap.get(yagoId);
                if (yagoPerson != null && m.find()) {
                    yagoPerson.addCategory(m.group(1).intern());
                }
            }
        }
        br.close();
    }

    // handles yagoGeonamesEntityIds
    public void parseYagoGeonamesFile(File yagoGeonamesFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoGeonamesFile));
        Pattern p = Pattern.compile(GEONAMES_URL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 3) {
                continue;
            }
            long locationId = yagoIdToHash(cols[0]);
            Matcher m = p.matcher(cols[2]);
            if (m.find()) {
                try {
                    long geoId = Long.parseLong(m.group(1));
                    ensureGeoIdToYagoLocation(geoId).setLocationId(locationId);
                    yagoToGeoMap.put(locationId, geoId);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        br.close();
    }

    // handles cities1000
    public void parseGeonamesCitiesFile(File geoCitiesFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(geoCitiesFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            try {
                long geoId = Long.parseLong(cols[0]);
                YagoLocation yagoLocation = locationsMap.get(geoId);
                if (yagoLocation != null) {
                    String name = cols[2];
                    double latitude = Double.parseDouble(cols[4]);
                    double longitude = Double.parseDouble(cols[5]);
                    yagoLocation.setName(name);
                    yagoLocation.setLatitude(latitude);
                    yagoLocation.setLongitude(longitude);
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        br.close();
    }

    // handles yagoWikipediaInfo
    public void parseYagoWikiFile(File yagoWiKiFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoWiKiFile));
        Pattern p = Pattern.compile(WIKI_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 3) {
                continue;
            }
            long locationId = yagoIdToHash(cols[0]);
            Matcher wikiMatcher = p.matcher(cols[2]);
            if ("<hasWikipediaUrl>".equals(cols[1]) && wikiMatcher.find()) {
                if (personsMap.containsKey(locationId)) {
                    personsMap.get(locationId).setWikiUrl(wikiMatcher.group(1));
                }
                Long geoId = yagoToGeoMap.get(locationId);
                if (geoId != null && locationsMap.containsKey(geoId)) {
                    locationsMap.get(geoId).setWikiUrl(wikiMatcher.group(1));
                }
            }
        }
        br.close();
    }

    // handles yagoLabels
    public void parseYagoLabelsFile(File yagoLabelsFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLabelsFile));
        Pattern p = Pattern.compile(LABEL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }

            long yagoId = yagoIdToHash(cols[1]);
            Matcher m = p.matcher(cols[3]);
            if (m.matches()) {
                YagoPerson yagoPerson = personsMap.get(yagoId);
                if ("skos:prefLabel".equals(cols[2]) && yagoPerson != null) {
                    yagoPerson.setPrefLabel(m.group(1));
                } else if ("rdfs:label".equals(cols[2]) && yagoPerson != null) {
                    yagoPerson.addLabel(m.group(1));
                }
            }
        }
        br.close();
    }

    public void parseFiles(char from, char to) throws IOException {
        System.out.println("Parser started");
        /*if (!validateFiles()) {
            System.out.println("Terminating parser");
//            throw new IOException("Bad input file");
        }*/
        System.out.print("Parsing YAGO dates..");

        parseYagoDateFile(yagoDateFile, from, to);
        System.out.println("   Done");
        System.out.println(personsMap.size());
        System.out.print("Parsing YAGO locations..");
        parseYagoLocationFile(yagoLocationFile);
        System.out.println("   Done");
        validatePersonsMap();
        System.out.println(personsMap.size());
        System.out.print("Parsing YAGO categories..");
        parseYagoCategoryFile(yagoCategoryFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO labels..");
        parseYagoLabelsFile(yagoLabelsFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO geonames info..");
        parseYagoGeonamesFile(yagoGeonamesFile);
        System.out.println("   Done");
        System.out.print("Parsing Geonames cities info..");
        parseGeonamesCitiesFile(geonamesCitiesFile);
        System.out.println("   Done");
        System.out.print("Parsing YAGO wikipedia info..");
        parseYagoWikiFile(yagoWikiFile);
        System.out.println("   Done");
        validateLocationsMap();
        ensureLabels(); // remove persons without prefLabel or no labels at all
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

        //yagoParser.parseFiles('a', 'e');
        yagoParser.parseFiles('f', 'k');
        //yagoParser.parseFiles('l', 'p');
        //yagoParser.parseFiles('q', 'z');
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
