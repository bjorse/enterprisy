(ns client-data-service.clients-db
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]))

(def db
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname "//172.16.194.135:5432/clients"
     :user "postgres"
     :password "postgres"})

(defn filter-clients [query]
  (if (string/blank? query)
    (sql/query db ["SELECT * FROM clients"])
    (do (let [wildcard-query (str "%" query "%")]
          (sql/query db ["SELECT * FROM clients WHERE firstname ILIKE ? OR lastname ILIKE ? OR email ILIKE ?" wildcard-query wildcard-query wildcard-query])))))

(defn get-client [id]
  (sql/query db ["SELECT * FROM clients WHERE id = ?" id]))

(defn add-client! [{:keys [firstname lastname email gender birthdate]}]
  (sql/insert! db :clients {:firstname firstname
                            :lastname lastname
                            :email email
                            :gender gender
                            :birthdate birthdate}))
