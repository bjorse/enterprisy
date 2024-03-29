(ns workorder-data-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [workorder-data-service.workorders :as workorders-data]))

(defn handle-workorders-request [client-id]
  (if client-id
    (workorders-data/get-workorders-by-client-id (Integer/parseInt client-id))
    (workorders-data/get-workorders)))

(defroutes app-routes
  (GET "/workorders" [client-id] {:body (handle-workorders-request client-id)})
  (GET "/workorders/:id" [id] {:body (workorders-data/get-workorder (Integer/parseInt id))})
  (POST "/workorders" {body :body} (workorders-data/add-workorder! (:workorder body)))
  (PUT "/workorders" {body :body} (workorders-data/update-workorder! (:workorder body)))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)))
