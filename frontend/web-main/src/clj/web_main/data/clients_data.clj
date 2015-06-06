(ns web-main.data.clients-data
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [clj-time.core :as time]
            [clj-time.format :as time-format]))

(def client-data-service-url "http://localhost:3010/clients")

(defn convert-json-to-map [content]
  (json/read-str content :key-fn keyword))

(defn convert-map-to-json [content]
  (json/write-str content :key-fn name))

(defn extract-body [result]
  (:body result))

(defn calculate-age [birthdate]
  (let [parsed-birthdate (time-format/parse birthdate)
        interval (time/interval parsed-birthdate (time/now))]
    (time/in-years interval)))

(defn extract-data [{:keys [id firstname lastname email birthdate gender added active]}]
  {:id id
   :firstname firstname
   :lastname lastname
   :email email
   :birthdate birthdate
   :gender gender
   :added added
   :active active
   :age (calculate-age birthdate)})

(defn filter-clients [query]
  (let [clients (extract-body (client/get client-data-service-url {:query-params {:query query} :accept :json}))]
    (map #(extract-data %) (convert-json-to-map clients))))

(defn get-client [id]
  (let [client (extract-body (client/get (str client-data-service-url "/" id)))]
    (extract-data (convert-json-to-map client))))

(defn add-client! [client]
  (let [result (client/post client-data-service-url {:body (convert-map-to-json {:client client})
                                                     :content-type :json
                                                     :accept :json
                                                     :throw-exceptions false})]
    {:status (:status result) :body (convert-json-to-map (extract-body result))}))
