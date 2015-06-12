(ns web-main.server
  (:require [web-main.handler :refer [app]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [web-main.queuing :as queuing]
            [web-main.event-handler :as event-handler])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3000"))]
     (queuing/listen! event-handler/handle-queue-event)
     (run-jetty app {:port port :join? false})))
