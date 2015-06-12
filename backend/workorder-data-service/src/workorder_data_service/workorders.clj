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

(def prority-format #"^[1-5]$")

(defn numeric? [value]
  (re-matches number-format value))

(defn valid-priority? [value]
  (re-matches prority-format value))

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
      (if (some #(= new-status %) allowed-new-status)
        true
        false))))

(defn actual-time-needed? [new-status]
  (= "finished" new-status))

(defn actual-time-valid? [status actual-time]
  (if (actual-time-needed? status)
    (numeric? (str actual-time))
    true))

(defn filter-validation-error-list [error-list]
  (filter #(not (= nil %)) error-list))

(defn validate-workorder [{:keys [title description estimated-time actual-time priority client-id]}]
  (let [errors (list (when (string/blank? title) {:key "title" :text "Title cannot be empty!"})
                     (when (string/blank? (str estimated-time)) {:key "estimated-time" :text "Estimated time cannot be empty!"})
                     (when-not (numeric? (str estimated-time)) {:key "estimated-time" :text "Estimated time must be a numeric value!"})
                     (when-not (valid-priority? (str priority)) {:key "priority" :text "Priority must be value in range 1-5!"}))]
    (filter-validation-error-list errors)))

(defn validate-workorder-update [old-workorder workorder]
  (let [errors (validate-workorder workorder)
        old-status (:status old-workorder)
        new-status (:status workorder)
        new-errors (list (when-not (validate-new-status old-status new-status) {:key "status" :text "The new status is not allowed!"})
                         (when-not (actual-time-valid? new-status (:actual-time workorder)) {:key "actual-time" :text "Actual time spent is not valid (must be a numeric value)!"}))]
    (concat errors (filter-validation-error-list new-errors))))

(defn fix-workorder-for-create [workorder]
  (merge workorder {:status "new" :actual-time nil}))

(defn fix-workorder-for-update [workorder-db workorder]
  (println (str "Old workorder: " workorder-db))
  (println (str "New workorder: " workorder))
  (merge workorder-db workorder))

(defn format-workorder [{:keys [id title description estimated-time actual-time status priority client-id]}]
  {:id id
   :title title
   :description description
   :estimated-time (util/convert-to-number estimated-time)
   :actual-time (if actual-time (util/convert-to-number actual-time) nil)
   :status status
   :priority (util/convert-to-number priority)
   :client-id (util/convert-to-number client-id)})

(defn format-workorder-from-db [workorder]
  (let [fixed-workorder (clojure.set/rename-keys workorder {:estimated_time :estimated-time
                                                            :actual_time :actual-time
                                                            :client_id :client-id})]
    (merge fixed-workorder {:added (util/format-short-date (:added fixed-workorder))
                            :changed (util/format-short-date (:changed fixed-workorder))})))

(defn get-workorder [id]
  (let [result (db/get-workorder id)]
    (if (empty? result)
      {:status 404 :body {:id id}}
      (format-workorder-from-db (first result)))))

(defn get-workorders-by-client-id [client-id]
  (map #(format-workorder-from-db %) (db/get-workorders-by-client-id client-id)))

(defn add-workorder! [workorder]
  (println (str "Trying to add this workorder: " workorder))
  (let [validation-errors (validate-workorder workorder)]
    (if (empty? validation-errors)
      (let [fixed-workorder (fix-workorder-for-create workorder)]
        (if-let [result (db/add-workorder! (format-workorder fixed-workorder))]
          (do (println (str "Added workorder: " result))
              (queuing/publish! result queuing/add-workorder-message-type)
              {:status 200 :body result})
        {:status 500}))
      {:status 422 :body {:errors validation-errors}})))

(defn update-workorder! [workorder]
  (println (str "Trying to update this workorder: " workorder))
  (let [workorder-id (:id workorder)
        old-workorder (format-workorder-from-db (first (db/get-workorder workorder-id)))
        fixed-workorder (fix-workorder-for-update old-workorder workorder)
        validation-errors (validate-workorder-update old-workorder fixed-workorder)]
    (if (empty? validation-errors)
      (if-let [result (db/update-workorder! (format-workorder fixed-workorder))]
        (do (println (str "Updated workorder: " result))
            (queuing/publish! fixed-workorder queuing/update-workorder-message-type)
            {:status 200 :body fixed-workorder})
        {:status 500})
      {:status 422 :body {:errors validation-errors}})))
