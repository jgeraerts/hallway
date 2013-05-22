(ns hallway.ui.actions
  (:use seesaw.core)
  (:require
   [clojure.tools.logging :as log]
   [seesaw.bind :as b]))

(defn back [appstate]
  (let [viewstack (:viewstack appstate)
        back-action (action
                     :icon "icons/arrow_left.png"
                     :enabled? false
                     :handler (fn [e] (swap! (:viewstack appstate) rest)))]
    (b/bind viewstack
            (b/transform #(> (count %) 1))
            (b/property back-action :enabled?))
    back-action))
