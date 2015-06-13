(ns event-service.core
  (:require [event-service.queuing :as queuing]
            [event-service.handler :as handler]
            [event-service.event-push :as event-push]))

(defn handle-queue-message [type message]
  (println (str "This is how the '" type "' message looks like: " message))
  (event-push/broadcast! type message))

(defn -main [& args]
  (queuing/listen! handle-queue-message)
  (handler/init))
