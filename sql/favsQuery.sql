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
    location.latitude AS d_latitude

FROM
    location b_location,
    category
        JOIN
    person_has_category ON category.category_ID = person_has_category.category_ID,
    person
        LEFT OUTER JOIN
    location ON person.diedInLocation = location.location_ID
    # , FROM EXTRA
    ,user_favorties

WHERE
    person.person_ID = person_has_category.person_ID
    AND person.wasBornInLocation = b_location.location_ID
    # AND CONDITIONS 
    AND user_favorites.user_ID = '1'
    
ORDER BY RAND() LIMIT 100
        





        
	


