CREATE TABLE ${MEDIASERVER.SCHEMA}.${SERIES.TABLE} (
  id SERIAL CONSTRAINT ${SERIES.TABLE}_primaryKey PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  season integer NOT NULL, 
  episode integer NOT NULL
  );

CREATE TABLE ${MEDIASERVER.SCHEMA}.${SERIES.DOWNLOAD.TABLE} (
  id SERIAL CONSTRAINT ${SERIES.TABLE}_primaryKey PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  season integer NOT NULL, 
  episode integer NOT NULL
  );
  
CREATE TABLE ${MEDIASERVER.SCHEMA}.${USERS.TABLE} (
  id SERIAL CONSTRAINT ${USERS.TABLE}_primaryKey PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL, 
  roles VARCHAR(100) NOT NULL,
  emailaddress VARCHAR(100) NOT NULL
  );
  
CREATE TABLE ${MEDIASERVER.SCHEMA}.${SERIES.NOTIFICATION.TABLE} (
  id SERIAL CONSTRAINT ${SERIES.NOTIFICATION.TABLE}_primaryKey PRIMARY KEY,
  userid integer REFERENCES ${MEDIASERVER.SCHEMA}.${USERS.TABLE}(id) UNIQUE NOT NULL,
  seriesid integer REFERENCES ${MEDIASERVER.SCHEMA}.${SERIES.TABLE}(id) NOT NULL
  );


UPDATE pg_class SET relowner = (SELECT oid FROM pg_roles WHERE rolname = '${datasource.username}') 
 WHERE relname IN (SELECT relname FROM pg_class, pg_namespace 
                    WHERE pg_namespace.oid = pg_class.relnamespace AND 
                          pg_namespace.nspname = '${MEDIASERVER.SCHEMA}');
