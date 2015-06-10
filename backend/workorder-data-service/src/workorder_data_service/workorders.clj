(ns workorder-data-service.workorders
  (:require [clojure.string :as string]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [clj-time.format :as time-format]
            [workorder-data-service.workorders-db :as db]
            [workorder-data-service.queuing :as queuing]
            [workorder-data-service.util :as util]))

(def valid-status ["new" "approved" "rejected" "in-progress" "aborted" "finished" "closed"])

(def number-format #"^(0|[1-9][0-9]*)$")

(defn numeric? [value]
  (re-matches number-format value))

(defn valid-status-gotos [status]
  (case status
    "new" ["approved" "rejected"]
    "approved" ["in-progress"]
    "in-progress" ["aborted" "finished"]
    "finished" ["closed"]
    nil))

(defn validate-new-status [old-status new-status]
  (if (= old-status new-status)
    true
    (let [allowed-new-status (valid-status-gotos old-status)]
      (if (some new-status allowed-new-status)
        true
        false))))

(defn validate-workorder [{:keys [title description estimated-time client-id]}]
  (let [errors (list (when (string/blank? title) {:key "title" :text "Title cannot be empty!"})
                     (when (string/blank? estimated-time) {:key "estimated-time" :text "Estimated time cannot be empty!"})
                     (when-not (numeric? estimated-time) {:key "estimated-time" :text "Estimated time must be a numeric value!"}))]
    (filter #(not (= nil %)) errors)))

(defn validate-workorder-update [old-workorder workorder]
  (let [errors (validate-workorder workorder)
        old-status (:status old-workorder)
        new-status (:status workorder)]
    (if (validate-new-status old-status new-status)
      errors
      (conj errors {:key "status" :text "The new status is not allowed!"}))))

(defn fix-workorder-for-create [workorder]
  (merge workorder {:status "new"}))

(defn fix-workorder-for-update [workorder-db workorder]
  (merge workorder-db workorder))

(defn format-workorder [{:keys [title description estimated-time status client-id]}]
  {:title title
   :description description
   :estimated-time (util/convert-to-number estimated-time)
   :status status
   :client-id (util/convert-to-number client-id)})

(defn get-workorder [id]
  (let [result (db/get-workorder id)]
    (if (empty? result)
      {:status 404 :body {:id id}}
      (first result))))

(defn get-workorders-by-client-id [client-id]
  (db/get-workorders-by-client-id client-id))

(defn add-workorder! [workorder]
  (println (str "Trying to add this workorder: " workorder))
  (let [validation-errors (validate-workorder workorder)]
    (if (empty? validation-errors)
      (let [fixed-workorder (fix-workorder-for-create workorder)]
        (if-let [result (db/add-workorder! (format-workorder fixed-workorder))]
          (do (println (str "Added workorder: " result))
              (queuing/publish! result queuing/add-workorder-message-type)
              {:status 200 :body result})
        {:status 500})
      {:status 422 :body {:errors validation-errors}}))))

(defn update-workorder! [workorder]
  (let [workorder-id (:id workorder)
        old-workorder (db/get-workorder workorder-id)
        fixed-workorder (fix-workorder-for-update old-workorder workorder)
        validation-errors (validate-workorder-update old-workorder fixed-workorder)]
    (if (empty? validation-errors)
      (if-let [result (db/update-workorder! (format-workorder fixed-workorder))]
        (do (println (str "Updated workorder: " result))
            (queuing/publish! result queuing/update-workorder-message-type)
            {:status 200 :body result})
        {:status 500})
      {:status 422 :body {:errors validation-errors}})))
