(ns event-service.event-push
  (:require [clojure.core.async :refer [<! >! put! close! go-loop]]))

(def channels (atom []))

(defn remove-channel! [channel]
  (println "Channel disconnected.")
  (reset! channels (filter #(not= channel %) @channels)))

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Channel connected.")
  (reset! channels (conj @channels ws-channel))
  (go-loop []
           (if (<! ws-channel)
             (recur)
             (remove-channel! ws-channel))))

(defn broadcast! [event-type message]
  (println (str "Broadcasting message '" event-type "': " message))
  (go-loop [channels-left @channels]
           (let [channel (first channels-left)]
             (println (str "Sending to channel: " channel))
             (>! channel {:type event-type :message message})
             (when (> (count channels-left) 1)
               (recur (rest channels-left))))))
