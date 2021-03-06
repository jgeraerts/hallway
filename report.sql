SELECT ROOM.ROOMNUMBER, G.INITIALS, T.DAY, G.INITIALS, T.NAME, T.NUTRITION, C.COMMENT, T.BABYNAME 
FROM (
  SELECT
          R.ID,
          R.SURNAMEMOTHER || ' ' || R.GIVENNAMEMOTHER AS NAME,
          R.SURNAMEBABY   || ' ' || R.GIVENNAMEBABY   AS BABYNAME,
          R.GYNEACOLOGIST,
          R.PEDIATRICIAN,
          R.NUTRITION,
          DECODE(R.TYPE, 3 , 'G', 2 , 'O' , 1, 'S', 0, 'D')
                 || ' '
                 || DATEDIFF('DAY',R.BIRTHDATE,CURRENT_DATE)  AS DAY,
          R.ROOMNUMBER
  FROM PATIENTS R
  WHERE R.TYPE IN (0,1,3)
        AND R.DISMISSED = false
  UNION SELECT
          R.ID,
          R.SURNAMEMOTHER || ' ' || R.GIVENNAMEMOTHER AS NAME,
          R.SURNAMEBABY   || ' ' || R.GIVENNAMEBABY   AS BABYNAME,
          R.GYNEACOLOGIST,
          R.PEDIATRICIAN,
          'Obs' AS NUTRITION,
          ''||(40+DATEDIFF('MONTH',R.BIRTHDATE,CURRENT_DATE))  AS DAY,
          R.ROOMNUMBER
  FROM PATIENTS R
  WHERE R.TYPE IN (2)
        AND R.DISMISSED = false
) AS t
JOIN DOCTORS G ON G.ID = T.GYNEACOLOGIST
JOIN COMMENTS c on c.recordid=T.id
JOIN (
  SELECT recordid,MAX(commentdate) AS commentdate 
  FROM COMMENTS
  WHERE COMMENTTYPE=0
  GROUP BY recordid) GC ON ( gc.recordid = t.id and gc.commentdate = c.commentdate)
RIGHT OUTER JOIN ROOMS ROOM ON ROOM.ROOMNUMBER=T.ROOMNUMBER


LEFT  OUTER 


RIGHT OUTER JOIN ROOMS ROOM ON ROOM.ROOMNUMBER=T.ROOMNUMBER

select * from comments


SELECT R.BIRTHDATE, CURRENT_DATE, -1*SET(@WEEKS,DATEDIFF('SECOND',R.BIRTHDATE,CURRENT_DATE)/7/3600/24) || '.' || @WEEKS*7-(DATEDIFF('SECOND',R.BIRTHDATE,CURRENT_DATE)/24/3600)
FROM PATIENTS R WHERE R.TYPE = 2


SELECT
          ROOM.ROOMNUMBER,
          R.SURNAMEMOTHER || ' ' || R.GIVENNAMEMOTHER AS NAME,
          R.SURNAMEBABY   || ' ' || R.GIVENNAMEBABY   AS BABYNAME,
          R.GYNEACOLOGIST,
          P.NAME,
          R.PEDIATRICIANFIRSTTIME,
          R.PEDIATRICIANHOME,
          R.NUTRITION,
	R.BIRTHDATE,
	R.NUTRITION,
	R.NUTRITIONAMOUNT,
	R.NUTRITIONADDITIVES,
	R.NUTRITIONPERCENT,
          DECODE(R.TYPE, 3 , 'G', 2 , 'O' , 1, 'S', 0, 'D')
                 || ' '
                 || DATEDIFF('DAY',R.BIRTHDATE,CURRENT_DATE)  AS DAY,
          R.ROOMNUMBER,
          C.COMMENT
  FROM PATIENTS R
  JOIN DOCTORS P ON P.ID = R.PEDIATRICIAN
JOIN COMMENTS c on c.recordid=R.id
 JOIN (
  SELECT recordid,MAX(commentdate) AS commentdate 
  FROM COMMENTS
  GROUP BY recordid) GC ON ( gc.recordid = r.id and gc.commentdate = c.commentdate)
RIGHT OUTER JOIN ROOMS ROOM ON ROOM.ROOMNUMBER=R.ROOMNUMBER

  LEFT OUTER JOIN DOCTORS P ON P.ID = R.PEDIATRICIAN
  RIGHT OUTER JOIN ROOMS ROOM ON R.ROOMNUMBER = ROOM.ROOMNUMBER
  LEFT JOIN COMMENTS c on c.recordid=R.id
  LEFT JOIN (
  SELECT recordid,MAX(commentdate) AS commentdate 
  FROM COMMENTS
  WHERE COMMENTTYPE = 1 
  GROUP BY recordid) GC ON ( gc.recordid = R.id and gc.commentdate = c.commentdate)
  WHERE R.DISMISSED = false
  

select * from comments

