(ns event-service.core
  (:require [clj-time.core :as time]
            [clj-time.local :as local-time]
            [clj-time.format :as time-format]
            [event-service.queuing :as queuing]
            [event-service.handler :as handler]
            [event-service.event-push :as event-push]
            [event-service.util :as util]))

(def datetime-formatter (time-format/formatter "yyyy-MM-dd HH:mm"))

(defn get-formatted-current-time []
  (time-format/unparse datetime-formatter (local-time/local-now)))

(defn decorate-queue-message [type message]
  (case type
    "event.added" (merge message {:event-time (get-formatted-current-time)})
    message))

(defn handle-queue-message [type message]
  (println (str "This is how the '" type "' message looks like: " message))
  (event-push/broadcast! type (decorate-queue-message type message)))

(defn -main [& args]
  (queuing/listen! handle-queue-message)
  (handler/init))
