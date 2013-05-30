(ns hallway.ui.report
  (:require [clojure.java.jdbc :as jdbc])
  (:require [clojure.java.io :as io]
            [hallway.db])
  (:use [seesaw.core])
  (:use hallway.ui.util)
  (:import [net.sf.jasperreports view.JRViewer engine.JasperCompileManager engine.JasperFillManager]))

(defn
  ^{:private true}
  fillreport [compiledReport]
  (jdbc/with-connection hallway.db/db
    (JasperFillManager/fillReport
     compiledReport
     (java.util.HashMap.)
     (jdbc/find-connection))))

(defn load-report [report]
  (run-in-background  
   (let [url (io/resource report)
         compiledReport (JasperCompileManager/compileReport (.openStream url))
         filledReport (fillreport compiledReport)  ]
     (invoke-later 
      (-> (custom-dialog
           :content (JRViewer. filledReport)
           :width 850
           :height 600)  show!)))))
