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

(defn get-todo-item [id]
  (let [result (sql/query db ["SELECT * FROM todo WHERE id = ?" id])]
    (if result
      (first result)
      nil)))

(defn get-todo-items []
  (sql/query db ["SELECT * FROM todo ORDER BY added DESC"]))

(defn handle-insert-result [result]
  (when-not (empty? result)
    (first result)))

(defn add-todo-item! [{:keys [title type type-id description priority]}]
  (handle-insert-result (sql/insert! db :todo {:title title
                                               :type type
                                               :type_id type-id
                                               :description description
                                               :priority priority})))

(defn delete-todo-item! [id]
  (sql/delete! db :todo ["id = ?" id]))
