CREATE USER '${datasource.username}'@'localhost' IDENTIFIED BY '${datasource.password}';
GRANT ALL PRIVILEGES ON `${MEDIADOWNLOADER.SCHEMA}`.* to '${datasource.username}'@'localhost';
