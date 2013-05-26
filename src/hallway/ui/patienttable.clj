(ns hallway.ui.patienttable
  (:use seesaw.core
        [hallway.ui.util :only [load-data-in-table]])
  (:require [hallway.model.patients :as patient])
  )


(defn patienttable []
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

(defn init [appstate]
  (let [view (patienttable)]
    (load-data-in-table (select view [:#patienttable]) (patient/load-patient-report))
    view))
