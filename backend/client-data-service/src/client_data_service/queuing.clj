(ns client-data-service.queuing
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [client-data-service.config :as config]
            [client-data-service.util :as util]))

(def ^{:const true} default-exchange-name "")

(def queue-name "enterprisy.data.clients")

(def event-queue-name "enterprisy.events")

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(def add-event-message-type "event.added")

(def add-client-message-type "client.added")

(defn- publish-on-queue! [queue type message]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)
        message-as-json (util/convert-map-to-json message)]
    (lb/publish ch default-exchange-name queue message-as-json {:content-type "application/json" :type type})
    (rmq/close ch)
    (rmq/close conn)))

(defn publish! [message type]
  (publish-on-queue! queue-name type message))

(defn publish-event! [event-message]
  (publish-on-queue! event-queue-name add-event-message-type {:text event-message}))
