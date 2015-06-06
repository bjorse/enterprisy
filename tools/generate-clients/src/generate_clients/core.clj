(ns generate-clients.core
  (:require [clojure.string :as string]))

(def genders ["Male" "Female"])

(def insert-statement "INSERT INTO clients (gender,firstname,lastname,email,birthdate) VALUES ")

(defn read-file [filename]
  (string/split (slurp filename) #"\s+"))

(defn read-and-capitalize-content [filename]
  (map #(string/capitalize %) (read-file filename)))

(defn generate-email [firstname lastname email-provides]
  (let [email (rand-nth email-provides)]
    (str (string/lower-case firstname) "." (string/lower-case lastname) "@" email)))

(defn pad [x]
  (format "%02d" x))

(defn generate-random-date []
  (let [year (+ (rand-int 58) 1940)
        month (pad (inc (rand-int 12)))
        day (pad (inc (rand-int 28)))]
    (str year "-" month "-" day)))

(defn create-sql-row [{:keys [gender firstname lastname email birthdate]}]
  (str "('" gender "','" firstname "','" lastname "','" email "','" birthdate "')"))

(defn create-sql-statement [data]
  (let [values-statement (string/join ", " (seq (map #(create-sql-row %) data)))]
    (str insert-statement values-statement ";")))

(defn create-random-data [count]
  (let [female-names (read-and-capitalize-content "data/first_names_female.txt")
        male-names (read-and-capitalize-content "data/first_names_male.txt")
        surnames (read-and-capitalize-content "data/surnames.txt")
        email-provides (read-file "data/email_providers.txt")]
    (loop [x count
           data []]
      (if-not (pos? x)
        data
        (let [gender (rand-nth genders)
              firstname (if (= "Female" gender) (rand-nth female-names) (rand-nth male-names))
              lastname (rand-nth surnames)
              email (generate-email firstname lastname email-provides)
              birthdate (generate-random-date)]
          (recur (dec x) (conj data {:firstname firstname
                                     :lastname lastname
                                     :gender gender
                                     :email email
                                     :birthdate birthdate})))))))

(defn -main [& args]
  (let [data (create-random-data 500)]
    (println (create-sql-statement data))))
