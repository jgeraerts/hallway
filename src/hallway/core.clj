(ns hallway.core
  (:gen-class)
  (:require [hallway db gui]))


(defn -main [& args]
    (hallway.db/create)
    (hallway.gui/build-gui false))

