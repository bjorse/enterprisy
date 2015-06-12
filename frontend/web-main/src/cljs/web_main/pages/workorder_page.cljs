(ns web-main.pages.workorder-page
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [web-main.validation :as validation]
            [web-main.data.workorders-data :as workorders-data]
            [web-main.utils :as utils]))

(defn get-priority-color [priority]
  (case priority
    1 "label-info"
    2 "label-primary"
    3 "label-success"
    4 "label-warning"
    5 "label-danger"
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

(defn show-rejected-actions []
  [:div.alert.alert-danger.big
    [:span.glyphicon.glyphicon-exclamation-sign]
    [:strong.left-buffer "Inactive! "] "This work order is rejected and cannot be finished."])

(defn show-aborted-actions []
  [:div.alert.alert-danger.big
    [:span.glyphicon.glyphicon-exclamation-sign]
    [:strong.left-buffer "Inactive! "] "This work order is aborted and cannot be finished."])

(defn show-approved-actions [id]
  [:div.row
    [:div.col-md-12
      [:button.btn.btn-lg.btn-primary.pull-right {:on-click #(workorders-data/start-workorder id)}
        [:span.glyphicon.glyphicon-ok] " Initiate work for this work order"]]])

(defn show-finished-actions [id]
  [:div.alert.alert-info.big
    [:span.glyphicon.glyphicon-ok-sign]
    [:strong.left-buffer "Good job! "] "This work order has been finished! "
    [:a.alert-link.link {:on-click #(workorders-data/close-workorder id)} "Click here to close this work order!"]])

(defn show-closed-actions []
  [:div.alert.alert-info.big
    [:span.glyphicon.glyphicon-info-sign]
    [:strong.left-buffer "Closed! "] "This work order is closed."])

(defn finish-workorder [id actual-time validation-errors]
  (let [callback (fn [response]
                   (if (contains? (:response response) :errors)
                     (reset! validation-errors (:errors (:response response)))
                     (reset! validation-errors [])))]
    (workorders-data/finish-workorder {:id id :actual-time actual-time :callback callback})))

(defn in-progress-form [id actual-time validation-errors]
  (let [has-error (validation/has-validation-error? :actual-time @validation-errors)]
    [:div
      (validation/render-errors @validation-errors)
      [:div.row
        [:div.col-md-6
          [:div.form-inline
            [:div.form-group {:class (when has-error "has-error")}
              [:label {:for "actual-time-form"} "Actual time spent (in hours)"]
              [:input#actual-time-form.form-control.left-buffer {:type "text"
                                                                 :value @actual-time
                                                                 :on-change #(reset! actual-time (-> % .-target .-value))}]]]]
        [:div.col-md-6
          [:div.pull-right
            [:button.btn.btn-danger {:on-click #(workorders-data/abort-workorder id)}
              [:span.glyphicon.glyphicon-remove] " Abort"]
            [:button.btn.btn-success.left-buffer {:on-click #(finish-workorder id @actual-time validation-errors)}
              [:span.glyphicon.glyphicon-ok] " Finish work"]]]]]))

(defn show-in-progress-actions [id]
  (let [actual-time (atom "0")
        validation-errors (atom [])]
    [in-progress-form id actual-time validation-errors]))

(defn show-new-actions [id]
  [:div.row
    [:div.col-md-12
      [:div.pull-right
        [:button.btn.btn-danger.btn-lg {:on-click #(workorders-data/reject-workorder id)}
          [:span.glyphicon.glyphicon-remove] " Reject"]
        [:button.btn.btn-success.btn-lg.left-buffer {:on-click #(workorders-data/approve-workorder id)}
          [:span.glyphicon.glyphicon-ok] " Approve"]]]])

(defn show-actions [{:keys [id status]}]
  [:div
    [:hr]
      (case status
        "new" [show-new-actions id]
        "approved" [show-approved-actions id]
        "rejected" [show-rejected-actions]
        "in-progress" [show-in-progress-actions id]
        "aborted" [show-aborted-actions]
        "finished" [show-finished-actions id]
        "closed" [show-closed-actions]
        nil)])

(defn title [{:keys [id title]}]
  [:div
    [:h3 [:span.text-muted (str "Workorder #" id ": ")] title]
      [:hr]])

(defn info-bar [{:keys [client added changed priority status]}]
  (let [client-name (str (:firstname client) " " (:lastname client))]
    [:div
      [:span.label {:class (get-priority-color priority)}
        [:span.glyphicon {:class (utils/get-priority-icon priority)}]
        [:span.left-buffer-sm (utils/get-priority-text priority)]]
      [:span.label.left-buffer {:class (get-status-color status)} (utils/get-status-text status)]
      [:span.text-muted.left-buffer "Added by "
        [:a {:href (str "#/clients/" (:id client))} client-name] " " added " (latest change: " changed ")"]]))

(defn detailed-info [{:keys [description estimated-time actual-time status]}]
  (let [has-description? (not (string/blank? description))
        text (if has-description? description [:em "No description available"])]
    [:div.top-buffer.well.well-lg
      [:p
        [:strong "Description:"] [:br] text]
      [:p
        [:strong "Estimated time: "] (str estimated-time " hours")]
      (when (and actual-time (or (= "finished" status) (= "closed" status)))
        [:p
          [:strong "Actual time spent: "] (str actual-time " hours")])]))

(defn render [workorder]
  [:div
    [title @workorder]
    [info-bar @workorder]
    [detailed-info @workorder]
    [show-actions @workorder]])
