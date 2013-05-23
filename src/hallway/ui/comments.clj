(ns hallway.ui.comments
  (:require [seesaw.bind :as b])
  (:use [seesaw core mig]))

(defn add-comment-handler [field id e]
 )


(defn make-comment-table [id]
  (scrollable
   (table
    :id id
    :model
    [:columns
     [{:key :date    :text "Datum"}
      {:key :comment :text "Opmerking"}]])))


(defn commentspanel [id]
  (let [newcommententry (text "")
        addaction (action
                   :name "Toevoegen"
                   :enabled? false
                   :handler (partial add-comment-handler newcommententry id))

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


(defn commenttabs []
  (tabbed-panel :placement :top
                :tabs [
                       {:title "Patienten"
                        :content (commentspanel :patientlistcomment)
                        }
                       {:title "Voeding"
                        :content (commentspanel :nutricioncomment)
                        }]))



(defn init [appstate]
  (commenttabs))
