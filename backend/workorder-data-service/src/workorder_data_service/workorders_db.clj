(ns workorder-data-service.workorders-db
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [clj-time.coerce :as tc]
            [workorder-data-service.config :as config]
            [workorder-data-service.util :as util]))

(def db
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" config/database-ip ":5432/workorders")
     :user "postgres"
     :password "postgres"})

(defn get-workorder [id]
  (sql/query db ["SELECT * FROM workorders WHERE id = ?" id]))

(defn get-workorders-by-client-id [client-id]
  (sql/query db ["SELECT * FROM workorders WHERE client_id = ?" client-id]))

(defn handle-insert-result [result]
  (when-not (empty? result)
    (let [added-workorder (first result)]
      (merge added-workorder {:added (util/format-long-date (:added added-workorder))
                              :changed (util/format-long-date (:changed added-workorder))}))))

(defn add-workorder! [{:keys [title description status estimated-time client-id]}]
  (handle-insert-result (sql/insert! db :workorders {:title title
                                        :description description
                                        :status status
                                        :estimated_time estimated-time
                                        :client_id client-id})))

(defn update-workorder! [{:keys [id title description status estimated-time]}]
  (sql/update! db :workorders {:title title
                               :description description
                               :status status
                               :estimated_time estimated-time}))
