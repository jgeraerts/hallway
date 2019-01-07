(ns hallway.ui.datechooser
  (:use [seesaw core [value :as v] [options :only [ apply-options]]])
 (:import com.toedter.calendar.JDateChooser))


(extend-protocol
    v/Value
  JDateChooser
  (container?* [this] false)
  (value* [this] ( .getDate this))
  (value!* [this value] (.setDate this value)))

(defn date-chooser [& args]
  (apply-options (construct JDateChooser) args))
