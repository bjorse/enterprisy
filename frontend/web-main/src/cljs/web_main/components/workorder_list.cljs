(ns web-main.components.workorder-list
  (:require [reagent.core :as reagent :refer [atom]]
            [web-main.utils :as utils]))

(defn add-workorder-button [modal-name]
  [:button.btn.btn-default {:data-toggle "modal" :data-target (str "#" modal-name)}
    [:span.glyphicon.glyphicon-plus] " Add a new work order"])

(defn get-status-row-color [status]
  (case status
    "rejected" "danger"
    "in-progress" "active"
    "finished" "success"
    "aborted" "danger"
    "closed" "info"
    ""))

(defn get-priority-text [priority]
  [:div
    [:span.glyphicon.small {:class (utils/get-priority-icon priority)}] (str " " (utils/get-priority-text priority))])

(defn workorder-row [{:keys [id title status priority changed]}]
  [:tr {:class (get-status-row-color status)}
    [:td [:a {:href (str "#/workorders/" id)} title]]
    [:td.no-wrap (utils/get-status-text status)]
    [:td.no-wrap (get-priority-text priority)]
    [:td.no-wrap changed]])

(defn workorder-count-row [workorder-count]
  [:tr
    [:td [:small (str "Number of work orders listed: " workorder-count)]]
    [:td]
    [:td]
    [:td]])

(defn workorder-table [source]
  [:div
    [:table.table
      [:thead
        [:tr
          [:td [:strong "Title"]]
          [:td [:strong "Status"]]
          [:td [:strong "Priority"]]
          [:td [:strong "Changed"]]]]
      [:tbody
        (for [row (reverse (sort-by :updated source))]
          ^{:key (:id row)} [workorder-row row])
      (workorder-count-row (count source))]]])

(defn filter-workorder [filters param workorder]
  (some #(= (keyword (str (param workorder))) %) filters))

(defn active-filters [filters]
  (keys (into {} (filter val filters))))

(defn any-active-filters? [filters]
  (pos? (count (active-filters filters))))

(defn filter-workorders [filters param workorders]
  (let [selected-filters (active-filters filters)]
    (if (empty? selected-filters)
      workorders
      (filter #(filter-workorder selected-filters param %) workorders))))

(defn filter-active? [filters filter-key]
  (get filters filter-key))

(defn toggle-filter! [filters filter-key]
  (let [value (get @filters filter-key)]
    (swap! filters assoc filter-key (not value))))

(defn workorders-filter-button [filters {:keys [key text]}]
  (let [active (filter-active? @filters key)
        icon (if active "glyphicon-check" "glyphicon-unchecked")]
    [:a.btn.btn-default {:class (when active "active") :on-click #(toggle-filter! filters key)}
      [:span.glyphicon {:class icon}] (str " " text)]))

(defn filter-bar [filters buttons]
  (let [filter-props {:filters filters}]
    [:div.btn-group {:role "group"}
      (for [button buttons]
        ^{:key (:key button)} [workorders-filter-button filters button])]))

(defn workorders-status-filter-bar [filters]
  (let [buttons [{:key :new :text "New"}
                 {:key :approved :text "Approved"}
                 {:key :rejected :text "Rejected"}
                 {:key :in-progress :text "In progress"}
                 {:key :aborted :text "Aborted"}
                 {:key :finished :text "Finished"}
                 {:key :closed :text "Closed"}]]
    (filter-bar filters buttons)))

(defn workorders-priority-filter-bar [filters]
  (let [buttons [{:key :1 :text "Very low"}
                 {:key :2 :text "Low"}
                 {:key :3 :text "Normal"}
                 {:key :4 :text "High"}
                 {:key :5 :text "Very high"}]]
    (filter-bar filters buttons)))

(defn workorders-upper-control-bar [status-filters add-workorder-modal-name]
  [:div.btn-toolbar {:role "toolbar"}
    [workorders-status-filter-bar status-filters]
    (when add-workorder-modal-name
      [:div.btn-group.pull-right {:role "group"}
        [add-workorder-button add-workorder-modal-name]])])

(defn workorders-lower-control-bar [priority-filters]
  [:div.btn-toolbar {:role "toolbar"}
    [workorders-priority-filter-bar priority-filters]])

(defn apply-filters [workorders status-filters priority-filters]
  (let [wrap-filtering (fn [source filters param] (filter-workorders filters param source))]
    (-> workorders
        (wrap-filtering status-filters :status)
        (wrap-filtering priority-filters :priority))))

(defn render-internal [workorders modal-name status-filters priority-filters]
  (let [any-active-status-filters? (any-active-filters? @status-filters)
        any-active-priority-filters? (any-active-filters? @priority-filters)
        any-active-filters? (or any-active-status-filters? any-active-priority-filters?)]
    [:div
      [workorders-upper-control-bar status-filters modal-name]
        [:div.top-buffer-sm
          [workorders-lower-control-bar priority-filters]]
        (if any-active-filters?
          [workorder-table (apply-filters workorders @status-filters @priority-filters)]
          [workorder-table workorders])]))

(defn render [workorders & [add-workorder-modal-name]]
  (let [status-filters (atom {:new false :approved false :rejected false :in-progress false :aborted false :finished false :closed false})
        priority-filters (atom {:1 false :2 false :3 false :4 false :5 false})]
    (fn []
      [render-internal @workorders add-workorder-modal-name status-filters priority-filters])))
