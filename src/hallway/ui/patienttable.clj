(ns hallway.ui.patienttable
  (:use seesaw.core
        [hallway.ui.util :only [load-data-in-table]])
  (:require [hallway.model.patients :as patient]
            [seesaw.bind :as b]
            [seesaw.table :as tbl]
            [hallway.ui.util :as util]))


(defn create-view []
  (scrollable (table
               :id :patienttable
               :selection-mode :single
               :model [:columns [{:key :roomnumber   :text "Kamer"}
                                 {:key :name         :text "Naam"}
                                 {:key :babyname     :text "Naam Baby"}
                                 {:key :doctor       :text "Gyneacoloog"   }
                                 {:key :pediatrician :text "Kinderarts"}
                                 {:key :day          :text "Dag"}]
                       ])))

(defn- click-handler [appstate e]
  (let [clickCount (.getClickCount e)
        root       (to-root e)
        selected-record-id @(:selected-record-id appstate)]
    (when (and (= clickCount 2) (not (nil? selected-record-id)))
      (util/push-to-viewstack :editform (:viewstack appstate)))))

(defn refresh-patienttable [view]
  (util/run-in-background 
   (let [data (patient/load-patient-report)]
     (load-data-in-table (select view [:#patienttable])
                         data))))

(defn init [appstate]
  (let [view (create-view)
        patienttable  (select view [:#patienttable])]
    (b/bind
     (b/selection patienttable)
     (b/transform #(-> (tbl/value-at patienttable %) :id))
     (b/transform #(if (nil? %1) -1 (identity %1)))
     (:selected-record-id appstate))
    
    (listen patienttable :mouse-clicked (partial click-handler appstate))
    (refresh-patienttable view)    
    view))
