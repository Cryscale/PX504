DROP DATABASE IF EXISTS smart_remote;

CREATE DATABASE smart_remote;

USE smart_remote;

CREATE TABLE Lampes (
  id  TINYINT PRIMARY KEY AUTO_INCREMENT,
  user VARCHAR(2)
  etat VARCHAR(3) NOT NULL,
  location VARCHAR(40) NOT NULL,
  brightness TINYINT NOT NULL,
  CONSTRAINT fk_client_id
  	FOREIGN KEY ()
);

CREATE TABLE Utilisateurs (
  surname  VARCHAR(40) PRIMARY KEY,
  nom VARCHAR(40) NOT NULL,
  prenom VARCHAR(40) NOT NULL,
  mdp VARCHAR(256) NOT NULL,
  code CHAR(4)
);

LOAD DATA LOCAL INFILE 'donnees_lampe.csv'
INTO TABLE Lampes
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(location,brightness);

LOAD DATA LOCAL INFILE 'donnees_utilisateurs.csv'
INTO TABLE Utilisateurs
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(nom,prenom,mdp,code);
