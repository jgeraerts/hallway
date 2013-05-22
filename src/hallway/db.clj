(ns hallway.db
  (:use [clojure.tools.logging :as log])
  (:require [clojure.java.jdbc :as sql])
  (:import java.lang.IllegalStateException))

(def db  {
          :classname   "org.h2.Driver"
          :subprotocol "h2:file"
          :subname     (str (System/getProperty "user.home") "/" ".hallway" "/" "hallway")
          :user        "sa"
          :password    "" }  )


;; ----------------------------------------------------------------------------
;;  Explicit connections prevent composition.  So we wrap all db functions in
;;  a fn that checks if a connection exists, and only if not does it make one
;;  that way the outermost fn that needs a db creates the connection and those
;;  internal use it.
 
(defmacro wrap-connection [& body]
  `(if (sql/find-connection)
    ~@body
    (sql/with-connection db ~@body)))
 
(defmacro transaction [& body]
  `(if (sql/find-connection)
     (sql/transaction ~@body)
     (sql/with-connection db (sql/transaction ~@body))))

(def ^{:private true} migrations
  [
   "CREATE TABLE MIGRATIONS (ID INT PRIMARY KEY, EXECUTION_DATETIME TIMESTAMP AS NOW() NOT NULL)"
   "CREATE TABLE ROOMS (
            ROOMNUMBER VARCHAR(8) PRIMARY KEY,
            )"
   "CREATE TABLE DOCTORS (
           INITIALS VARCHAR(32) PRIMARY KEY,
           TYPE INT NOT NULL,
           NAME VARCHAR(255) NOT NULL,
           DELETED BOOLEAN NOT NULL DEFAULT FALSE)"
   "CREATE TABLE PATIENTS (
            ID BIGINT PRIMARY KEY AUTO_INCREMENT,
            TYPE TINYINT NOT NULL,
            SURNAMEMOTHER VARCHAR(255),
            GIVENNAMEMOTHER VARCHAR(255),
            GYNEACOLOGIST INT,
            PEDIATRICIAN INT,
            NUTRITION VARCHAR(255),
            SURNAMEBABY VARCHAR(255),
            GIVENNAMEBABY VARCHAR(255),
            BIRTHDATE TIMESTAMP,
            ROOMNUMBER VARCHAR(8),
            DISMISSED BOOLEAN AS FALSE
            )"
   "ALTER TABLE PATIENTS ADD CONSTRAINT FK_ROOMNUMBER       FOREIGN KEY (ROOMNUMBER)    REFERENCES ROOMS(ROOMNUMBER)"
   "ALTER TABLE PATIENTS ADD CONSTRAINT FK_GYNEACOLOGIST    FOREIGN KEY (GYNEACOLOGIST) REFERENCES DOCTORS(INITIALS)"
   "ALTER TABLE PATIENTS ADD CONSTRAINT FK_PEDIATRICIAN     FOREIGN KEY (PEDIATRICIAN)  REFERENCES DOCTORS(INITIALS)"
   "CREATE UNIQUE INDEX IDX_ROOMNUMBER ON PATIENTS(ROOMNUMBER)"
   "CREATE TABLE COMMENTS (
            ID INT PRIMARY KEY AUTO_INCREMENT,
            RECORDID BIGINT NOT NULL,
            COMMENTDATE TIMESTAMP AS NOW() NOT NULL,
            COMMENTTYPE TINYINT NOT NULL,
            COMMENT VARCHAR(255) NOT NULL)"
   "ALTER TABLE COMMENTS ADD CONSTRAINT FK_RECORDID FOREIGN KEY (RECORDID) REFERENCES PATIENTS(ID)"
   
   "INSERT INTO ROOMS (ROOMNUMBER)
    VALUES ('401'),('402'),('403'),('404'),('405'),('406'),('407'),
           ('408'),('409'),('410'),('411'),('412'),('413'),('414'),
           ('415'),('416/1'),('416/2'),('417/1'),('417/2'),('418/1'),
           ('418/2'),('419/1'),('419/2'),('420/1'),('420/2')"])

(defn- next-upgrade-number []
  (if (-> (sql/find-connection) .getMetaData (.getTables nil nil "MIGRATIONS" nil) .next)
    (sql/with-query-results rows ["SELECT MAX(id) + 1 AS migration_no FROM MIGRATIONS"] (:migration_no (first rows)))
    0))

(defn create []
  (sql/with-connection db
    (loop [idx (next-upgrade-number)]
      (if (< idx (count migrations))
        (do (sql/transaction
             (sql/do-commands
              (migrations idx)
              (str "INSERT INTO MIGRATIONS (id) VALUES (" idx ")")))
            (recur (inc idx)))))))


