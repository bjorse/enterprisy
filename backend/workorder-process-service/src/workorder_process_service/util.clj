(ns workorder-process-service.util
  (:require [clojure.data.json :as json]
            [clj-time.coerce :as tc]))

(defn convert-json-to-map [content]
  (json/read-str content :key-fn keyword))

(defn convert-map-to-json [content]
  (json/write-str content :key-fn name))

(defn format-long-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))

(defn convert-to-number [value]
  (if (number? value)
    value
    (Integer/parseInt value)))
