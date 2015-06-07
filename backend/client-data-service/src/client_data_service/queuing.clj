(ns client-data-service.queuing
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [client-data-service.config :as config]
            [client-data-service.util :as util]))

(def ^{:const true} default-exchange-name "")

(def queue-name "enterprisy")

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(def add-client-message-type "client.added")

(defn message-handler
  [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println
    (format "Received a message from queue: %s, delivery tag: %d, content type: %s, type: %s"
    (String. payload "UTF-8") delivery-tag content-type type)))

(defn listen! []
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)]
    (println (format "Connected to RabbitMQ. Channel id: %d" (.getChannelNumber ch)))
    (lq/declare ch queue-name {:exclusive false :auto-delete true})
    (lc/subscribe ch queue-name message-handler {:auto-ack true})))

(defn publish! [message type]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)
        message-as-json (util/convert-map-to-json message)]
    (lb/publish ch default-exchange-name queue-name message-as-json {:content-type "application/json" :type type})
    (rmq/close ch)
    (rmq/close conn)))
