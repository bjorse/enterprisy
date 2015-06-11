(ns web-main.pages.workorder-page
  (:require [clojure.string :as string]
            [web-main.data.workorders-data :as workorders-data]))

(defn get-priority-text [priority]
  (case priority
    1 "Very low priority"
    2 "Low priority"
    3 "Normal priority"
    4 "High priority"
    5 "Very high priority"
    nil))

(defn get-priority-color [priority]
  (case priority
    1 "label-info"
    2 "label-primary"
    3 "label-success"
    4 "label-warning"
    5 "label-danger"
    nil))

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
    "new" "label-primary"
    "approved" "label-success"
    "rejected" "label-danger"
    "in-progress" "label-default"
    "finished" "label-info"
    "aborted" "label-danger"
    "closed" "label-default"
    nil))

(defn show-new-actions [id]
  [:div.row
    [:div.col-md-12
      [:div.pull-right
        [:button.btn.btn-danger {:on-click #(workorders-data/reject-workorder id)}
          [:span.glyphicon.glyphicon-remove] " Reject"]
        [:button.btn.btn-success.left-buffer {:on-click #(workorders-data/approve-workorder id)}
          [:span.glyphicon.glyphicon-ok] " Approve"]]]])

(defn show-temp-actions [id]
  [:div "This status is not implemented yet!"])

(defn show-actions [{:keys [id status]}]
  [:div
    [:hr]
      (case status
        "new" (show-new-actions id)
        (show-temp-actions id))])

(defn title [{:keys [id title]}]
  [:div
    [:h3 [:span.text-muted (str "Workorder #" id ": ")] title]
      [:hr]])

(defn info-bar [{:keys [client added changed priority status]}]
  (let [client-name (str (:firstname client) " " (:lastname client))]
    [:div
      [:span.label {:class (get-priority-color priority)} (get-priority-text priority)]
      [:span.label.left-buffer {:class (get-status-color status)} (get-status-text status)]
      [:span.text-muted.left-buffer "Added by "
        [:a {:href (str "#/clients/" (:id client))} client-name] " " added " (latest change: " changed ")"]]))

(defn detailed-info [{:keys [description estimated-time actual-time]}]
  (let [has-description? (not (string/blank? description))
        text (if has-description? description "No description available...")]
    [:div.top-buffer.well.well-lg
      [:p
        [:strong "Description:"] [:br] text]
      [:p
        [:strong "Estimated time: "] (str estimated-time " hours")]
      (when actual-time
        [:p
          [:strong "Actual time spent: "] (str actual-time " hours")])]))

(defn render [workorder]
  (let [client (:client @workorder)
        client-name (str (:firstname client) " " (:lastname client))]
    [:div
      (title @workorder)
      (info-bar @workorder)
      (detailed-info @workorder)
      (show-actions @workorder)]))
