package il.ac.tau.cs.databases.atlas.parsing;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.progress.ProgressUpdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * handles input files, extracts meaningful information and constructs objects to represent db records
 */
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

    /**
     * a. person is missing either a birth date/location? remove it
     * b. person has only one of death date/location? set both to null
     * @throws AtlasServerException
     */
    public void validatePersonsMap() throws AtlasServerException {
        progressUpdater.resetProgress();
        long numberOfRecords = personsMap.size();
        long recordNumber = 0;
        long interval = numberOfRecords / 100;
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            if (++recordNumber % interval == 0) {
                updateProgress(numberOfRecords, recordNumber);
            }
            Entry<Long, YagoPerson> entry = it.next();
            YagoPerson yagoPerson = entry.getValue();
            if (! yagoPerson.isValidPerson()) {
                it.remove();
            } else if (yagoPerson.getDiedInLocation() == null || yagoPerson.getDiedOnDate() == null ) {
                // no person with only one of the two
                yagoPerson.setDiedInLocation(null);
                yagoPerson.setDiedOnDate(null);
            }
        }
    }

    /**
     * person has no labels? remove it
     * person was born/died in a location not in our db? remove it
     * person has no categories? remove it
     * @throws AtlasServerException
     */
    public void ensureCategoriesLabelsAndLocations() throws AtlasServerException {
        progressUpdater.resetProgress();
        long numberOfRecords = personsMap.size();
        long recordNumber = 0;
        long interval = numberOfRecords / 100;
        for (Iterator<Entry<Long, YagoPerson>> it = personsMap.entrySet().iterator(); it.hasNext(); ) {
            boolean remove = false;
            if (++recordNumber % interval == 0) {
                updateProgress(numberOfRecords, recordNumber);
            }
            Entry<Long, YagoPerson> entry = it.next();
            if (! entry.getValue().isValidPersonLabels()) {
                remove = true;
            }
            Long bornInLocation = entry.getValue().getBornInLocation();
            if (bornInLocation != null && !locationsMap.containsKey(bornInLocation)) {
                remove = true;
            }
            Long diedInLocation = entry.getValue().getDiedInLocation();
            if (diedInLocation != null && !locationsMap.containsKey(diedInLocation)) {
                remove = true;
            }
            if (entry.getValue().getCategories().size() == 0) {
                remove = true;
            }
            if (remove) {
                it.remove();
            }
        }
    }

    /**
     * location has some missing properties? remove it
     * @throws AtlasServerException
     */
    public void validateLocationsMap() throws AtlasServerException {
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

    /**
     * location is usused? remove it
     * @throws AtlasServerException
     */
    public void removeUnusedLocation() throws AtlasServerException {
        progressUpdater.resetProgress();
        long numberOfRecords = locationsMap.size();
        long recordNumber = 0;
        long interval = numberOfRecords / 100;
        for (Iterator<Entry<Long, YagoLocation>> it = locationsMap.entrySet().iterator(); it.hasNext(); ) {
            if (++recordNumber % interval == 0) {
                updateProgress(numberOfRecords, recordNumber);
            }
            Entry<Long, YagoLocation> entry = it.next();
            if (! entry.getValue().isUsed()) {
                it.remove();
            }
        }
    }

    private void updateProgress(long numberOfRecords, long recordNumber) throws AtlasServerException {
        progressUpdater.updateProgress((int) (recordNumber * 100 / numberOfRecords), recordNumber + "/" + numberOfRecords + " processed");
    }

    private long yagoIdToHash(String yagoId) {
        String cleanYagoId = yagoId.substring(1,yagoId.length()-1);
        long hash=7;
        for (int i=0; i < cleanYagoId.length(); i++) {
            hash = hash * 31 + cleanYagoId.charAt(i);
        }
        return hash;
    }

    /**
     * fetch YagoPerson object with yagoId, if no such person exists, create it
     */
    private YagoPerson ensureYagoIdToYagoPerson(long yagoId) {
        YagoPerson yagoPerson = personsMap.get(yagoId);
        if (yagoPerson == null) {
            yagoPerson = new YagoPerson(yagoId);
            personsMap.put(yagoId, yagoPerson);
        }
        return yagoPerson;
    }

    /**
     * fetch YagoLocation object with locationId, if no such location exists, create it
     */
    private YagoLocation ensureLocationIdToYagoLocation(long locationId) {
        YagoLocation yagoLocation = locationsMap.get(locationId);
        if (yagoLocation == null) {
            yagoLocation = new YagoLocation(locationId);
            locationsMap.put(locationId, yagoLocation);
        }
        return yagoLocation;
    }

    private void setLocationIdFromYagoId(String yagoLocationId, YagoPerson yagoPerson, boolean born) {
        long locationId = yagoIdToHash(yagoLocationId);
        YagoLocation yagoLocation = locationsMap.get(locationId);
        if (yagoLocation != null) {
            yagoLocation.setUsed(true);
            if (born) {
                yagoPerson.setBornInLocation(locationId);
            } else {
                yagoPerson.setDiedInLocation(locationId);
            }
        }
    }

    private long numberOfLines(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        long numberOfLines = 0;
        while (br.readLine() != null) {
            numberOfLines++;
        }
        br.close();
        return numberOfLines;
    }

    /**
     * handles yagoDateFacts file
     */
    public void parseYagoDateFile(File yagoDateFile) throws IOException, AtlasServerException {
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

    /**
     * handles yagoFacts file
     */
    public void parseYagoLocationFile(File yagoLocationFile) throws IOException, AtlasServerException {
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

    /**
     * handles yagoTransitiveType file
     */
    public void parseYagoCategoryFile(File yagoCategoryFile) throws IOException, AtlasServerException {
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

    /**
     * handles yagoGeonamesEntityIds file
     */
    public void parseYagoGeonamesFile(File yagoGeonamesFile) throws IOException, AtlasServerException {
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
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        br.close();
    }

    /**
     * handles cities1000 file
     */
    public void parseGeonamesCitiesFile(File geoCitiesFile) throws IOException, AtlasServerException {
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

    /**
     * handles yagoWikipediaInfo file
     */
    public void parseYagoWikiFile(File yagoWiKiFile) throws IOException, AtlasServerException {
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

    /**
     * handles yagoLiteralFacts file
     */
    public void parseYagoLiteralFacts(File yagoLiteralFactsFile) throws IOException, AtlasServerException {
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
                double latitude = Double.parseDouble(cols[4]);
                ensureLocationIdToYagoLocation(locationId).setLatitude(latitude);
            } else if ("<hasLongitude>".equals(cols[2])) {
                double longitue = Double.parseDouble(cols[4]);
                ensureLocationIdToYagoLocation(locationId).setLongitude(longitue);
            }
        }
        br.close();
    }

    /**
     * handles yagoLabels file
     */
    public void parseYagoLabelsFile(File yagoLabelsFile, boolean searchForLocation) throws IOException, AtlasServerException {
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
