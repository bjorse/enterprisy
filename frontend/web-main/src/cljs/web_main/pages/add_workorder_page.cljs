(ns web-main.pages.add-workorder-page
  (:require [reagent.core :as reagent :refer [atom]]
            [web-main.validation :as validation]
            [web-main.components :as components]
            [web-main.data.workorders-data :as workorders-data]))

(def workorder (atom {:title "" :description "" :estimated-time "0"}))

(def validation-errors (atom []))

(defn reset-workorder! []
  (reset! workorder {:title "" :description "" :estimated-time "0"}))

(defn reset-form! []
  (reset-workorder!)
  (reset! validation-errors []))

(defn input-form [{:keys [id placeholder param]}]
  [:input.form-control {:type "input"
                        :id id
                        :placeholder placeholder
                        :value (get @workorder param)
                        :on-change #(swap! workorder assoc param (-> % .-target .-value))}])

(defn textarea-form [{:keys [id placeholder param rows]}]
  [:textarea.form-control {:id id
                           :placeholder placeholder
                           :value (get @workorder param)
                           :rows rows
                           :on-change #(swap! workorder assoc param (-> % .-target .-value))}])


(defn input-form-group [{:keys [title placeholder param]}]
  (let [has-error (validation/has-validation-error? param @validation-errors)
        group-id (str "add-workorder-" (name param))]
    [:div.form-group {:class (when has-error "has-error")}
      [:label {:for group-id} title]
      (input-form {:id group-id :placeholder placeholder :param param})]))

(defn textarea-form-group [{:keys [title placeholder param rows]}]
  (let [has-error (validation/has-validation-error? param @validation-errors)
        group-id (str "add-workorder-" (name param))]
    [:div.form-group {:class (when has-error "has-error")}
     [:label {:for group-id} title]
     (textarea-form {:id group-id :placeholder placeholder :param param :rows rows})]))

(defn render []
  [:div.container-fluid
    (validation/render-errors @validation-errors)
    [:div.row
      [:div.col-md-12
        (input-form-group {:title "Title" :placeholder "Enter title" :param :title})]]
    [:div.row
      [:div.col-md-12
        (textarea-form-group {:title "Description" :placeholder "Enter description (optional)" :param :description :rows "5"})]]
    [:div.row
      [:div.col-md-6
        (input-form-group {:title "Estimated time (hours)" :placeholder "" :param :estimated-time})]
      [:div.col-md-6
        [:button.btn.btn-default.btn-xs.right {:on-click #(reset-form!)} "Reset form"]]]])
