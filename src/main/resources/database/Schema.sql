CREATE DATABASE `shanggang` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE shanggang;
CREATE TABLE `customer` (
  `CUST_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) NOT NULL,
  `USER_ID` varchar(20) NOT NULL UNIQUE,
  `PASSWORD` varchar(60) NOT NULL,
  `PRIVILEGE` varchar(40),
  `UNIT` varchar(40),
  `LAST_ONLINE` DATE,
  `PHONE` varchar(20),
  PRIMARY KEY (`CUST_ID`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `harbor_date` (
  `DATE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MEASURE_DATE` varchar(5) NOT NULL,
  PRIMARY KEY(`DATE_ID`)
);

CREATE TABLE `harbor_measure` (
  `LONGITUDE` DOUBLE NOT NULL,
  `LATITUDE` DOUBLE NOT NULL,
  `DEPTH` DOUBLE NOT NULL,
  `DATE_ID` int(10) unsigned NOT NULL ,
  FOREIGN KEY(`DATE_ID`) references harbor_date(`DATE_ID`)
);

