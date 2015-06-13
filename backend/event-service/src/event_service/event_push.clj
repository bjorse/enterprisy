(ns event-service.event-push
  (:require [clojure.core.async :refer [<! >! put! close! go-loop]]))

(def channels (atom []))

(defn ws-handler [{:keys [ws-channel] :as req}]
  (println "Opened connection from" (:remote-addr req))
  (reset! channels (conj @channels ws-channel)))

(defn broadcast! [event-type message]
  (println (str "Broadcasting message '" event-type "': " message))
  (go-loop [channels-left @channels]
           (let [channel (first channels-left)]
             (println (str "Sending to channel: " channel))
             (>! channel {:type event-type :message message})
             (println (str "Total channels count right now: " (count channels-left)))
             (if (> 1 (count channels-left))
               (recur (rest channels-left))
               nil))))
