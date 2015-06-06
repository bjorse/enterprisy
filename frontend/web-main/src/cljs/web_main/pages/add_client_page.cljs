(ns web-main.pages.add-client-page
  (:require [reagent.core :as reagent :refer [atom]]
            [web-main.components :as components]
            [web-main.data.clients-data :as clients-data]))

(def client (atom {:firstname "" :lastname "" :email "" :birthdate "" :gender "Male"}))

(def validation-errors (atom []))

(defn any-validation-errors? []
  (not-empty (seq @validation-errors)))

(defn has-validation-error? [param]
  (let [name (name param)]
    (= true (some #(= name (:key %)) @validation-errors))))

(defn get-validation-errors []
  (map #(:text %) @validation-errors))

(defn reset-client! []
  (reset! client {:firstname "" :lastname "" :email "" :birthdate "" :gender "Male"}))

(defn reset-form! []
  (reset-client!)
  (reset! validation-errors []))

(defn input-form [{:keys [id placeholder param]}]
  [:input.form-control {:type "input"
                        :id id
                        :placeholder placeholder
                        :value (get @client param)
                        :on-change #(swap! client assoc param (-> % .-target .-value))}])

(defn input-form-group [{:keys [title placeholder param]}]
  (let [has-error (has-validation-error? param)
        group-id (str "add-client-" (name param))]
    [:div.form-group {:class (when has-error "has-error")}
      [:label {:for group-id} title]
      (input-form {:id group-id :placeholder placeholder :param param})]))

(defn radio-form [{:keys [id group-name icon text param]}]
  (let [checked (= (get @client param) text)]
    [:label.btn.btn-default {:on-click #(swap! client assoc param text) :class (when checked "active")}
      [:span.glyphicon {:class icon}] (str " " text)
      [:input {:type "radio" :id id :name group-name}]]))

(defn validation-error-row [error]
  [:div
    [:span.glyphicon.glyphicon-exclamation-sign] (str " " error)
    [:br]])

(defn render []
  [:div.container-fluid
    (when (any-validation-errors?)
      (let [errors (get-validation-errors)]
        [:div.row
          [:div.alert.alert-danger
            (for [error errors]
              ^{:key error} [validation-error-row error])]]))
    [:div.row
      [:div.col-md-6
        (input-form-group {:title "First name" :placeholder "Enter first name" :param :firstname})]
      [:div.col-md-6
        (input-form-group {:title "Last name" :placeholder "Enter last name" :param :lastname})]]
    [:div.row
      [:div.col-md-6
        (input-form-group {:title "E-mail adress" :placeholder "Enter e-mail adress" :param :email})]
      [:div.col-md-6
        (input-form-group {:title "Birth date (YYYY-MM-DD)" :placeholder "Enter birth date" :param :birthdate})]]
    [:div.row
      [:div.col-md-6
        [:div.form-group
          [:div.btn-group {:data-toggle "buttons"}
            (radio-form {:id "add-client-gender-male" :group-name "add-client-gender" :icon "glyphicon-king" :text "Male" :param :gender})
            (radio-form {:id "add-client-gender-female" :group-name "add-client-gender" :icon "glyphicon-queen" :text "Female" :param :gender})]]]
      [:div.col-md-6
        [:button.btn.btn-default.btn-xs.right {:on-click #(reset-form!)} "Reset form"]]]])
