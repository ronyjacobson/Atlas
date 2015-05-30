INSERT INTO final(id, label, category)
SELECT 
    temp_location.id, label, category
FROM
    temp_location,
    temp_category
WHERE
    temp_location.id = temp_category.id
    AND temp_category.category IS NOT NULL;
