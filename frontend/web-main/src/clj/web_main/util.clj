(ns web-main.util
  (:require [clojure.data.json :as json]))

(defn convert-json-to-map [content]
  (json/read-str content :key-fn keyword))

(defn convert-map-to-json [content]
  (json/write-str content :key-fn name))

(defn extract-body [result]
  (convert-json-to-map (:body result)))

(defn remove-nil-values [map-to-clean]
  (into {} (remove #(nil? (val %)) map-to-clean)))
