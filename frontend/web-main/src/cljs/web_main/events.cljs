(ns web-main.events
  (:require [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]]
            [web-main.store :as store])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn handle-todo-added! [todo-item]
  (reset! store/todo-items (conj @store/todo-items todo-item)))

(defn handle-todo-removed! [todo-item]
  (let [item-id (:id todo-item)]
    (reset! store/todo-items (filter #(not= (:id %) item-id) @store/todo-items))))

(defn handle-event-added! [event-message]
  (let [next-id (inc (count @store/events))]
    (reset! store/events (conj @store/events (merge event-message {:id next-id})))))

(defn handle-message! [message]
  (let [event-type (:type message)
        event-message (:message message)]
    (case event-type
      "todo.added" (handle-todo-added! event-message)
      "todo.removed" (handle-todo-removed! event-message)
      "event.added" (handle-event-added! event-message))))

(defn listen-for-events! []
  (go
    (let [{:keys [ws-channel]} (<! (ws-ch "ws://localhost:3100/events" {:format :transit-json}))]
      (go-loop []
        (let [{:keys [message error]} (<! ws-channel)]
          (if error
            (js/console.log "Error reading from event server: " error)
            (handle-message! message)))
      (recur)))))
