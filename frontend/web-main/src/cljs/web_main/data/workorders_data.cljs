(ns web-main.data.workorders-data
  (:require [web-main.rest :refer [GET POST PUT]]
            [web-main.store :as store]))

(def base-url "/api/workorders")

(defn get-workorders [callback]
  (GET base-url {:handler callback}))

(defn get-workorders-for-client [client-id callback]
  (GET base-url {:handler callback :params {:client-id client-id}}))

(defn add-workorder [workorder callback]
  (let [updated-workorder (merge workorder {:client-id (:id @store/current-client)})]
    (POST base-url {:handler callback :params {:workorder updated-workorder}})))
