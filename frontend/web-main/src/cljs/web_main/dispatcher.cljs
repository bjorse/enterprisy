(ns web-main.dispatcher
  (:require [web-main.store :as store]
            [web-main.data.clients-data :as clients-data]
            [web-main.data.todo-data :as todo-data]
            [web-main.data.workorders-data :as workorders-data]))

(defn handle-update-current-workorder-response [workorder & [callback]]
  (let [client-id (:client-id workorder)
        fixed-workorder (merge workorder {:client clients-data/default-client})]
    (reset! store/current-workorder fixed-workorder)
    (clients-data/get-client client-id #(swap! store/current-workorder assoc :client %))
    (when callback (callback workorder))))

(defn handle-update-workorders-response [workorders & [callback]]
  (reset! store/workorders workorders)
  (when callback (callback workorders)))

(defn handle-update-current-client-workorders-response [workorders & [callback]]
  (reset! store/current-client-workorders workorders)
  (when callback (callback workorders)))

(defn handle-update-current-client-response [client & [callback]]
  (reset! store/current-client client)
  (when callback (callback client)))

(defn handle-update-todo-items-response [todo-items & [callback]]
  (reset! store/todo-items todo-items)
  (when callback (callback todo-items)))

(defn reset-current-client! []
  (reset! store/current-client nil)
  (reset! store/current-client-workorders []))

(defn update-current-workorder! [workorder-id & [callback]]
  (reset! store/current-workorder nil)
  (workorders-data/get-workorder workorder-id #(handle-update-current-workorder-response % callback)))

(defn update-workorders! [& [callback]]
  (workorders-data/get-workorders #(handle-update-workorders-response % callback)))

(defn update-current-client-workorders! [client-id & [callback]]
  (workorders-data/get-workorders-for-client client-id #(handle-update-current-client-workorders-response % callback)))

(defn update-current-client! [client-id & [callback]]
  (reset-current-client!)
  (update-current-client-workorders! client-id #(handle-update-current-client-workorders-response %))
  (clients-data/get-client client-id #(handle-update-current-client-response % callback)))

(defn update-todo-items! [& [callback]]
  (todo-data/get-todo-items #(handle-update-todo-items-response % callback)))
