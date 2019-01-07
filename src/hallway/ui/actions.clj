(ns hallway.ui.actions
  (:use seesaw.core
        [hallway.ui.util :only [goto-previous-view]])
  (:require
   [clojure.tools.logging :as log]
   [seesaw.bind :as b]))

(defn back [appstate]
  (let [viewstack (:viewstack appstate)
        back-action (action
                     :icon "icons/arrow_left.png"
                     :enabled? false
                     :handler (fn [e] (goto-previous-view appstate)))]
    (b/bind viewstack
            (b/transform #(> (count %) 1))
            (b/property back-action :enabled?))
    back-action))

(defn- previous-view-handler [appstate e]
  (goto-previous-view appstate))

(defn cancel-action [appstate]
  ( action :name "Annuleren"
           :handler (partial previous-view-handler appstate)))

(defn close-action [appstate]
  (action :name "Close"
          :handler (partial previous-view-handler appstate)))


      
