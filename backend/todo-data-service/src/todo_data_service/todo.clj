(ns todo-data-service.todo
  (:require [clojure.string :as string]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-format]
            [todo-data-service.todo-db :as db]
            [todo-data-service.queuing :as queuing]
            [todo-data-service.util :as util]))

(def number-format #"^[1-9]\d*$")

(def prority-format #"^[1-5]$")

(defn numeric-and-positive? [value]
  (if (nil? value)
    false
    (re-matches number-format value)))

(defn valid-priority? [value]
  (re-matches prority-format value))

(defn validate-todo-item [{:keys [title type type-id priority]}]
  (let [errors (list (when (string/blank? title) {:key "title" :text "Title cannot be empty!"})
                     (when (string/blank? type) {:key "type" :text "Type cannot be empty!"})
                     (when-not (numeric-and-positive? (str type-id)) {:key "type-id" :text "Type-id must be a numeric value and positive!"})
                     (when-not (valid-priority? (str priority)) {:key "priority" :text "Priority must be numeric value in range 1-5!"}))]
    (filter #(not (= nil %)) errors)))

(defn format-todo-item [{:keys [id title type type_id priority added]}]
  {:id id
   :title title
   :type type
   :type-id (util/convert-to-number type_id)
   :priority (util/convert-to-number priority)
   :added added})

(defn get-todo-items []
  (map #(format-todo-item %) (db/get-todo-items)))

(defn add-todo-item! [todo-item]
  (println (str "Trying to add this todo item: " todo-item))
  (let [validation-errors (validate-todo-item todo-item)]
    (if (empty? validation-errors)
      (if-let [result (format-todo-item (db/add-todo-item! todo-item))]
        (do (println (str "Added todo item: " result))
            (queuing/publish! result queuing/add-todo-item-message-type)
            {:status 200 :body result})
        {:status 500})
      {:status 422 :body {:errors validation-errors}})))
