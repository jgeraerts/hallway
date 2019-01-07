(ns hallway.model.comments
  (:use [hallway.db :only [ transaction wrap-connection db]])
  (:require [clojure.java.jdbc :as sql]))

(def
  ^{:private true}
  commenttype-mapping {:patientlistcomment 0
                       :nutritioncomment   1})

(defn find-comments-for-record [id type]
  (sql/with-connection db
    (sql/with-query-results rs
      ["SELECT
         COMMENT,
         FORMATDATETIME(COMMENTDATE,'yyyy-MM-dd') AS DATE
        FROM COMMENTS
        WHERE RECORDID=?
        AND COMMENTTYPE=?
        ORDER BY COMMENTDATE DESC" id (type commenttype-mapping)]
      (into [] rs))))

(defn save-new-comment [recordid type comment]
  (transaction
   ( sql/insert-record
     :comments
     {:recordid recordid
      :comment comment
      :commenttype (type commenttype-mapping)})))

