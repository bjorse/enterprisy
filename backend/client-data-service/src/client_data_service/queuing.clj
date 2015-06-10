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

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(def add-client-message-type "client.added")

(defn publish! [message type]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)
        message-as-json (util/convert-map-to-json message)]
    (lb/publish ch default-exchange-name queue-name message-as-json {:content-type "application/json" :type type})
    (rmq/close ch)
    (rmq/close conn)))
