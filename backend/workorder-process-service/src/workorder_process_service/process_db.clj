(ns workorder-process-service.process-db
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [clj-time.coerce :as tc]
            [workorder-process-service.config :as config]
            [workorder-process-service.util :as util]))

(def db
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" config/database-ip ":5432/workorder_process")
     :user "postgres"
     :password "postgres"})

(defn get-by-workorder-id-and-status [workorder-id status]
  (let [result (sql/query db ["SELECT * FROM workorder_process WHERE workorder_id = ? AND status = ?" workorder-id status])]
    (if (empty? result)
      nil
      (first result))))

(defn handle-insert-result [result]
  (when-not (empty? result)
    (let [added-process (first result)]
      (merge added-process {:added (util/format-long-date (:added added-process))}))))

(defn add-process-status! [{:keys [workorder-id client-id status]}]
  (println (str "Data to add: id: " workorder-id ", client-id: " client-id", status: " status))
  (handle-insert-result (sql/insert! db :workorder_process {:workorder_id workorder-id
                                                            :client_id client-id
                                                            :status status})))
