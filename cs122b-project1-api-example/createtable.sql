CREATE DATABASE IF NOT EXISTS moviedb;

USE moviedb;

CREATE TABLE IF NOT EXISTS movies (
    id varchar(10) DEFAULT '' NOT NULL PRIMARY KEY,
    title varchar(100) DEFAULT '' NOT NULL,
    year int NOT NULL,
    director varchar(100) DEFAULT '' NOT NULL
);

CREATE TABLE IF NOT EXISTS stars (
    id varchar(10) DEFAULT '' NOT NULL PRIMARY KEY,
    name varchar(100) DEFAULT '' NOT NULL,
    birthYear int
);

CREATE TABLE IF NOT EXISTS stars_in_movies (
    starId varchar(10) DEFAULT '' NOT NULL,
    movieId varchar(10) DEFAULT '' NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id),
    PRIMARY KEY (starId, movieId)
);

CREATE TABLE IF NOT EXISTS genres (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(32) DEFAULT '' NOT NULL
);

CREATE TABLE IF NOT EXISTS genres_in_movies (
    genreId int NOT NULL,
    movieId varchar(10) DEFAULT '' NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id),
    PRIMARY KEY (genreId, movieId)
);

CREATE TABLE IF NOT EXISTS creditcards (
    id varchar(20) DEFAULT '' NOT NULL PRIMARY KEY,
    firstName varchar(50) DEFAULT '' NOT NULL,
    lastName varchar(50) DEFAULT '' NOT NULL,
    expiration date NOT NULL
    );
CREATE TABLE IF NOT EXISTS customers (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firstName varchar(50) DEFAULT '' NOT NULL,
    lastName varchar(50) DEFAULT '' NOT NULL,
    ccId varchar(20) DEFAULT '' NOT NULL,
    address varchar(200) DEFAULT '' NOT NULL,
    email varchar(50) DEFAULT '' NOT NULL,
    password varchar(20) DEFAULT '' NOT NULL,
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE IF NOT EXISTS sales (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customerId int NOT NULL,
    movieId varchar(10) DEFAULT '' NOT NULL,
    saleDate date NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS ratings (
    movieId varchar(10) DEFAULT '' NOT NULL,
    rating float NOT NULL,
    numVotes int NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);
