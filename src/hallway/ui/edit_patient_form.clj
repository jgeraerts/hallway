(ns hallway.ui.edit-patient-form
  (:use [seesaw
         [core :exclude (separator)]
         mig]
        hallway.ui.datechooser)
  (:require [seesaw.bind :as b]))

(def types
  [{:id 0 :name "Vaginale Geboorte"}
   {:id 1 :name "Sectio"}
   {:id 2 :name "Observatie"}
   {:id 3 :name "Gyneacologische patient"}])

(defn- type-renderer [renderer {:keys [value]}]
  (apply config! renderer [:text (:name value)])
  )

(defn- create-form [appstate]
  (mig-panel
   :constraints ["inset 10", "[right][150lp,fill][right][150lp,fill]"]
   :items [
           [ "Type" "gap 10"]
           [ (combobox :id :type
                       :model types
                       :renderer type-renderer ) "wrap"]
           [ "Mama" "split, span, gaptop 10"]
           [ :separator "growx, wrap, gaptop 10"]
           [ "Naam" "gap 10" ]
           [ (text :id :surnamemother) ""]
           [ "Voornaam" "gap 10"]
           [ (text :id :givennamemother) "wrap"]
           [ "Gyneacoloog" "gap 10"]
           [ (combobox :id :gyneacologist
                       :model []) "wrap"]
           [ "Baby" "split,span,gaptop 10"]
           [ :separator "growx,wrap,gaptop 10"]
           [ "Naam" "gap 10"] [ (text :id :surnamebaby) ""]
           [ "Voornaam" "gap 10"]
           [ (text :id :givennamebaby) "wrap"]
           [ "Geboortedatum" "gap 10"]
           [ (date-chooser :id :birthdate) ""]
           [ "Geboorteuur" "gap 10"]
           [ (text :id :birthhour  :text "hh:mm") "wrap"]
           [ "Kinderarts" "gap 10"]
           [ (combobox :id :pediatrician
                       :model [] ) ""]
           [ "Voeding" "gap 10"]
           [ (combobox :id :nutrition
                       :model ["BV" "KV/Nutrilon " "KV/Nan"]) "wrap"]

           [ "Ziekenhuis" "split,span,gaptop 10"]
           [ :separator "growx,wrap,gaptop 10"]              
           [ "Kamer" "gap 10"]
           [ (combobox :id :roomnumber :model []) "wrap"]
           [ (action :name "Opslaan")   "span 4, split 2 , tag ok" ]
           [ (action :name "Annuleren") "tag cancel"]]))

(defn init [appstate]
  (let [form (create-form appstate)]
    (b/bind (select form [:#type])
            (b/transform :id)
            (b/transform (partial contains? [0 1]))
            (b/tee 
             (b/property (select form [:#givennamebaby]) :enabled?)
             (b/property (select form [:#surnamebaby])   :enabled?)
             (b/property (select form [:#pediatrician])  :enabled?)
             (b/property (select form [:#nutrition])     :enabled?)
             (b/property (select form [:#birthhour])     :enabled?)))
    (listen (select form [:#birthhour]) :focus-gained
            (fn [e] (config! (to-widget e) :text "" )))
    form))
