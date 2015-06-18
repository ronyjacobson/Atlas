package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.parsing.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YagoParser {
    private Map<Long, YagoPerson> personsMap; // yagoId -> Person
    private Map<Long, YagoLocation> locationsMap; // locationId -> Location
    private Map<Long, Long> geoIdToLocationIdMap; // geoId -> locationId

    private Map<String, Integer> categoryTypes;

    // TODO: consider changing to private members or globals in another class (ParserConstants?)
    public static final String GEONAMES_URL_REGEX = "http://sws.geonames.org/([0-9]+)";
    public static final String DATE_REGEX = "[0-9][0-9][0-9][0-9]-[#0-9][#0-9]-[#0-9][#0-9]";
    public static final String CATEGORY_REGEX = "<wordnet_(.+)_[0-9]+>";
    public static final String LABEL_REGEX = "\"(.*)\"((@eng)?)";
    public static final String WIKI_REGEX = "<http://en\\.wikipedia\\.org/wiki/(.*)>";

    public YagoParser() {

        personsMap = new HashMap<>();
        locationsMap = new HashMap<>();
        geoIdToLocationIdMap = new HashMap<>();
        categoryTypes = new HashMap<>();

        categoryTypes.put("<wordnet_scientist_110560637>", 1);
        categoryTypes.put("<wordnet_philosopher_110423589>", 2);
        categoryTypes.put("<wordnet_politician_110450303>", 3);
        categoryTypes.put("<wordnet_composer_109947232>", 4);
        categoryTypes.put("<wordnet_football_player_110101634>", 5);
        categoryTypes.put("<wordnet_monarchist_110327824>", 6);
        categoryTypes.put("<wordnet_poet_110444194>", 7);
        categoryTypes.put("<wordnet_medalist_110305062>", 8);
    }

    public Map<Long, YagoPerson> getPersonsMap() {
        return personsMap;
    }

    public Map<Long, YagoLocation> getLocationsMap() {
        return locationsMap;
    }

    public Map<String, Integer> getCategoryTypes() {
        return categoryTypes;
    }

    public void validatePersonsMap() {
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPerson()) {
                it.remove();
            }
        }
    }

    public void ensureLabels() {
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPersonLabels()) {
                it.remove();
            }
        }
    }

    public void validateLocationsMap() {
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

    private YagoLocation ensureLocationIdToYagoLocation(long locationId) {
        YagoLocation yagoLocation = locationsMap.get(locationId);
        if (yagoLocation == null) {
            yagoLocation = new YagoLocation(locationId);
            locationsMap.put(locationId, yagoLocation);
        }
        return yagoLocation;
    }

    private YagoLocation ensureGeoIdToYagoLocation(long geoId) {
        YagoLocation yagoLocation = locationsMap.get(geoId);
        if (yagoLocation == null) {
            yagoLocation = new YagoLocation();
            locationsMap.put(geoId, yagoLocation);
        }
        return yagoLocation;
    }

    // handles yagoDateFacts
    public void parseYagoDateFile(File yagoDateFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoDateFile));
        Pattern pattern = Pattern.compile(DATE_REGEX);

        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 5) {
                continue;
            }

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
                setLocationIdFromYagoId(factValue, yagoPerson, true);
            } else if ("<diedIn>".equals(factType) && yagoPerson != null) {
                setLocationIdFromYagoId(factValue, yagoPerson, false);
            }
        }
        br.close();
    }

    private void setLocationIdFromYagoId(String yagoLocationId, YagoPerson yagoPerson, boolean born) {
        long locationId = yagoIdToHash(yagoLocationId);
        YagoLocation yagoLocation = locationsMap.get(locationId);
        if (yagoLocation != null && born) {
            yagoPerson.setBornInLocation(locationId);
        } else if (yagoLocation != null) {
            yagoPerson.setDiedInLocation(locationId);
        }
    }

    // handles yagoTransitiveType
    public void parseYagoCategoryFile(File yagoCategoryFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoCategoryFile));

        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }

            long yagoId = yagoIdToHash(cols[1]);
            String foundCategory = cols[3];
            Integer categoryId = categoryTypes.get(foundCategory);
            if (categoryId != null) {
                YagoPerson yagoPerson = personsMap.get(yagoId);
                if (yagoPerson != null) {
                    yagoPerson.addCategory(categoryId);
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
                    geoIdToLocationIdMap.put(geoId, locationId);
                    //ensureLocationIdToYagoLocation(locationId);
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

                // translation from geo id to hashed yago id
                Long locationId = geoIdToLocationIdMap.get(geoId);
                if (locationId == null) {
                    continue;
                }
                if (! locationsMap.containsKey(locationId)) {
                    String name = cols[2];
                    double latitude = Double.parseDouble(cols[4]);
                    double longitude = Double.parseDouble(cols[5]);

                    // insert new location
                    YagoLocation yagoLocation = new YagoLocation(locationId);
                    yagoLocation.setName(name);
                    yagoLocation.setLatitude(latitude);
                    yagoLocation.setLongitude(longitude);
                    locationsMap.put(locationId, yagoLocation);
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
                YagoPerson yagoPerson = personsMap.get(locationId);
                if (yagoPerson != null) {
                    yagoPerson.setWikiUrl(wikiMatcher.group(1));
                }

                YagoLocation yagoLocation = locationsMap.get(locationId);
                if (yagoLocation != null) {
                    yagoLocation.setWikiUrl(wikiMatcher.group(1));
                }
            }
        }
        br.close();
    }

    public void parseYagoLiteralFacts(File yagoLiteralFactsFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(yagoLiteralFactsFile));
        String line;
        while ((line = br.readLine()) != null) {
            String[] cols = line.trim().split("\\t");
            if (cols.length < 5) {
                continue;
            }

            long locationId = yagoIdToHash(cols[1]);
            if ("<hasLatitude>".equals(cols[2])) {
                if ("<Berlin>".equals(cols[1])) {
                    System.out.println("233");
                }
                double latitude = Double.parseDouble(cols[4]);
                ensureLocationIdToYagoLocation(locationId).setLatitude(latitude);
            } else if ("<hasLongitude>".equals(cols[2])) {
                double longitue = Double.parseDouble(cols[4]);
                ensureLocationIdToYagoLocation(locationId).setLongitude(longitue);
            }
        }
        br.close();
    }

    // handles yagoLabels
    public void parseYagoLabelsFile(File yagoLabelsFile, boolean searchForLocation) throws IOException {
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
                String label = m.group(1);
                if (label.length() > 199) {
                    label = label.substring(0, 199);
                }
                if ("skos:prefLabel".equals(cols[2]) && searchForLocation) {
                    YagoLocation yagoLocation = locationsMap.get(yagoId);
                    if (yagoLocation != null) {
                        yagoLocation.setName(label);
                    }
                } else if ("skos:prefLabel".equals(cols[2]) && ! searchForLocation) {
                    YagoPerson yagoPerson = personsMap.get(yagoId);
                    if (yagoPerson != null) {
                        yagoPerson.setPrefLabel(label);
                    }
                } else if ("rdfs:label".equals(cols[2]) && ! searchForLocation) {
                    YagoPerson yagoPerson = personsMap.get(yagoId);
                    if (yagoPerson != null) {
                        yagoPerson.addLabel(label);
                    }
                }
            }
        }
        br.close();
    }
}
