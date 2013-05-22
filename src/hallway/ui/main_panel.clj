(ns hallway.ui.main-panel
  (:require [hallway.ui
             overview-panel
             manage-doctors-panel
             manage-rooms-panel]
            [seesaw.bind :as b]
            [clojure.tools.logging :as log])
  (:use seesaw.core))


(defn- create-view [appstate]
  (card-panel
   :items
   [[(hallway.ui.overview-panel/init appstate) :overviewpanel]
    ])
  )

(defn init [appstate]
  {:pre [(complement (nil? appstate))]}
  (let [view (create-view appstate)]
    (b/bind (:viewstack appstate)
            (b/transform first)
            (b/b-do [e]
                    (log/debug "switching card to " e)
                    (show-card! view e)))
    view))
