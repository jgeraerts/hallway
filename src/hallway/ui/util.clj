(ns hallway.ui.util
  (:use seesaw.core)
  (:require [clojure.tools.logging :as log]))

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
    (do (swap! viewstack (fn [coll] (conj coll item))))))
