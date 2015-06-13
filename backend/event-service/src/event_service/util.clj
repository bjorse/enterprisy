(ns event-service.util
  (:require [clojure.data.json :as json]))

(defn convert-json-to-map [content]
  (json/read-str content :key-fn keyword))
