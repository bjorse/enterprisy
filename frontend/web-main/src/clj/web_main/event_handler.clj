(ns web-main.event-handler)

(defn handle-queue-event [type message]
  (println (str "Handling type: " type ", message: " message)))
