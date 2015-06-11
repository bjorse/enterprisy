(ns web-main.data.workorders-data
  (:require [web-main.rest :refer [GET POST PUT]]
            [web-main.store :as store]))

(def base-url "/api/workorders")

(defn get-workorder [workorder-id callback]
  (GET (str base-url "/" workorder-id) {:handler callback}))

(defn get-workorders [callback]
  (GET base-url {:handler callback}))

(defn get-workorders-for-client [client-id callback]
  (.log js/console (str "Client id when fetching workorders: " client-id))
  (GET base-url {:handler callback :params {:client-id client-id}}))

(defn add-workorder [workorder callback]
  (let [updated-workorder (merge workorder {:client-id (:id @store/current-client)})]
    (POST base-url {:handler callback :params {:workorder updated-workorder}})))

(defn update-workorder [id status & [actual-time]]
  (let [data-to-post {:id id
                      :status status
                      :actual-time actual-time}]
    (.log js/console (str "Putting to web server: " data-to-post))
    (PUT base-url {:params {:workorder data-to-post}})))

(defn approve-workorder [id]
  (update-workorder id "approved"))

(defn reject-workorder [id]
  (update-workorder id "rejected"))

(defn start-workorder [id]
  (update-workorder id "in-progress"))

(defn finish-workorder [id actual-time]
  (update-workorder id "finished" actual-time))

(defn abort-workorder [id]
  (update-workorder id "aborted"))

(defn close-workorder [id]
  (update-workorder id "closed"))
