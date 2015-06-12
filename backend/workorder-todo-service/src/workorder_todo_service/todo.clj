(ns workorder-todo-service.todo
  (:require [workorder-todo-service.todo-db :as db]
            [workorder-todo-service.queuing :as queing]
            [workorder-todo-service.util :as util]
            [clj-http.client :as client]))

(def todo-service-url "http://localhost:3030/todo-items")

(def subscribed-messages ["workorder.new" "workorder.approved" "workorder.rejected" "workorder.in-progress" "workorder.aborted" "workorder.finished" "workorder.closed"])

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

(defn get-internal-workorder-todo-item [description workorder]
  {:title (:title workorder)
   :description description
   :workorder-id (:workorder-id workorder)
   :workorder-status (:status workorder)
   :client-id (:client-id workorder)
   :priority (:priority workorder)})

(defn get-external-workorder-todo-item [{:keys [title description workorder-id priority]}]
  {:title title
   :type "workorder"
   :type-id workorder-id
   :description description
   :priority priority})

(defn get-previous-status [status]
  (case status
    "approved" "new"
    "rejected" "new"
    "in-progress" "approved"
    "finished" "in-progress"
    "aborted" "in-progress"
    "closed" "finished"
    nil))

(defn get-todo-description-by-message-type [type]
  (case type
    "workorder.new" "Approval or rejection is needed"
    "workorder.approved" "Waiting for initiation"
    "workorder.in-progress" "Work needs to be completed"
    "workorder.finished" "Waiting for final verification"
    nil))

(defn message-subscribed? [message-type]
  (some #(= message-type %) subscribed-messages))

(defn delete-outdated-todo-item! [{:keys [workorder-id status]}]
  (when-let [previous-status (get-previous-status status)]
    (println (str "Current status: " status ", previous status: " previous-status))
    (when-let [todo-item (db/get-by-workorder-id-and-status workorder-id previous-status)]
      (println (str "Sending request to delete todo item with id: " (:todo_id todo-item)))
      (client/delete todo-service-url {:body (util/convert-map-to-json {:id (:todo_id todo-item)})
                                       :content-type :json
                                       :accept :json
                                       :throw-exceptions false}))))

(defn handle-workorder-message [type message]
  (when (message-subscribed? type)
    (println (str "Are we subscribing on message type: " type "? YES, we are!"))
    (delete-outdated-todo-item! message)
    (when-let [description (get-todo-description-by-message-type type)]
      (get-internal-workorder-todo-item description message))))

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
