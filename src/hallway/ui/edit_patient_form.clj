(ns hallway.ui.edit-patient-form
  (:use [seesaw
         [core :exclude (separator)]
         mig]
        hallway.ui.datechooser
        hallway.ui.actions
        [hallway.ui.util :only [load-data-in-table goto-previous-view]])
  (:require [seesaw.bind :as b]
            [clojure.tools.logging :as log]
            [hallway.model.patients :as patient]
            [hallway.model.doctors  :as doctor]
            [hallway.model.rooms :as room]))

(def types
  {0  "Vaginale Geboorte"
   1  "Sectio"
   2  "Observatie"
   3  "Gyneacologische patient"})

(defn- type-renderer [renderer {:keys [value]}]
  (apply config! renderer [:text (types value)]))

(defn- doctor-renderer [renderer {:keys [value]}]
  (let [doctor (doctor/get-doctor-by-id value)]
    (apply config! renderer [:text (str (:initials doctor) " - " (:name doctor))])))

(defn- save-handler [appstate e]
  (let [formvalue (value (select (to-root e) [:#editpatientform]))]
    (patient/save-patient
     @(:selected-record-id appstate)
     formvalue)
    (load-data-in-table
     (-> e to-root (select [:#patienttable]))
     (patient/load-patient-report))
    (goto-previous-view appstate)))


(defn- create-form [appstate]
  (mig-panel
   :id :editpatientform
   :constraints ["inset 10", "[right]10[150lp,fill][right]10[150lp,fill]"]
   :items [
           [ "Type" "gap 10"]
           [ (combobox :id :type
                       :model (into [] (keys types))
                       :renderer type-renderer ) "wrap"]
           [ "Mama" "split, span, gaptop 10"]
           [ :separator "growx, wrap, gaptop 10"]
           [ "Naam" "gap 10" ]
           [ (text :id :surnamemother) ""]
           [ "Voornaam" "gap 10"]
           [ (text :id :givennamemother) "wrap"]
           [ "Gyneacoloog" "gap 10"]
           [ (combobox :id :gyneacologist
                       :renderer doctor-renderer
                       :model []) "wrap"]
           [ "Baby" "split,span,gaptop 10"]
           [ :separator "growx,wrap,gaptop 10"]
           [ "Naam" "gap 10"] [ (text :id :surnamebaby) ""]
           [ "Voornaam" "gap 10"]
           [ (text :id :givennamebaby) "wrap"]
           [ "Geboortedatum" "gap 10"]
           [ (date-chooser :id :birthdate) ""]
           [ "Geboorteuur" "gap 10"]
           [ (text :id :birthhour ) "wrap"]
           [ "Kinderarts" "gap 10"]
           [ (combobox :id :pediatrician
                       :renderer doctor-renderer
                       :model [] ) ""]
           [ "Voeding" "gap 10"]
           [ (combobox :id :nutrition
                       :model ["BV" "KV/Nutrilon " "KV/Nan"]) "wrap"]

           [ "Ziekenhuis" "split,span,gaptop 10"]
           [ :separator "growx,wrap,gaptop 10"]              
           [ "Kamer" "gap 10"]
           [ (combobox :id :roomnumber :model []) "wrap"]
           [ (action :name "Opslaan"
                     :handler (partial save-handler appstate))   "span 4, split 2 , tag ok" ]
           [ (cancel-action appstate) "tag cancel"]]))

(defn filter-doctors-by-type [type coll]
  (log/debug "filtering doctors " coll "for type " type)
  (into [] (filter #(-> % :type (= type)) coll)))

(defn create-new-entry []
  {:type (first types)
   :surnamemother nil
   :givennamemother nil
   :surnamebaby nil
   :givennamebaby nil
   :birthdate (java.util.Date.)
   :birthhour nil
   :nutrition "BV"})

(defn load-existing-record [id]
  (patient/get-patient-by-id id))

(defn transform-id-to-record [id]
  (if (< id 0)
    (create-new-entry)
    (load-existing-record id)))

(defn init [appstate]
  (let [form (create-form appstate)]
    (b/bind (select form [:#type])
            (b/transform (partial contains? [0 1]))
            (b/tee 
             (b/property (select form [:#givennamebaby]) :enabled?)
             (b/property (select form [:#surnamebaby])   :enabled?)
             (b/property (select form [:#pediatrician])  :enabled?)
             (b/property (select form [:#nutrition])     :enabled?)
             (b/property (select form [:#birthhour])     :enabled?)))
    
    (b/bind (:doctors appstate)
            (b/tee
             (b/bind
              (b/transform (partial filter-doctors-by-type :gyneacologist))
              (b/transform #(into [] (map :id %)))
              (b/property (select form [:#gyneacologist]) :model))
             (b/bind
              (b/transform (partial filter-doctors-by-type :pediatrician))
              (b/transform #(into [] (map :id %)))
              (b/property (select form [:#pediatrician]) :model ))))
    
    (b/bind (:selected-record-id appstate)
            (b/filter (complement nil?))
            (b/tee
             (b/bind
              (b/transform room/find-rooms-for-patient)
              (b/notify-later)
              (b/property (select form [:#roomnumber]) :model))
             (b/bind 
              (b/transform transform-id-to-record)
              (b/notify-later)
              (b/value form))))
    form))
