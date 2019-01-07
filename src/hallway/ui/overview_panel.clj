(ns hallway.ui.overview-panel
  (:use seesaw.core hallway.ui.util)
  (:require  [hallway.ui
              [actions :as actions]
              [patienttable :as patienttable]
              [comments :as comments]
              [util :as util]]
            [hallway.model.patients :as patient] 
            [clojure.tools.logging :as log]
            [seesaw.bind :as b]))

(defn dismiss-patient-handler [appstate e]
  (util/run-in-background
   (patient/dismiss-patient @(:selected-record-id appstate))
   (patienttable/refresh-patienttable (to-root e))))


(defn create-view [appstate]
  {:pre [((complement nil?) appstate)]}
  (let [new-action (action :icon "icons/add.png"
                           :handler (fn [e]
                                      (do 
                                        (reset! (:selected-record-id appstate) -1)
                                        (push-to-viewstack :editform
                                                           (:viewstack appstate)))))
        dismiss-action (action
                        :icon "icons/delete.png"
                        :enabled? false
                        :handler (partial dismiss-patient-handler appstate)
                        )]
    (b/bind (:selected-record-id appstate)
            (b/transform #(and (not (nil? %)) ( > % 0)))
            (b/property dismiss-action :enabled?))
    

    (border-panel
     :border 5
     :north (toolbar :floatable? false
                     :items [new-action dismiss-action])
     :center (top-bottom-split
              (patienttable/init appstate)
              (comments/init appstate) :divider-location 1/2)))
  
  )

(defn init [appstate]
  {:pre [((complement nil?) appstate)]}
  (create-view appstate))
   
