(ns event-service.handler
  (:use [org.httpkit.server :only [run-server]])
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources]]
            [ring.middleware.reload :as reload]
            [chord.http-kit :refer [wrap-websocket-handler]]
            [event-service.event-push :as event-push]))

(defn in-dev? []
  true)

(defroutes app-routes
  (GET "/events" [] (-> event-push/ws-handler
                        (wrap-websocket-handler {:format :transit-json}))))

(defn init []
  (let [handler (if (in-dev?)
                  (reload/wrap-reload (site #'app-routes))
                  (site app-routes))]
    (run-server handler {:port 3100})))
