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
  `WARNING_STATUS` varchar(20),
  PRIMARY KEY (`CUST_ID`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `user_depth_setting` (
  `USER_ID` VARCHAR(20) NOT NULL,
  `HARBOR_ID` int(10) unsigned NOT NULL,
  `DEPTH_LEVEL` varchar(100)
);

CREATE TABLE `user_warning_setting` (
  `USER_ID` VARCHAR(20) NOT NULL,
  `HARBOR_ID` int(10) unsigned NOT NULL,
  `RED_WARNING` double,
  `YELLOW_WARNING` double,
  `RED_WARNING2` double,
  `YELLOW_WARNING2` double
);

CREATE TABLE `harbor_date` (
  `DATE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `MEASURE_DATE` varchar(5) NOT NULL,
  `HARBOR_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY(`DATE_ID`)
);

CREATE TABLE `harbor_measure` (
  `LONGITUDE` DOUBLE NOT NULL,
  `LATITUDE` DOUBLE NOT NULL,
  `DEPTH` DOUBLE NOT NULL,
  `DATE_ID` int(10) unsigned NOT NULL ,
  FOREIGN KEY(`DATE_ID`) references harbor_date(`DATE_ID`)
);

CREATE TABLE `harbor_trend` (
  `LONGITUDE` DOUBLE NOT NULL,
  `LATITUDE` DOUBLE NOT NULL,
  `TREND` DOUBLE NOT NULL,
  `HARBOR_ID` int(10) unsigned NOT NULL
);

CREATE INDEX HARBOR_INDEX ON harbor_measure (DATE_ID);
INSERT INTO customer VALUES (2, 'sjtucit', 'admin', '8cb2237d0679ca88db6464eac60da96345513964', 'admin', NULL, '2016-01-19', NULL, 'ggggg');
