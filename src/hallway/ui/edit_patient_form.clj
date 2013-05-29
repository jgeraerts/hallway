(ns hallway.ui.edit-patient-form
  (:use [seesaw
         [core :exclude (separator)]
         mig]
        hallway.ui.datechooser
        hallway.ui.actions
        [hallway.ui.util :only [run-in-background
                                load-data-in-table
                                goto-previous-view]])
  (:require [seesaw.bind :as b]
            [clojure.tools.logging :as log]
            [hallway.model.patients :as patient]
            [hallway.model.doctors  :as doctor]
            [hallway.model.rooms :as room]
            [hallway.ui.patienttable :as patienttable]))

(def types
  {0  "Vaginale Geboorte"
   1  "Sectio"
   2  "Observatie"
   3  "Gyneacologische patient"})

(defn create-new-entry []
  {:type (first types)
   :surnamemother ""
   :givennamemother ""
   :surnamebaby ""
   :givennamebaby ""
   :birthdate (java.util.Date.)
   :birthhour ""
   :nutrition "BV"
   :nutritionamount ""
   :nutritionpercent ""
   :nutritionadditives ""
   :pediatricianfirsttime false
   :pediatricianhome      false})

(defn load-existing-record [id]
  (patient/get-patient-by-id id))

(defn transform-id-to-record [id]
  (if (< id 0)
    (create-new-entry)
    (load-existing-record id)))

(defn- set-checkbox! [form record key]
  (let [value (key record)
        selector (keyword (str "#" (subs (str key) 1)))
        cb    (select form [selector])]
    (config! cb :selected? value)))

(defn- fill-form-for-record [form id]
  (run-in-background
   (log/debug "fill form for id " id)
   (let [rooms (room/find-rooms-for-patient id)
         roomnumberdropdown (select form [:#roomnumber])
         record (transform-id-to-record id)]
     (invoke-later 
      (config! roomnumberdropdown :model rooms)
      (value! form record)
      (set-checkbox! form record :pediatricianfirsttime)
      (set-checkbox! form record :pediatricianhome)))))

(defn- type-renderer [renderer {:keys [value]}]
  (apply config! renderer [:text (types value)]))

(defn- doctor-renderer [appstate renderer {:keys [value]}]
  (let [doctor (first (filter #(-> %1 :id (= value)) @(:doctors appstate)))]
    (apply config! renderer [:text (str (:initials doctor) " - " (:name doctor))])))

(defn- save-handler [appstate e]
  (run-in-background 
   (let [formvalue (value (select (to-root e) [:#editpatientform]))]
     (patient/save-patient
      @(:selected-record-id appstate)
      formvalue)
     (patienttable/refresh-patienttable (to-root e))
     (goto-previous-view appstate))))


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
           [ (text :id :surnamemother :columns 255) ""]
           [ "Voornaam" "gap 10"]
           [ (text :id :givennamemother :columns 255) "wrap"]
           [ "Gyneacoloog" "gap 10"]
           [ (combobox :id :gyneacologist
                       :renderer (partial  doctor-renderer appstate)
                       :model []) "wrap"]
           [ "Baby" "split,span,gaptop 10"]
           [ :separator "growx,wrap,gaptop 10"]
           [ "Naam" "gap 10"] [ (text :id :surnamebaby :columns 255 ) ""]
           [ "Voornaam" "gap 10"]
           [ (text :id :givennamebaby :columns 255) "wrap"]
           [ "Geboortedatum" "gap 10"]
           [ (date-chooser :id :birthdate) ""]
           [ "Geboorteuur" "gap 10"]
           [ (text :id :birthhour ) "wrap"]
           [ "Kinderarts" "gap 10"]
           [ (combobox :id :pediatrician
                       :renderer (partial doctor-renderer appstate)
                       :model [] ) ""]
           [ (checkbox :id :pediatricianfirsttime :text "1x gezien") "skip,wrap"]
           [ (checkbox :id :pediatricianhome      :text "Naar huis") "skip 3,wrap"]
           [ "Voeding" "split,span,gaptop 10"]
           [ :separator "growx,wrap,gaptop 10"]
           [ "Voeding" "gap 10"]
           [ (combobox :id :nutrition
                       :model ["BV" "KV/Nutrilon " "KV/Nan"]) ""]
           [ "Hoeveelheid" ""]
           [ (text     :id :nutritionamount :columns 255)     "wrap"]
           [ "Toevoeging"  ""]
           [ (text     :id :nutritionadditives :columns 255) ""]
           [ "Percentage"  ""]
           [ (text     :id :nutritionpercent   :columns 255) "wrap"]
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
            (b/b-do [v] (fill-form-for-record form v)))
    form))
