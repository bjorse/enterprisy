(ns workorder-process-service.queuing
  (:require [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [workorder-process-service.config :as config]
            [workorder-process-service.util :as util]))

(def ^{:const true} default-exchange-name "")

(def in-queue-name "enterprisy.data.workorders")

(def out-queue-name "enterprisy.process.workorders")

(def amqp-url (str "amqp://enterprisy:enterprisy@" config/queue-ip ":5672"))

(def workorder-new-message-type "workorder.new")

(def workorder-accepted-message-type "workorder.accepted")

(def workorder-rejected-message-type "workorder.rejected")

(def workorder-in-progress-message-type "workorder.in-progress")

(def workorder-aborted-message-type "workorder.aborted")

(def workorder-finished-message-type "workorder.finished")

(def workorder-closed-message-type "workorder.closed")

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
    (lq/declare ch in-queue-name {:exclusive false :auto-delete false})
    (lc/subscribe ch in-queue-name (wrap-message-handler message-handler) {:auto-ack true})))

(defn publish! [message type]
  (let [conn (rmq/connect {:uri amqp-url})
        ch (lch/open conn)
        message-as-json (util/convert-map-to-json message)]
    (lb/publish ch default-exchange-name out-queue-name message-as-json {:content-type "application/json" :type type})
    (rmq/close ch)
    (rmq/close conn)))
