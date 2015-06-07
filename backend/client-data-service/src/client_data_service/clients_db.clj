(ns client-data-service.clients-db
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [clj-time.coerce :as tc]
            [client-data-service.config :as config]
            [client-data-service.util :as util]))

(def db
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" config/database-ip ":5432/clients")
     :user "postgres"
     :password "postgres"})

(def max-limit 500)

(defn filter-clients [query]
  (if (string/blank? query)
    (sql/query db [(str "SELECT * FROM clients LIMIT " max-limit)])
    (do (let [wildcard-query (str "%" query "%")]
          (sql/query db [(str "SELECT * FROM clients WHERE firstname ILIKE ? OR lastname ILIKE ? OR email ILIKE ? LIMIT " max-limit) wildcard-query wildcard-query wildcard-query])))))

(defn get-client [id]
  (sql/query db ["SELECT * FROM clients WHERE id = ?" id]))

(defn handle-insert-result [result]
  (when-not (empty? result)
    (let [added-client (first result)]
      (merge added-client {:added (util/format-short-date (:added added-client))
                           :birthdate (util/format-short-date (:birthdate added-client))}))))

(defn add-client! [{:keys [firstname lastname email gender birthdate]}]
  (handle-insert-result (sql/insert! db :clients {:firstname firstname
                                                  :lastname lastname
                                                  :email email
                                                  :gender gender
                                                  :birthdate birthdate})))
