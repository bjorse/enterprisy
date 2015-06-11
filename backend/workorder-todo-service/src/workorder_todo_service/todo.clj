(ns workorder-todo-service.todo
  (:require [workorder-todo-service.todo-db :as db]
            [workorder-todo-service.queuing :as queing]
            [workorder-todo-service.util :as util]
            [clj-http.client :as client]))

(def todo-service-url "http://localhost:3030/todo-items")

(defn add-todo-item-external! [todo-item]
  (let [result (client/post todo-service-url {:body (util/convert-map-to-json {:todo-item todo-item})
                                                                               :content-type :json
                                                                               :accept :json
                                                                               :throw-exceptions false})]
    (println (str "Result form todo service: " result))
    {:status (:status result) :body (util/extract-body result)}))

(defn add-todo-item-internal! [{:keys [workorder-id workorder-status client-id todo-id]}]
    (db/add-workorder-todo! {:workorder-id workorder-id
                             :workorder-status workorder-status
                             :client-id client-id
                             :todo-id todo-id}))

(defn get-internal-workorder-todo-item [title workorder]
  {:title title
   :workorder-id (:workorder-id workorder)
   :workorder-status (:status workorder)
   :client-id (:client-id workorder)
   :priority (:priority workorder)})

(defn get-external-workorder-todo-item [{:keys [title workorder-id priority]}]
  {:title title
   :type "workorder"
   :type-id workorder-id
   :priority priority})

(defn get-todo-title-by-message-type [type]
  (case type
    "workorder.new" "Workorder needs to be approved or rejected"
    "workorder.approved" "Workorder needs to be started"
    "workorder.in-progress" "Workorder needs to be finished"
    "workorder.finished" "Workorder needs to be closed"
    nil))

(defn handle-workorder-message [type message]
  (when-let [title (get-todo-title-by-message-type type)]
    (get-internal-workorder-todo-item title message)))

(defn handle-message [type message]
  (when-let [todo-item (handle-workorder-message type message)]
    (println (str "This is how the message looks from queue: " message))
    (when-not (db/get-by-workorder-id-and-status (:workorder-id todo-item) (:workorder-status todo-item))
      (let [external-todo-item (get-external-workorder-todo-item todo-item)]
        (println (str "Sending this content to external todo service: " external-todo-item))
        (when-let [added-external-todo-item (add-todo-item-external! external-todo-item)]
          (let [extracted-external-todo-item (:body added-external-todo-item)
                internal-todo-item (merge todo-item {:todo-id (:id extracted-external-todo-item)})]
            (println (str "Adding this item in database: " internal-todo-item))
            (add-todo-item-internal! internal-todo-item)))))))
