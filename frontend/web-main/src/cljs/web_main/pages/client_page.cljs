(ns web-main.pages.client-page
  (:require [reagent.core :as reagent :refer [atom]]
            [web-main.components :as components]
            [web-main.components.workorder-list :as workorder-list]
            [web-main.store :as store]
            [web-main.data.clients-data :as clients-data]
            [web-main.data.workorders-data :as workorders-data]
            [web-main.pages.add-workorder-page :as add-workorder-page]
            [web-main.dispatcher :as dispatcher]
            [web-main.utils :as utils]))

(def add-workorder-modal-name "add-workorder-modal")

(def active-page (atom "profile"))

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
          [info-group {:column-size 6 :title "Full name" :value (str firstname " " lastname)}]
          [info-group {:column-size 3 :title "Age" :value age}]
          [info-group {:column-size 3 :title "Gender" :value gender}]]
        [:div.row.top-buffer
          [info-group {:column-size 6 :title "E-mail adress" :value email}]
          [info-group {:column-size 6 :title "Joined" :value added}]]]]
    (when active
      [:button.btn.btn-danger.right [:strong "Deactivate client"]])])

(defn add-workorder-button []
  [:button.btn.btn-default {:data-toggle "modal" :data-target (str "#" add-workorder-modal-name)}
    [:span.glyphicon.glyphicon-plus] " Add a new work order"])

(defn no-workorders-info []
  [:div.alert.alert-info.top-buffer "This client has no work orders! "
    [:a.link {:data-toggle "modal" :data-target (str "#" add-workorder-modal-name)} "Click here to add a work order."]])

(defn workorders-tab [workorders {:keys [active]}]
  (let [any-workorders? (pos? (count @workorders))]
    [:div
      [:div.panel.panel-default.top-buffer
        [:div.panel-body
          (when any-workorders?
            [workorder-list/render workorders add-workorder-modal-name])
          (when active
            (when-not any-workorders?
              [no-workorders-info]))]]]))

(defn user-info [client workorders]
  (let [active (:active client)]
    [:div
      [header client]
      (when-not active
        [:div.alert.alert-danger
          [:span.glyphicon.glyphicon-exclamation-sign]
          [:strong.left-buffer "Warning!"] " This client is no longer active! "
          [:a.link {:on-click #(js/alert "Not implemented yet!")} "Click here to reactivate this client."]])
      [:ul.nav.nav-pills
        [tab "Profile" "profile"]
        [tab "Work orders" "workorders"]]
      [:div
        (case @active-page
          "profile" [profile-tab client]
          "workorders" [workorders-tab workorders client])]]))

(defn on-add-workorder-response [client-id]
  (fn [response]
    (if (contains? (:response response) :errors)
      (reset! add-workorder-page/validation-errors (:errors (:response response)))
      (do (components/close-modal! add-workorder-modal-name)
          (add-workorder-page/reset-form!)
          (dispatcher/update-current-client-workorders! client-id)))))

(defn on-add-workorder [client-id]
  (workorders-data/add-workorder @add-workorder-page/workorder (on-add-workorder-response client-id)))

(defn render [client workorders]
  [:div
    [components/modal-save {:id add-workorder-modal-name
                            :title (str "Add a new workorder for " (:firstname @client) " " (:lastname @client))
                            :body add-workorder-page/render
                            :on-save #(on-add-workorder (:id @client))}]
    (when @client
      [user-info @client workorders])])
