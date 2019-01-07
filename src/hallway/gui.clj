(ns hallway.gui
  (:use [seesaw [core :exclude (separator) ]
         dev mig table
         [options :only [apply-options]]])
  (:use hallway.datechooser)
  (:require [clojure.tools.logging :as log])
  
  (:require [hallway.db :as db])
  (:require [seesaw.bind :as b]))

(native!)


(declare
 previous-card
 refresh-patienttable
  commentspanel)

(def view-stack (atom '(:patienttable)))
(def selected-record-id (atom -1))
(defn empty-record []
  {:givennamemother ""
   :surnamemother   ""
   :givennamebaby   ""
   :surnamebaby     ""
   :caesarian       false
   :birthdate       (java.util.Date.)})


(defn clear-form [editpanel]
  (value! editpanel
          ))

(defn save-form-handler [e]
  (run-in-background
   (let [root (to-root e)
         editpanel (select root [:#edit-record-panel])
         record (value editpanel)]
     (db/save-record @selected-record-id record)
     (refresh-patienttable)
     (previous-card))))

(defn validate [widget validationfunction]
  (b/bind
   widget
   (b/transform validationfunction)
   (b/transform #(if % "lightcoral" "white"))
   (b/property widget :background))
  )


(defn add-record-validations [panel]
  (validate (select panel [:#surnamemother]) empty?)
  (validate (select panel [:#givennamemother]) empty?)
  (validate (select panel [:#surnamebaby]) empty?)
  (validate (select panel [:#givennamebaby]) empty?))


(defn make-edit-record-panel []
  (let [
        gyneacologists ["Jos" "Piet" "Jaak"]
        pediatricians  ["Klaas" "Karel" "Lowie"]
        panel
        (mig-panel
         :id :edit-record-panel
         :constraints ["inset 10", "[right][150lp,fill][right][150lp,fill]"]
         :items [ [ "Mama" "split, span, gaptop 10"]
                  [ :separator "growx, wrap, gaptop 10"]
                  [ "Naam" "gap 10" ]
                  [ (text :id :surnamemother) ""]
                  [ "Voornaam" "gap 10"]
                  [ (text :id :givennamemother) "wrap"]
                  [ "Gyneacoloog" "gap 10"]
                  [ (combobox :id :gyneacologist
                              :model gyneacologists)]
                  [ "Voeding" "gap 10"]
                  [ (combobox :id :nutrition
                              :model ["BV" "KV/Nutrilon " "KV/Nan"]) "wrap"]
                  [ "Sectio?" "gap 10"]
                  [ (checkbox :id :caesarian) "wrap"]
                  [ "Baby" "split,span,gaptop 10"]
                  
                  [ :separator "growx,wrap,gaptop 10"]
                  [ "Naam" "gap 10"] [ (text :id :surnamebaby) ""]
                  [ "Voornaam" "gap 10"]
                  [ (text :id :givennamebaby) "wrap"]
                  [ "Geboortedatum" "gap 10"]
                  [ (date-chooser :id :birthdate) ""]
                  [ "Kinderarts" "gap 10"]
                  [ (combobox :id :pediatrician
                              :model pediatricians ) "wrap"]
                  [ "Ziekenhuis" "split,span,gaptop 10"]
                  
                  [ :separator "growx,wrap,gaptop 10"]              
                  [ "Kamer" "gap 10"]
                  [ (combobox :id :roomnumber :model []) "wrap"]

                  
                  [ (action :name "Opslaan"
                            :handler save-form-handler) "span 4, split 2 , tag ok" ]
                  [ (action :name "Annuleren"
                            :handler (fn [e] ( previous-card))) "tag cancel"]])]
    (add-record-validations panel)
    panel))

(def patienttable
  (table
   :id :patienttable
   :selection-mode :single
   :model [
           :columns [{:key :roomnumber   :text "Kamer"}
                     {:key :name         :text "Naam"}
                     {:key :babyname     :text "Naam Baby"}
                     {:key :doctor       :text "Gyneacoloog"   }
                     {:key :pediatrician :text "Kinderarts"}
                     {:key :day          :text "Dag"}]
           ]
   ))

(defn set-data-in-table!
  [table data]
  {:pre [(not ( nil? table))]}
  (clear! table)
  (if-not (empty? data)
    (apply insert-at! table (interleave (iterate identity 0) data)))) 

(defn refresh-comments-table [root]
  (run-in-background
    (let [v @selected-record-id
          comments (db/load-comments-for-record v :patientlistcomment)
          nutritioncomments (db/load-comments-for-record v :nutritioncomment)
          patientcommentstable   (select root [:#patientlistcomment])
          nutriciancommentstable (select root [:#nutricioncomment])]
      (invoke-later
       (set-data-in-table! patientcommentstable comments)
       (set-data-in-table! nutriciancommentstable nutritioncomments)
       ))))

(defn add-comment-handler [field id e]
  (run-in-background
   (let [comment (text field)]
     (db/insert-new-comment @selected-record-id id comment)
     (invoke-later 
      (refresh-comments-table (to-root e))
      (text! field "")))))

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


(defn refresh-patienttable []
  (run-in-background
   (let [records (db/get-records)]
     (invoke-later 
      (set-data-in-table! patienttable records)))))

(defn make-main-panel []
  (top-bottom-split (scrollable patienttable) (commenttabs) :divider-location 1/2))

(def maincardpanel (card-panel
                    :items [
                            [ (make-main-panel)        :patienttable]
                            [ (make-edit-record-panel) :hospitalizationpanel]]))

(defn switch-to-card [id]
  (if (not (= (first @view-stack) id))
    (do 
      (swap! view-stack (fn [x]  ( cons id x)))
      (show-card! maincardpanel id)
      )))

(defn previous-card []
  (swap! view-stack rest)
  (show-card! maincardpanel (first @view-stack)))

(defn show-edit-record-form [root record]
  (run-in-background
    (let [form            (select root [:#edit-record-panel])
          caesariancheck  (select root [:#caesarian])
          roomcombobox    (select form [:#roomnumber])
          roomnumber      (:roomnumber record)
          available-rooms (if
                              (nil? roomnumber)
                            (db/load-rooms)
                            (conj (db/load-rooms) roomnumber))]
      (invoke-later
       (value! form record)
       (value! caesariancheck (:caesarian record))
       (config! roomcombobox :model available-rooms)
       (switch-to-card :hospitalizationpanel)))))

(defn dismiss-patient-handler [e]
  (run-in-background
   (db/dismiss-patient @selected-record-id)
   (refresh-patienttable)))

(defn mainpanel []
  (
    (border-panel
     :border 5
     :north  (toolbar :floatable? false :items [new-action dismiss-action back-action])
     :center maincardpanel )))


(defn patienttable-click-handler [e]
  (run-in-background
   (let [clickCount (.getClickCount e)
         root (to-root e)]
     (if (and (not (nil?  @selected-record-id)) (= 2 clickCount))
        (show-edit-record-form root (db/get-record-with-id @selected-record-id))))))

(defn add-behaviours [root]
  (b/bind
   (b/selection patienttable)
   (b/transform #(-> (value-at patienttable %) :id))
   (b/tee
    selected-record-id
    (b/b-do [v] (refresh-comments-table root))))

  (listen patienttable :mouse-clicked patienttable-click-handler)
  root)


 
(defn build-gui
  ([] (build-gui true))
  ([debug]
     (invoke-later
      (if debug (debug!))
      (-> (mainframe debug)
          add-behaviours
          show!)
      (refresh-patienttable))))
