(ns hallway.model.doctors
  (:use [hallway.db :only [ transaction wrap-connection db]])
  (:use [clojure.set :only [map-invert]])
  (:require [clojure.java.jdbc :as sql]))

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
  (let [initials (:initials record)]
    (transaction
     (sql/update-or-insert-values
      :doctors
      ["INITIALS=?" initials]
      (translate-type-to-sql record)))))

(defn find-all-doctors
  []
  (wrap-connection
   (sql/with-query-results
     rows
     ["SELECT * FROM DOCTORS WHERE DELETED=FALSE"]
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
