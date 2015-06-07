(ns client-data-service.clients
  (:require [clojure.string :as string]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-format]
            [client-data-service.clients-db :as db]
            [client-data-service.queuing :as queuing]
            [client-data-service.util :as util]))

(def allowed-gender-values ["Male" "Female"])

(def date-formatter (time-format/formatter "yyyy-MM-dd"))

(def datetime-formatter (time-format/formatter "yyyy-MM-dd HH:mm"))

(def email-format #"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$")

(def date-format #"^(19|20)\d\d([- /.])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])$")

(defn get-text-for-search [{:keys [firstname lastname email birthdate]}]
  (str firstname " " lastname " " email " " birthdate))

(defn list-contains? [value coll]
  (= true (some #(= value %) coll)))

(defn validate-email [email]
  (if (string/blank? email)
    true
    (re-matches email-format email)))

(defn validate-date-format [date]
  (re-matches date-format date))

(defn validate-date [date]
  (if (or (not (validate-date-format date)) (string/blank? date))
    true
    (do (let [parsed-date (time-format/parse date)
              now (time/now)
              earliest-date (time/minus now (time/years 100))]
          (time/within? (time/interval earliest-date now) parsed-date)))))

(defn validate-client [{:keys [firstname lastname email birthdate gender]}]
  (let [errors (list
                (when (string/blank? firstname) {:key "firstname" :text "First name cannot be empty!"})
                (when (string/blank? lastname) {:key "lastname" :text "Last name cannot be empty!"})
                (when (string/blank? email) {:key "email" :text "E-mail adress cannot be empty!"})
                (when (not (validate-email email)) {:key "email" :text "E-mail adress format is not valid!"})
                (when (not (validate-date-format birthdate)) {:key "birthdate" :text "Birth date format is not valid (expected 'YYYY-MM-DD')."})
                (when (not (validate-date birthdate)) {:key "birthdate" :text "Birth date cannot be later than today or earlier than 100 years."})
                (when (string/blank? gender) {:key "gender" :text "Gender cannot be empty!"})
                (when (not (list-contains? gender allowed-gender-values)) {:key "gender" :text "Gender is not a valid value!"}))]
    (filter #(not (= nil %)) errors)))

(defn format-client [client]
  (merge client {:added (util/format-short-date (:added client))
                 :birthdate (util/format-short-date (:birthdate client))}))

(defn filter-clients [query]
  (let [result (map #(format-client %) (db/filter-clients query))
        limited (= (count result) db/max-limit)]
    {:clients result :limited limited}))

(defn get-client [id]
  (let [result (db/get-client id)]
    (if (empty? result)
      {:status 404 :body {:id id}}
      (format-client (first result)))))

(defn add-client! [client]
  (let [validation-errors (validate-client client)]
    (if (empty? validation-errors)
      (do
        (let [fixed-client (merge client {:birthdate (time-coerce/to-sql-date (time-format/parse (:birthdate client)))})]
          (if-let [result (db/add-client! fixed-client)]
            (do (println (str "Added client: " result))
                (queuing/publish! result queuing/add-client-message-type)
                {:status 200 :body result}))
          {:status 500}))
      {:status 422 :body {:errors validation-errors}})))
