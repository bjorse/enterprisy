(ns web-main.data.todo-data
  (:require [clj-http.client :as client]
            [web-main.util :as util]))

(def todo-data-service-url "http://localhost:3030/todo-items")

(defn get-todo-items []
  (let [result (client/get todo-data-service-url)]
    (util/extract-body (client/get todo-data-service-url))))
