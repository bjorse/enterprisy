(ns todo-data-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [todo-data-service.todo :as todo-data]))

(defroutes app-routes
  (GET "/todo-items" [] {:body (todo-data/get-todo-items)})
  (POST "/todo-items" {body :body} (todo-data/add-todo-item! (:todo-item body)))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)))
