(ns web-main.pages.client-page
  (:require [reagent.core :as reagent :refer [atom]]
            [web-main.store :as store]
            [web-main.data.clients-data :as clients-data]))

(def active-page (atom "profile"))

(def status-filters (atom {:new false :approved false :rejected false :in-progress false :finished false :closed false}))

(defn header [{:keys [firstname lastname birthdate]}]
  (let [fullname (str firstname " " lastname)]
    [:div.page-header
      [:h1 fullname
        [:small.left-buffer birthdate]]]))

(defn tab [title name]
  (let [active? #(= @active-page %)]
    [:li {:role="presentation" :class (when (active? name) "active")} [:a.link {:on-click #(reset! active-page name)} title]]))

(defn info-group [{:keys [column-size title value]}]
  (let [column-name (str "col-md-" column-size)]
    [:div {:class column-name}
      [:div.form.group
        [:label title] [:br] value]]))

(defn profile-tab [{:keys [firstname lastname age gender email added active]}]
  [:div
    [:div.panel.panel-default.top-buffer
      [:div.panel-body
        [:div.row
          (info-group {:column-size 6 :title "Full name" :value (str firstname " " lastname)})
          (info-group {:column-size 3 :title "Age" :value age})
          (info-group {:column-size 3 :title "Gender" :value gender})]
        [:div.row.top-buffer
          (info-group {:column-size 6 :title "E-mail adress" :value email})
          (info-group {:column-size 6 :title "Joined" :value added})]]]
    (when active
      [:button.btn.btn-danger.right [:strong "Deactivate client"]])])

(defn add-workorder-button []
  [:button.btn.btn-default {:on-click #(js/alert "Add work order!")} [:span.glyphicon.glyphicon-plus] " Add a new work order"])

(defn no-workorders-info []
  [:div.alert.alert-info.top-buffer "This client has no work orders! "
    [:a.link {:on-click #(js/alert "Add work order!")} "Click here to add a work order."]])

(defn get-status-row-color [status]
  (case status
    "Rejected" "danger"
    "In progress" "active"
    "Finished" "success"
    "Closed" "info"
    ""))

(defn workorder-row [{:keys [id title status updated]}]
  [:tr {:class (get-status-row-color status)}
    [:td [:a {:href (str "#/workorders/" id)} title]]
    [:td status]
    [:td updated]])

(defn workorder-count-row [workorder-count]
  [:tr
    [:td]
    [:td]
    [:td [:small (str "Number of work orders listed: " workorder-count)]]])

(defn workorder-table [source]
  [:div
    [:table.table
      [:thead
        [:tr
          [:td [:strong "Title"]]
          [:td [:strong "Status"]]
          [:td [:strong "Changed"]]]]
      [:tbody
        (for [row (reverse (sort-by :updated source))]
          ^{:key (:id row)} [workorder-row row])
      (workorder-count-row (count source))]]])

(defn get-filter-key-name [filter-key]
  (case filter-key
    :new "New"
    :approved "Approved"
    :rejected "Rejected"
    :in-progress "In progress"
    :finished "Finished"
    :closed "Closed"))

(defn filter-workorder [filters {:keys [status]}]
  (some #(= status %) filters))

(defn active-filters []
  (map #(get-filter-key-name %) (keys (into {} (filter val @status-filters)))))

(defn filter-workorders [workorders]
  (let [filters (active-filters)]
    (filter #(filter-workorder filters %) workorders)))

(defn filter-active? [filter-key]
  (get @status-filters filter-key))

(defn toggle-filter! [filter-key]
  (let [value (get @status-filters filter-key)]
    (swap! status-filters assoc filter-key (not value))))

(defn clear-filters! []
  (loop [filter-keys (filter #(filter-active? %) (keys @status-filters))]
    (let [key (first filter-keys)]
      (when-not (nil? key)
        (swap! status-filters assoc key false)
        (recur (rest filter-keys))))))

(defn workorders-status-filter-button [{:keys [key text]}]
  (let [active (filter-active? key)
        icon (if active "glyphicon-check" "glyphicon-unchecked")]
    [:a.btn.btn-default {:class (when active "active") :on-click #(toggle-filter! key)}
      [:span.glyphicon {:class icon}] (str " " text)]))

(defn workorders-clear-filter-button []
  [:a.btn.btn-default {:on-click clear-filters!}
    [:span.glyphicon.glyphicon-remove] " Clear filters"])

(defn workorders-status-filter-bar []
  [:div.btn-group {:role "group"}
    (workorders-status-filter-button {:key :new :text "New"})
    (workorders-status-filter-button {:key :approved :text "Approved"})
    (workorders-status-filter-button {:key :rejected :text "Rejected"})
    (workorders-status-filter-button {:key :in-progress :text "In progress"})
    (workorders-status-filter-button {:key :finished :text "Finished"})
    (workorders-status-filter-button {:key :closed :text "Closed"})
    (workorders-clear-filter-button)])

(defn workorders-control-bar []
  [:div.btn-toolbar {:role "toolbar"}
    (workorders-status-filter-bar)
    [:div.btn-group.pull-right {:role "group"}
      (add-workorder-button)]])

(defn workorders-tab [workorders {:keys [active]}]
  (let [any-workorders? (pos? (count workorders))]
    [:div
      [:div.panel.panel-default.top-buffer
        [:div.panel-body
          (when any-workorders?
            (workorders-control-bar))
          (when active
            (when-not any-workorders?
              (no-workorders-info)))]
        (if (pos? (count (active-filters)))
          (workorder-table (filter-workorders workorders))
          (workorder-table workorders))]]))

(defn user-info [client workorders]
  (let [active (:active client)]
    [:div
      (header client)
      (when-not active
        [:div.alert.alert-danger
          [:span.glyphicon.glyphicon-exclamation-sign]
          [:strong.left-buffer "Warning!"] " This client is no longer active! "
          [:a.link {:on-click #(js/alert "Not implemented yet!")} "Click here to reactivate this client."]])
      [:ul.nav.nav-pills
        (tab "Profile" "profile")
        (tab "Work orders" "workorders")]
      [:div
        (case @active-page
          "profile" (profile-tab client)
          "workorders" (workorders-tab workorders client))]]))

(defn render []
  (let [client @store/current-client
        workorders @store/current-client-workorders]
  [:div
    (when client
      (user-info client workorders))]))