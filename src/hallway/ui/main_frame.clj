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
   :doctors (atom [])
   :viewstack (atom '(:overviewpanel))
   :selected-record-id (atom nil)})

(defn create-main-frame [appstate]
  (frame
   :icon "icons/nurse.png"
   :title ::frame-title
   :on-close (if @(:debug appstate) :dispose :exit)
   :size [800 :by 600]
   :menubar (hallway.ui.menubar/create-menubar appstate)
   :content (hallway.ui.main-panel/init appstate)))

(defn- load-data [appstate]
  (reset! (:doctors appstate) (doctors/find-all-doctors)))

(native!)

(defn init
  [debug]
  (let [appstate  (create-app-state debug)
        main-view (create-main-frame appstate)]
    (load-data appstate)
    (invoke-later
     (-> main-view

         show!))))
