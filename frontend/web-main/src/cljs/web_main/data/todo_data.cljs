(ns web-main.data.todo-data
  (:require [web-main.rest :refer [GET]]))

(def base-url "/api/todo-items")

(defn get-todo-items [callback]
  (GET base-url {:handler callback}))
