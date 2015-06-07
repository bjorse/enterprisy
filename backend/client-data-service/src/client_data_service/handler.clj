(ns client-data-service.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [client-data-service.clients :as clients-data]
            [client-data-service.queuing :as queuing]))

(defroutes app-routes
  (GET "/clients" [query] {:body (clients-data/filter-clients query)})
  (GET "/clients/:id" [id] {:body (clients-data/get-client (Integer/parseInt id))})
  (POST "/clients" {body :body} (clients-data/add-client! (:client body)))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      (middleware/wrap-json-response)))

(defn init []
  (queuing/listen!))
