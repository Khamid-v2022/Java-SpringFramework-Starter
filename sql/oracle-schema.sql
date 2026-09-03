-- Oracle schema for spring-starter (run as your app user)
-- Example user: starter / starter

CREATE SEQUENCE PRODUCT_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE PRODUCTS (
    ID           NUMBER(19)        NOT NULL,
    NAME         VARCHAR2(100 CHAR) NOT NULL,
    DESCRIPTION  VARCHAR2(500 CHAR),
    PRICE        NUMBER(12, 2)     NOT NULL,
    QUANTITY     NUMBER(10)        NOT NULL,
    CREATED_AT   TIMESTAMP         NOT NULL,
    UPDATED_AT   TIMESTAMP,
    CONSTRAINT PK_PRODUCTS PRIMARY KEY (ID)
);

-- Optional sample rows (IDs come from sequence when using Hibernate)
-- Hibernate hbm2ddl.auto=update can also create objects automatically.
