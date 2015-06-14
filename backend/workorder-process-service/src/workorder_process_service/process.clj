(ns workorder-process-service.process
  (:require [workorder-process-service.process-db :as db]
            [workorder-process-service.queuing :as queing]))

(def subscribed-messages ["workorder.added" "workorder.updated"])

(defn get-message-type [status]
  (case status
    "new" queing/workorder-new-message-type
    "approved" queing/workorder-approved-message-type
    "rejected" queing/workorder-rejected-message-type
    "in-progress" queing/workorder-in-progress-message-type
    "aborted" queing/workorder-aborted-message-type
    "finished" queing/workorder-finished-message-type
    "closed" queing/workorder-closed-message-type
    nil))

(defn create-message [workorder-id title status client-id priority]
  {:workorder-id workorder-id
   :title title
   :status status
   :client-id client-id
   :priority priority})

(defn get-event-text [status]
  (case status
    "approved" "Approved"
    "rejected" "Rejected"
    "in-progress" "In progress"
    "aborted" "Aborted"
    "finished" "Finished"
    "closed" "Closed"
    nil))

(defn create-event-message [id title status]
  (when-let [event-text (get-event-text status)]
    (str "Workorder #" id " - '" title "' has a new status: " status)))

(defn handle-message [type message]
  (when (some #(= % type) subscribed-messages)
    (println (str "Handling message type '" type "': " message))
    (let [workorder-id (:id message)
          title (:title message)
          status (:status message)
          client-id (:client-id message)
          priority (:priority message)
          db-entity (db/get-by-workorder-id-and-status workorder-id status)]
      (println (str "Entity from db: " db-entity))
      (when (nil? db-entity)
        (if-let [message-type (get-message-type status)]
          (let [message-to-add (create-message workorder-id title status client-id priority)]
            (println (str "Publishing message '" message-type "' to queue: " message-to-add))
            (queing/publish! message-to-add message-type)
            (when-let [event-message (create-event-message workorder-id title status)])
            (db/add-process-status! message-to-add)))))))
