(ns hallway.model.rooms
  (:use [hallway.db :only [ transaction wrap-connection db]])
  (:require [clojure.java.jdbc :as sql]
            [clojure.tools.logging :as log]))

(defn save-room [nr]
  (log/debug "saving room number " nr)
  (transaction
   (sql/insert-record :rooms nr)))

(defn all-rooms []
  (wrap-connection
   (sql/with-query-results rs
     ["SELECT
        ROOMNUMBER
       FROM ROOMS"]
     (into [] rs))))

(defn remove-rooms [records]
  (log/debug "deleting records " records)
  (transaction
   (sql/delete-rows :rooms ["ROOMNUMBER=?" (:roomnumber records)])))

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
