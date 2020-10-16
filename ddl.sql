-- ----------------------------------------------------------------------- 
-- ADMLOCAL 
-- ----------------------------------------------------------------------- 

DROP TABLE ADMLOCAL CASCADE CONSTRAINTS PURGE;

-- ----------------------------------------------------------------------- 
-- ADMLOCAL 
-- ----------------------------------------------------------------------- 

CREATE TABLE ADMLOCAL
(
    ADLCD VARCHAR2(10) NOT NULL,
    ADLEDESC VARCHAR2(1024),
    ADLLDESC VARCHAR2(1024),
    ADLTYPE VARCHAR2(4),
    ADLCRE TIMESTAMP NOT NULL,
    ADLCREUSR VARCHAR2(48),
    ADLUPD TIMESTAMP NOT NULL,
    ADLUSR VARCHAR2(48),
    PRIMARY KEY (ADLCD)
);

CREATE UNIQUE INDEX ADMLOCAL_UK1 ON ADMLOCAL (ADLCD, ADLEDESC);

