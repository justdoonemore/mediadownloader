CREATE USER '${datasource.username}'@'${DATABASE.USER.HOST}' IDENTIFIED BY '${datasource.password}';
CREATE USER '${user.name}'@'192.168.1.194' IDENTIFIED BY PASSWORD '*45F9ABB1D55C31BEEE498B353447ABD69F5E1C61';
GRANT ALL PRIVILEGES ON `${JBOSS.SCHEMA}`.* to '${datasource.username}'@'${DATABASE.USER.HOST}';
GRANT ALL PRIVILEGES ON `${MEDIASERVER.SCHEMA}`.* to '${datasource.username}'@'${DATABASE.USER.HOST}';
GRANT ALL ON *.* to '${user.name}'@'localhost';
