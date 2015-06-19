package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.ParserConstants;
import il.ac.tau.cs.databases.atlas.ProgressUpdater;
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
    private ProgressUpdater progressUpdater;

    public YagoParser() {
        personsMap = new HashMap<>();
        locationsMap = new HashMap<>();
        geoIdToLocationIdMap = new HashMap<>();
    }

    public Map<Long, YagoPerson> getPersonsMap() {
        return personsMap;
    }

    public Map<Long, YagoLocation> getLocationsMap() {
        return locationsMap;
    }

    public void setProgressUpdater(ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }

    public void validatePersonsMap() {
        progressUpdater.resetProgress();
        long numberOfRecords = personsMap.size();
        long recordNumber = 0;
        long interval = numberOfRecords / 100;
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            if (++recordNumber % interval == 0) {
                updateProgress(numberOfRecords, recordNumber);
            }
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPerson()) {
                it.remove();
            }
        }
    }

    public void ensureLabels() {
        progressUpdater.resetProgress();
        long numberOfRecords = personsMap.size();
        long recordNumber = 0;
        long interval = numberOfRecords / 100;
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            if (++recordNumber % interval == 0) {
                updateProgress(numberOfRecords, recordNumber);
            }
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPersonLabels()) {
                it.remove();
            }
        }
    }

    private void updateProgress(long numberOfRecords, long recordNumber) {
        progressUpdater.updateProgress((int) (recordNumber * 100 / numberOfRecords), recordNumber + "/" + numberOfRecords + " processed");
    }

    public void validateLocationsMap() {
        progressUpdater.resetProgress();
        long numberOfRecords = locationsMap.size();
        long recordNumber = 0;
        long interval = numberOfRecords / 100;
        for (Iterator<Entry<Long, YagoLocation>> it = locationsMap.entrySet().iterator(); it.hasNext(); ) {
            if (++recordNumber % interval == 0) {
                updateProgress(numberOfRecords, recordNumber);
            }
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

    private long numberOfLines(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        long numberOfLines = 0;
        while (br.readLine() != null) {
            numberOfLines++;
        }
        return numberOfLines;
    }

    // handles yagoDateFacts
    public void parseYagoDateFile(File yagoDateFile) throws IOException {
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoDateFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoDateFile));
        Pattern pattern = Pattern.compile(ParserConstants.DATE_REGEX);

        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoLocationFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoLocationFile));
        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoCategoryFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoCategoryFile));

        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
            String[] cols = line.trim().split("\\t");
            if (cols.length < 4) {
                continue;
            }

            long yagoId = yagoIdToHash(cols[1]);
            String foundCategory = cols[3];
            Integer categoryId = ParserConstants.CATEGORY_TYPES.get(foundCategory);
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoGeonamesFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoGeonamesFile));
        Pattern p = Pattern.compile(ParserConstants.GEONAMES_URL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(geoCitiesFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(geoCitiesFile));
        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoWiKiFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoWiKiFile));
        Pattern p = Pattern.compile(ParserConstants.WIKI_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoLiteralFactsFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoLiteralFactsFile));
        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
        progressUpdater.updateProgress(0, "Reading file..");
        long numberOfLines = numberOfLines(yagoLabelsFile);
        long lineNumber = 0;
        long interval = numberOfLines / 100;

        BufferedReader br = new BufferedReader(new FileReader(yagoLabelsFile));
        Pattern p = Pattern.compile(ParserConstants.LABEL_REGEX);
        String line;
        while ((line = br.readLine()) != null) {
            if (++lineNumber % interval == 0) {
                updateProgress(numberOfLines, lineNumber);
            }
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
