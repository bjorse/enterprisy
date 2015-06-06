(ns web-main.data.workorders-data
  (:require [web-main.rest :refer [GET POST PUT]]))

(def base-url "/api/workorders")

(defn get-workorders [callback]
  (GET base-url {:handler callback}))

(defn get-workorders-for-client [client-id callback]
  (GET base-url {:handler callback :params {:client-id client-id}}))
