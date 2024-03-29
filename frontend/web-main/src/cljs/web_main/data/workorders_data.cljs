(ns web-main.data.workorders-data
  (:require [web-main.rest :refer [GET POST PUT]]
            [web-main.store :as store]))

(def base-url "/api/workorders")

(defn get-workorder [workorder-id callback]
  (GET (str base-url "/" workorder-id) {:handler callback}))

(defn get-workorders [callback]
  (GET base-url {:handler callback}))

(defn get-workorders-for-client [client-id callback]
  (GET base-url {:handler callback :params {:client-id client-id}}))

(defn add-workorder [workorder callback]
  (let [updated-workorder (merge workorder {:client-id (:id @store/current-client)})]
    (POST base-url {:handler callback :params {:workorder updated-workorder}})))

(defn update-workorder-callback [response]
  (when-not (contains? :error response)
    (when (= (:id @store/current-workorder) (:id response))
      (reset! store/current-workorder (merge @store/current-workorder response)))))

(defn wrap-update-workorder-callback [callback]
  (fn [response]
    (callback response)
    (update-workorder-callback response)))

(defn update-workorder [{:keys [id status actual-time callback]}]
  (let [data-to-post {:id id
                      :status status
                      :actual-time actual-time}
        handler (if (nil? callback) update-workorder-callback (wrap-update-workorder-callback callback))]
    (PUT base-url {:handler handler :params {:workorder data-to-post}})))

(defn approve-workorder [id & [callback]]
  (update-workorder {:id id :status "approved" :callback callback}))

(defn reject-workorder [id & [callback]]
  (update-workorder {:id id :status "rejected" :callback callback}))

(defn start-workorder [id & [callback]]
  (update-workorder {:id id :status "in-progress" :callback callback}))

(defn finish-workorder [{:keys [id actual-time callback]}]
  (update-workorder {:id id :status "finished" :actual-time actual-time :callback callback}))

(defn abort-workorder [id & [callback]]
  (update-workorder {:id id :status "aborted" :callback callback}))

(defn close-workorder [id & [callback]]
  (update-workorder {:id id :status "closed" :callback callback}))
