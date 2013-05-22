(ns hallway.model.rooms
  (:use [hallway.db :only [ transaction wrap-connection db]])
  (:require [clojure.java.jdbc :as sql]))


(defn find-rooms-for-patient [id]
  (sql/with-connection db
    (sql/with-query-results rs
      [ "SELECT
         ROOM.ROOMNUMBER
        FROM ROOMS ROOM
        LEFT OUTER JOIN RECORDS R ON R.ROOMNUMBER = ROOM.ROOMNUMBER
        WHERE
           R.ID IS NULL
           OR R.ID=?" id]
      (map #(:roomnumber %) (into [] rs)))))
