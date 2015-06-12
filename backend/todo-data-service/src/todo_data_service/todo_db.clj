(ns todo-data-service.todo-db
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [clj-time.coerce :as tc]
            [todo-data-service.config :as config]
            [todo-data-service.util :as util]))

(def db
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" config/database-ip ":5432/todo")
     :user "postgres"
     :password "postgres"})

(defn get-todo-items []
  (sql/query db ["SELECT * FROM todo ORDER BY added DESC"]))

(defn handle-insert-result [result]
  (when-not (empty? result)
    (let [added-todo-item (first result)]
      (merge added-todo-item {:added (util/format-short-date (:added added-todo-item))}))))

(defn add-todo-item! [{:keys [title type type-id priority]}]
  (handle-insert-result (sql/insert! db :todo {:title title
                                               :type type
                                               :type_id type-id
                                               :priority priority})))

(defn delete-todo-item! [id]
  (sql/delete! db :todo ["id = ?" id]))
