(ns hallway.ui.patienttable
  (:use seesaw.core))


(defn patienttable []
  (scrollable (table
               :id :patienttable
               :selection-mode :single
               :model [
                       :columns [{:key :roomnumber   :text "Kamer"}
                                 {:key :name         :text "Naam"}
                                 {:key :babyname     :text "Naam Baby"}
                                 {:key :doctor       :text "Gyneacoloog"   }
                                 {:key :pediatrician :text "Kinderarts"}
                                 {:key :day          :text "Dag"}]
                       ]
               )))

(defn init [appstate]
  (let [view (patienttable)]

    view))
