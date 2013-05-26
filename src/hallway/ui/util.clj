(ns hallway.ui.util
  (:use seesaw.core)
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [seesaw.table :as tbl]))

(defn handle-exception [ex]
  (log/error ex "exception in background process")
  (invoke-later
   (-> (dialog :type :error :content "an error occured") pack! show!)))
  
(defmacro run-in-background [& body]
  `(future
     (try
       ~@body
       (catch Exception ex#
         (handle-exception ex#)))))

(defn push-to-viewstack [item viewstack]
  (when-not
      (= item (first @viewstack))
    (do (swap! viewstack (fn [coll] (conj coll item)))
        (log/debug "current viewstack looks like " @viewstack))))

(defn goto-previous-view [appstate]
  (swap! (:viewstack appstate) rest))

(defn trim-values [m]
  (into {} (for [[k v] m] [k (if (string? v) (str/trim v) v)])))

(defn load-data-in-table [table data]
  (tbl/clear! table)
  (doseq [d (reverse data)]
    (tbl/insert-at! table 0 d)))
