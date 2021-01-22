-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE FUNCTION `fristPinyin` (P_NAME VARCHAR(255)) RETURNS varchar(255) CHARSET utf8 
BEGIN 
  DECLARE V_RETURN VARCHAR(255); 
  SET V_RETURN = ELT(INTERVAL(CONV(HEX(left(CONVERT(P_NAME USING gbk),1)),16,10), 
        0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7, 
        0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,
        0xC8F6,0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1), 
        'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P','Q','R','S','T','W','X','Y','Z');
  if(isnull(V_RETURN)) then 
       set V_RETURN = P_NAME; 
  end if; 
  return V_RETURN; 
END