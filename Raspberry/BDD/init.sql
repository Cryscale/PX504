DROP DATABASE IF EXISTS smart_remote;

CREATE DATABASE smart_remote;

USE smart_remote;

CREATE TABLE Lamp (
  id  TINYINT PRIMARY KEY AUTO_INCREMENT,
  state VARCHAR(3) NOT NULL,
  location VARCHAR(40) NOT NULL,
  brightness TINYINT NOT NULL
);

CREATE TABLE User (
  username  VARCHAR(150) PRIMARY KEY,
  lastname VARCHAR(40) NOT NULL,
  firstname VARCHAR(40) NOT NULL,
  password VARCHAR(150) NOT NULL
);

CREATE TABLE Control (
  username VARCHAR(150) NOT NULL,
  id TINYINT NOT NULL,
  CONSTRAINT pk_Control PRIMARY KEY (username, id),
  CONSTRAINT FOREIGN KEY (username) REFERENCES User(username),
  CONSTRAINT FOREIGN KEY (id) REFERENCES Lamp(id)

);

LOAD DATA LOCAL INFILE 'donnees_lampe.csv'
INTO TABLE Lamp
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(state,location,brightness);

LOAD DATA LOCAL INFILE 'donnees_utilisateurs.csv'
INTO TABLE User
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(username,lastname,firstname,password);

LOAD DATA LOCAL INFILE 'donnees_control.csv'
INTO TABLE Control
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(username, id);