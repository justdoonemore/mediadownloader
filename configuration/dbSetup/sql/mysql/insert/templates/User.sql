-- Insert ${USERS.TABLE} table data
${INSERT.STATEMENT} `${MEDIASERVER.SCHEMA}`.`${USERS.TABLE}` (`name`, `password`, `roles`, `emailaddress`) VALUES ('@VALUE@', '@VALUE@', '@VALUE@', '@VALUE@');

