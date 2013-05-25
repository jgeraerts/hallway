(ns hallway.ui.main-frame
  (:require [hallway.ui
             menubar report main-panel]
            [hallway.model
             [patients :as patients]
             [doctors :as doctors]
             [rooms :as  rooms]])
  (:use seesaw.core))

(defn create-app-state [debug]
  {:debug (atom debug)
   :doctors (atom (doctors/find-all-doctors))
   :viewstack (atom '(:overviewpanel))
   :selected-record-id (atom -1)})

(defn create-main-frame [appstate]
  (frame
   :icon "icons/nurse.png"
   :title ::frame-title
   :on-close (if @(:debug appstate) :dispose :exit)
   :size [800 :by 600]
   :menubar (hallway.ui.menubar/create-menubar appstate)
   :content (hallway.ui.main-panel/init appstate)))

(native!)

(defn init
  [debug]
  (let [appstate  (create-app-state debug)
        main-view (create-main-frame appstate)] 
    (invoke-later
     (-> main-view
         pack!
         show!))))
