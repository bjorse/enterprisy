(ns client-data-service.util
  (:require [clojure.data.json :as json]
            [clj-time.coerce :as tc]))

(defn convert-json-to-map [content]
  (json/read-str content :key-fn keyword))

(defn convert-map-to-json [content]
  (json/write-str content :key-fn name))

(defn format-short-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") date))
