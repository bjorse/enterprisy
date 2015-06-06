(ns web-main.data.clients-data
  (:require [web-main.rest :refer [GET POST PUT]]))

(def base-url "/api/clients")

(defn get-clients [callback query]
  (GET base-url {:handler callback :params {:query query}}))

(defn get-client [id callback]
  (let [url (str base-url "/" id)]
    (GET url {:handler callback})))

(defn add-client [client callback]
  (POST base-url {:handler callback :params {:client client}}))

(defn update-client [client]
  (PUT base-url {:params {:client client}}))

(defn delete-client [id]
  (PUT base-url {:params {:client {:id id :active false}}}))
