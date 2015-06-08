(ns workorder-data-service.queuing
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [workorder-data-service.config :as config]
            [workorder-data-service.util :as util]))

(def ^{:const true} default-exchange-name "")

(def queue-name "enterprisy")

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(def add-workorder-message-type "workorder.added")

(def update-workorder-message-type "workorder.updated")

(defn publish! [message type]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)
        message-as-json (util/convert-map-to-json message)]
    (lb/publish ch default-exchange-name queue-name message-as-json {:content-type "application/json" :type type})
    (rmq/close ch)
    (rmq/close conn)))
