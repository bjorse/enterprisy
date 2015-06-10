(ns todo-data-service.queuing
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [todo-data-service.config :as config]
            [todo-data-service.util :as util]))

(def ^{:const true} default-exchange-name "")

(def queue-name "enterprisy.data")

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(def add-todo-item-message-type "todo.added")

(def remove-todo-item-message-type "todo.removed")

(defn publish! [message type]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)
        message-as-json (util/convert-map-to-json message)]
    (lb/publish ch default-exchange-name queue-name message-as-json {:content-type "application/json" :type type})
    (rmq/close ch)
    (rmq/close conn)))
