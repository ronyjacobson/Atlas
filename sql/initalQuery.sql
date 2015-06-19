SELECT DISTINCT
    person.person_ID,
    person.addedByUser,
    person.prefLabel,
    person.wasBornOnDate,
    person.diedOnDate,
    person.wasBornInLocation,
    person.diedInLocation,
    person.wikiURL AS PersonURL,
    person.isFemale,
    category.categoryName,
    b_location.geo_name AS b_geoname,
    b_location.wikiURL AS b_LocURL,
    b_location.longitude AS b_longitude,
    b_location.latitude AS b_latitude,
    location.geo_name AS d_geoname,
    location.wikiURL AS d_LocURL,
    location.longitude AS d_longitude,
    location.latitude AS d_latitude,
    MAX(year(person.wasBornOnDate)) AS maxBirthYear,
    MAX(year(person.diedOnDate)) AS maxDeathYear,
    MIN(year(person.wasBornOnDate)) AS minBirthYear,
    MIN(year(person.diedOnDate)) AS minDeathYear
    
FROM
    location b_location,
    category
        JOIN
    person_has_category ON category.category_ID = person_has_category.category_ID,
    person
        LEFT OUTER JOIN
    location ON person.diedInLocation = location.location_ID
WHERE
    person.person_ID = person_has_category.person_ID
	AND person.wasBornInLocation = b_location.location_ID
	AND category.categoryName = 'monarchist' #'category'
	AND ((YEAR(person.wasBornOnDate) >= '1760' AND YEAR(person.wasBornOnDate) <= '1780') OR (YEAR(person.diedOnDate) >= '1760' AND YEAR(person.diedOnDate) <= '1780')) #'end'
        
        
        
ORDER BY RAND()
LIMIT 100
        





        
	


