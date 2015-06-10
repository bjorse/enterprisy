(ns workorder-todo-service.util
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-time.coerce :as tc]))

(defn convert-json-to-map [content]
  (json/read-str content :key-fn #(keyword (string/replace % "_" "-"))))

(defn convert-map-to-json [content]
  (json/write-str content :key-fn name))

(defn extract-body [result]
  (convert-json-to-map (:body result)))

(defn format-long-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))

(defn convert-to-number [value]
  (if (number? value)
    value
    (Integer/parseInt value)))
