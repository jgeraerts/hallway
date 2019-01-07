(ns hallway.model.patients
  (:use [hallway.db :only [ transaction wrap-connection db]])
  (:use [clojure.set :only [map-invert]])
  (:require [clojure.tools.logging :as log])
  (:require [clojure.java.jdbc :as sql]))


(defn save-patient [id attr]
  (let [record (select-keys attr
                            [:surnamemother :givennamemother
                             :gyneacologist :nutrition
                             :caesarian     :surnamebaby
                             :givennamebaby :birthdate
                             :pediatrician  :roomnumber
                             :type          :nutritionamount
                             :nutritionpercent :nutritionadditives
                             :pediatricianfirsttime
                             :pediatricianhome])]
    (log/debug "upsert record with id" id "and values" record)
    (transaction
     (sql/update-or-insert-values :patients ["id=?" id] record))))

(defn dismiss-patient [id]
  (transaction
   (sql/update-values
    :patients  ["id=?" id] {:dismissed true :roomnumber nil})))

(defn get-patient-by-id [id]
  (sql/with-connection db
    ( sql/with-query-results rs
      ["SELECT * FROM PATIENTS WHERE ID=?" id]
      (first rs))))

(defn load-patient-report []
  (sql/with-connection db
    (sql/with-query-results rs
      ["SELECT
          R.ID,
          ROOM.ROOMNUMBER,
          R.SURNAMEMOTHER || ' ' || R.GIVENNAMEMOTHER AS NAME,
          R.SURNAMEBABY   || ' ' || R.GIVENNAMEBABY   AS BABYNAME,
          G.INITIALS AS DOCTOR,
          P.INITIALS AS PEDIATRICIAN,
          DECODE(R.TYPE, 3 , 'G', 2 , 'O' , 1, 'S', 0, 'D')
                 || ' '
                 || DATEDIFF('DAY',R.BIRTHDATE,CURRENT_DATE)  AS DAY
        FROM PATIENTS R
        INNER JOIN DOCTORS P ON P.ID=R.PEDIATRICIAN
        INNER JOIN DOCTORS G ON G.ID=R.GYNEACOLOGIST
        RIGHT OUTER JOIN ROOMS ROOM ON ROOM.ROOMNUMBER = R.ROOMNUMBER"]
      (into [] rs))))

