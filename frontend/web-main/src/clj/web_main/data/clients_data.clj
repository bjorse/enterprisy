(ns web-main.data.clients-data
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [web-main.config :as config]
            [web-main.util :as util]))

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
  (let [result (util/extract-body (client/get config/client-data-service-url {:query-params {:query query} :accept :json}))
        clients (map #(extract-data %) (:clients result))]
    {:clients clients :limited (:limited result)}))

(defn get-client [id]
  (let [client (util/extract-body (client/get (str config/client-data-service-url "/" id)))]
    (extract-data client)))

(defn add-client! [client]
  (let [result (client/post config/client-data-service-url {:body (util/convert-map-to-json {:client client})
                                                            :content-type :json
                                                            :accept :json
                                                            :throw-exceptions false})]
    {:status (:status result) :body (util/extract-body result)}))
