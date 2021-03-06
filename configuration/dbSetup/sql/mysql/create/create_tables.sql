CREATE TABLE `${MEDIADOWNLOADER.SCHEMA}`.`${SERIES.TABLE}` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) UNIQUE NOT NULL,
  `season` INT NOT NULL, 
  `episode` INT NOT NULL
  );

CREATE TABLE `${MEDIADOWNLOADER.SCHEMA}`.`EntityKeyGen` (
  `genKey` VARCHAR(100) NOT NULL,
  `genValue` INT NOT NULL,
  PRIMARY KEY(`genKey`, `genValue`)
  );

CREATE TABLE `${MEDIADOWNLOADER.SCHEMA}`.`${SERIES.DOWNLOAD.TABLE}` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `seriesid` INT NOT NULL,
  `season` INT NOT NULL, 
  `episode` INT NOT NULL,
  `time` TIMESTAMP NOT NULL,
   FOREIGN KEY (`seriesid`) REFERENCES `${SERIES.TABLE}`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   UNIQUE (`seriesid`, `season`, `episode`)
  );

CREATE TABLE `${MEDIADOWNLOADER.SCHEMA}`.`${USERS.TABLE}` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) UNIQUE NOT NULL,
  `emailaddress` VARCHAR(100) NOT NULL
  );
  
CREATE TABLE `${MEDIADOWNLOADER.SCHEMA}`.`${SERIES.NOTIFICATION.TABLE}` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `userid` INT NOT NULL,
  `seriesid` INT NOT NULL,
  FOREIGN KEY (`userid`) REFERENCES `${USERS.TABLE}`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`seriesid`) REFERENCES `${SERIES.TABLE}`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE (`userid`, `seriesid`)
  );
