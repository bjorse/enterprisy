(ns workorder-todo-service.queuing
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [workorder-todo-service.config :as config]
            [workorder-todo-service.util :as util]))

(def ^{:const true} default-exchange-name "")

(def queue-name "enterprisy.process.workorders")

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(defn wrap-message-handler [message-handler]
  (fn [ch {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
    (let [message-as-string (String. payload "UTF-8")
          message-as-map (util/convert-json-to-map message-as-string)]
      (println
        (format "Received a message from queue: %s, delivery tag: %d, content type: %s, type: %s"
        (String. payload "UTF-8") delivery-tag content-type type))
      (message-handler type message-as-map))))

(defn listen! [message-handler]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)]
    (println (format "Connected to RabbitMQ. Channel id: %d" (.getChannelNumber ch)))
    (lq/declare ch queue-name {:exclusive false :auto-delete false})
    (lc/subscribe ch queue-name (wrap-message-handler message-handler) {:auto-ack true})))
