(ns web-main.data.todo-data
  (:require [clj-http.client :as client]
            [web-main.config :as config]
            [web-main.util :as util]))

(defn get-todo-items []
  (let [result (client/get config/todo-data-service-url)]
    (util/extract-body result)))
