(ns hallway.ui.select-patient-type-panel
  (:use seesaw.core hallway.ui.util)
  (:require [hallway.ui.actions :as actions]))

(defn- create-view [appstate]
  (let [back-action (actions/back appstate)
        new-womaninchildbed (action :name "Kraamvrouw")
        new-observation     (action :name "Observatie")
        new-gyneacological  (action :name "Gyneacologische")]
    (border-panel
     :border 5
     :north (toolbar :floatable? false
                     :items [back-action])
     :center (vertical-panel
              :items [new-womaninchildbed new-observation new-gyneacological]))))

(defn init [appstate]
  {:pre [((complement nil?) appstate)]}
  (create-view appstate))
