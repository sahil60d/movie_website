DELIMITER $$

CREATE PROCEDURE `add_movie`(
    IN movie_title VARCHAR(100),
    IN movie_year INT,
    IN movie_director VARCHAR(100),
    IN star_name VARCHAR(100),
    IN genre_name VARCHAR(32)
)
BEGIN
    DECLARE new_movie_id VARCHAR(10);
    DECLARE new_star_id VARCHAR(10);
    DECLARE new_genre_id INT;
    DECLARE max_movie_num INT;
    DECLARE max_star_num INT;

    -- Check if the movie already exists
SELECT id INTO new_movie_id FROM movies WHERE title = movie_title AND year = movie_year AND director = movie_director LIMIT 1;
IF new_movie_id IS NOT NULL THEN
        -- If the movie exists, raise an error
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate movie entry';
ELSE
        -- Generate a new movie_id
SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) + 1 INTO max_movie_num FROM movies;
IF max_movie_num IS NULL THEN
            SET max_movie_num = 0000000; -- Start with this number if table is empty
END IF;
        SET new_movie_id = CONCAT('tt', LPAD(max_movie_num, 7, '0'));

        -- Insert the movie with the new_movie_id
INSERT INTO movies (id, title, year, director) VALUES (new_movie_id, movie_title, movie_year, movie_director);
END IF;

    -- Check if the star exists and retrieve the star_id, otherwise create a new star_id
SELECT id INTO new_star_id FROM stars WHERE name = star_name LIMIT 1;
IF new_star_id IS NULL THEN
        -- Generate a new star_id
SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) + 1 INTO max_star_num FROM stars;
IF max_star_num IS NULL THEN
            SET max_star_num = 0000000; -- Start with this number if table is empty
END IF;
        SET new_star_id = CONCAT('nm', LPAD(max_star_num, 7, '0'));

        -- Insert the new star with the new_star_id
INSERT INTO stars (id, name) VALUES (new_star_id, star_name);
END IF;

    -- Check if the genre exists and retrieve the genre_id, otherwise create a new genre_id
SELECT id INTO new_genre_id FROM genres WHERE name = genre_name LIMIT 1;
IF new_genre_id IS NULL THEN
        -- Since genre_id is an INT, we can still use auto_increment or a similar method
        INSERT INTO genres (name) VALUES (genre_name);
        SET new_genre_id = LAST_INSERT_ID();
END IF;

    -- Link the movie with the star
INSERT INTO stars_in_movies (starId, movieId) VALUES (new_star_id, new_movie_id);

-- Link the movie with the genre
INSERT INTO genres_in_movies (genreId, movieId) VALUES (new_genre_id, new_movie_id);
END$$

DELIMITER ;