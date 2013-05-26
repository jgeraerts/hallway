(ns hallway.model.doctors
  (:use [hallway.db :only [ transaction wrap-connection db]])
  (:use [clojure.set :only [map-invert]])
  (:require [clojure.java.jdbc :as sql]
            [clojure.tools.logging :as log]))

(def
  ^{:private true}
  doctor-type-mapping
  {:gyneacologist 0
   :pediatrician  1})

(def
  ^{:private true}
  inverse-doctor-type-mapping
  (map-invert doctor-type-mapping))

(defn map-type
  ^{private true}
  [mapping record]
  ( update-in record [:type] mapping))

(def translate-type-to-sql
  ^{:private true}
  (partial map-type doctor-type-mapping))

(def translate-type-from-sql
  ^{:private true}
  (partial map-type inverse-doctor-type-mapping))

(defn remove-deleted-flag
  ^{:private true}
  [record]
  (dissoc record :deleted))

(def translate-row
  ^{:private true}
  (comp remove-deleted-flag translate-type-from-sql))

(defn save-doctor
  [record]
  (log/debug "saving record " record)
  (let [id (:id record)]
    (transaction
     (sql/update-or-insert-values
      :doctors
      ["ID=?" id]
      (translate-type-to-sql (dissoc record :id))))))

(defn find-all-doctors
  []
  (wrap-connection
   (sql/with-query-results
     rows
     ["SELECT *
       FROM DOCTORS
       WHERE DELETED=FALSE
       ORDER BY INITIALS"]
     (doall (map translate-row rows)))))

(defn delete-doctor
  [initial]
  (transaction
   (sql/update-values
    :doctors
    ["INITIALS=? AND DELETED=FALSE" initial]
    {:deleted true})))

(defn undelete-doctor
  [initial]
  (transaction
   (sql/update-values
    :doctors
    ["INITIALS=? AND DELETED=TRUE" initial]
    {:deleted false})))
