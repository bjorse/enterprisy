(ns workorder-process-service.process
  (:require [workorder-process-service.process-db :as db]
            [workorder-process-service.queuing :as queing]))

(defn get-message-type [status]
  (case status
    "accepted" queing/workorder-accepted-message-type
    "rejected" queing/workorder-rejected-message-type
    "in-progress" queing/workorder-in-progress-message-type
    "aborted" queing/workorder-aborted-message-type
    "finished" queing/workorder-finished-message-type
    "closed" queing/workorder-closed-message-type
    nil))

(defn create-message [workorder-id status client-id]
  {:workorder-id workorder-id
   :status status
   :client-id client-id})

(defn handle-message [type message]
  (when (= type "workorder.updated")
    (println (str "Handling workorder.updated: " message))
    (let [workorder-id (:id message)
          status (:status message)
          client-id (:client-id message)
          db-entity (db/get-by-workorder-id-and-status workorder-id status)]
      (when (nil? db-entity)
        (if-let [message-type (get-message-type status)]
          (let [message-to-add (create-message workorder-id status client-id)]
            (queing/publish! message-to-add message-type)
            (db/add-process-status! message-to-add)))))))
