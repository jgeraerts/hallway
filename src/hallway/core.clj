(ns hallway.core
  (:gen-class)
  (:require [hallway db]
            [hallway.ui main-frame]))


(defn -main [& args]
  (hallway.db/backup)
  (hallway.db/create)
  (hallway.ui.main-frame/init false))

