CREATE TABLE USER_ORDER
(
ID SERIAL NOT NULL,
CREATE_TIME TIMESTAMP NOT NULL,
LAST_UPDATE_TIME TIMESTAMP NOT NULL,
ORDER_STATUS VARCHAR(16) NOT NULL,
SCHEDULING_STEP_ID INTEGER NOT NULL,
PRE_PROCESSING_STEP_ID INTEGER NOT NULL,
PROCESSING_STEP_ID INTEGER NOT NULL,
POST_PROCESSING_STEP_ID INTEGER NOT NULL,
LOCK_VERSION INTEGER NOT NULL DEFAULT 1,
PRIMARY KEY (ID)
);

CREATE TABLE ORDER_STEP
(
ID SERIAL NOT NULL,
CREATE_TIME TIMESTAMP NOT NULL,
LAST_UPDATE_TIME TIMESTAMP NOT NULL,
STEP_STATUS VARCHAR(16) NOT NULL,
PRIMARY KEY (ID)
);

ALTER TABLE USER_ORDER ADD FOREIGN KEY (SCHEDULING_STEP_ID) REFERENCES ORDER_STEP (ID);
ALTER TABLE USER_ORDER ADD FOREIGN KEY (PRE_PROCESSING_STEP_ID) REFERENCES ORDER_STEP (ID);
ALTER TABLE USER_ORDER ADD FOREIGN KEY (PROCESSING_STEP_ID) REFERENCES ORDER_STEP (ID);
ALTER TABLE USER_ORDER ADD FOREIGN KEY (POST_PROCESSING_STEP_ID) REFERENCES ORDER_STEP (ID);