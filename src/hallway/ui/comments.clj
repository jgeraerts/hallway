(ns hallway.ui.comments
  (:require [seesaw.bind :as b]
            [hallway.ui.util :as util]
            [hallway.model.comments :as comment])
  (:use [seesaw core mig]))

(defn- refresh-comments-table [root v]
  (util/run-in-background
    (let [comments (comment/find-comments-for-record v :patientlistcomment)
          nutritioncomments (comment/find-comments-for-record v :nutritioncomment)
          patientcommentstable   (select root [:#patientlistcomment])
          nutriciancommentstable (select root [:#nutritioncomment])]
      (util/load-data-in-table patientcommentstable comments)
      (util/load-data-in-table nutriciancommentstable nutritioncomments))))

(defn- add-comment-handler [appstate field id e]
  (util/run-in-background
   (let [selected-record-id @(:selected-record-id appstate)
         comment (text field)]
     (comment/save-new-comment selected-record-id id comment)
     (invoke-later 
      (refresh-comments-table (to-root e) selected-record-id)
      (text! field "")))))

(defn- make-comment-table [id]
  (scrollable
   (table
    :id id
    :model
    [:columns
     [{:key :date    :text "Datum"}
      {:key :comment :text "Opmerking"}]])))

(defn- commentspanel [appstate id]
  (let [newcommententry (text "")
        addaction (action
                   :name "Toevoegen"
                   :enabled? false
                   :handler (partial add-comment-handler appstate newcommententry id))

        panel (mig-panel
               :constraints ["","[grow,fill][fill]"]
               :items [
                       [(make-comment-table id) "span,wrap"]
                       [ newcommententry ""]
                       [ addaction ""]]) ]
    (b/bind newcommententry
            (b/transform #(-> % count (> 0)))
            (b/property addaction :enabled?))
    panel))

(defn- commenttabs [appstate]
  (tabbed-panel :placement :top
                :tabs [
                       {:title "Patienten"
                        :content (commentspanel appstate :patientlistcomment)
                        }
                       {:title "Voeding"
                        :content (commentspanel appstate :nutritioncomment)
                        }]))

(defn init [appstate]
  (let [view (commenttabs appstate)]
    (b/bind
     (:selected-record-id appstate)
     (b/filter (complement nil?))
     (b/b-do [v] (refresh-comments-table view v)))
    view))
