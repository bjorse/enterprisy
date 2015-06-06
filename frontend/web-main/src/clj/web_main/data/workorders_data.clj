(ns web-main.data.workorders-data
  (:require [web-main.static-data :as static-data]))

(def data (atom static-data/workorders-data))

(defn filter-workorders-by-client-id [client-id source]
  (let [id (Integer/parseInt client-id)]
    (filter #(= (:client-id %) id) source)))

(defn filter-workorders [params]
  (if (contains? params :client-id)
    (filter-workorders-by-client-id (:client-id params) @data)
    @data))
