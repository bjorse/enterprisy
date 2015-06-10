(ns workorder-todo-service.todo-db
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [clj-time.coerce :as tc]
            [workorder-todo-service.config :as config]
            [workorder-todo-service.util :as util]))

(def db
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" config/database-ip ":5432/workorder_todo")
     :user "postgres"
     :password "postgres"})

(defn get-by-workorder-id-and-status [workorder-id workorder-status]
  (let [result (sql/query db ["SELECT * FROM workorder_todo WHERE workorder_id = ? AND workorder_status = ?" workorder-id workorder-status])]
    (if (empty? result)
      nil
      (first result))))

(defn handle-insert-result [result]
  (when-not (empty? result)
    (let [added-todo (first result)]
      (merge added-todo {:added (util/format-long-date (:added added-todo))}))))

(defn add-workorder-todo! [{:keys [workorder-id client-id workorder-status todo-id]}]
  (handle-insert-result (sql/insert! db :workorder_todo {:workorder_id workorder-id
                                                         :workorder_status workorder-status
                                                         :client_id client-id
                                                         :todo_id todo-id})))
