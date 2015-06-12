(ns web-main.utils)

(defn list-data [alist filter-text filter-function]
 (if-let [filter-text (some-> filter-text not-empty .toLowerCase)]
   (filter #(-> (filter-function %)
                .toLowerCase
                (.indexOf filter-text)
                (not= -1))
           alist)
   alist))

(defn get-priority-text [priority]
  (case priority
    1 "Very low priority"
    2 "Low priority"
    3 "Normal priority"
    4 "High priority"
    5 "Very high priority"
    nil))

(defn get-priority-icon [priority]
  (if (not= priority 3)
    (if (> priority 3) "glyphicon-triangle-top" "glyphicon-triangle-bottom")
    "glyphicon-minus"))

(defn get-status-text [status]
  (case status
    "new" "New"
    "approved" "Approved"
    "rejected" "Rejected"
    "in-progress" "In progress"
    "finished" "Finished"
    "aborted" "Aborted"
    "closed" "Closed"
    nil))

(defn get-status-color [status]
  (case status
    "rejected" "danger"
    "in-progress" "active"
    "finished" "success"
    "aborted" "danger"
    "closed" "info"
    "default"))

(defn get-priority-color [priority]
  (case priority
    1 "info"
    2 "primary"
    3 "success"
    4 "warning"
    5 "danger"
    nil))
