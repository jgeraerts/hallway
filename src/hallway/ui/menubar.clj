(ns hallway.ui.menubar
  (:require [hallway.ui.report :as report])
  (:use seesaw.core))

(defn create-menubar [appstate]
  (let [
        exit-action       (action 
                           :name ::exit
                           :handler (fn [e]
                                      (if @(:debug appstate)
                                        (dispose! (to-widget e))
                                        (System/exit 0))))
        patient-report    (action
                           :name ::patientlist
                           :handler
                           (fn [e] (report/load-report "patientenlijst.jrxml")))
        nutrition-report  (action
                           :name ::nutritionlist )
        edit-doctors      (action
                           :name ::doctors)
        edit-rooms        (action
                           :name ::rooms)
        menu-bar          (menubar
                           :items [(menu :text ::menu-file
                                         :items [exit-action])
                                   (menu :text ::menu-edit
                                         :items [edit-doctors edit-rooms])
                                   (menu :text ::menu-reports
                                         :items
                                         [patient-report nutrition-report])])]
    menu-bar))
