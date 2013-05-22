(ns hallway.ui.overview-panel
  (:use seesaw.core hallway.ui.util)
  (:require [hallway.ui.actions :as actions]
            [clojure.tools.logging :as log]
            [seesaw.bind :as b]))

(def- popupactions
  [(action :name ::new-type-normalbirth)
   (action :name ::new-type-caesarian)
   (action :name ::new-type-observation)
   (action :name ::new-type-gynealogical)
   ])

(defn- show-new-patient-popup [e]
  (let [widget (to-widget e)
        p (popup :items popupactions)]
    (.show p widget 0 (.getHeight widget))))

(defn create-view [appstate]
  {:pre [((complement nil?) appstate)]}
  (log/info "create-view")
  (let [back-action (actions/back appstate)
        
        new-action (action :icon "icons/add.png"
                            :handler show-new-patient-popup)
        dismiss-action (action
                        :icon "icons/delete.png"
                        :enabled? false
                                        ;:handler dismiss-patient-handler
                        )]
    (b/bind (:selected-record-id appstate)
            (b/transform #(and (not (nil? %)) ( > % 0)))
            (b/property dismiss-action :enabled?))
    

    (border-panel
     :border 5
     :north (toolbar :floatable? false
                     :items [back-action new-action dismiss-action])
     :center (label :text "todo")))
  
  )

(defn init [appstate]
  {:pre [((complement nil?) appstate)]}
  (create-view appstate))
   
