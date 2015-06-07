(ns web-main.pages.clients-page
    (:require [reagent.core :as reagent :refer [atom]]
              [web-main.components :as components]
              [web-main.pages.add-client-page :as add-client-page]
              [web-main.data.clients-data :as clients-data]))

(def add-client-modal-name "add-client-modal")

(def search-string (atom ""))

(def search-result-limited (atom false))

(defn update-data-result! [data result]
  (reset! data (:clients result))
  (reset! search-result-limited (:limited result)))

(defn update-data! [data]
  (clients-data/get-clients #(update-data-result! data %) @search-string))

(defn result-row [{:keys [id firstname lastname email birthdate active]}]
  [:tr {:class (when (not active) "danger")}
    [:td [:a {:href (str "#/clients/" id)} (str firstname " " lastname)]]
    [:td email]
    [:td birthdate]
    [:td]])

(defn result-table [source]
  [:div
    [:table.table.table-condensed.table-hover
      [:thead
        [:tr
          [:td [:strong "Name"]]
          [:td [:strong "E-mail"]]
          [:td [:strong "Birth date"]]
          [:td [:small.right (str "Number of clients listed: " (count @source))]]]]
      [:tbody
        (for [row (sort-by :firstname @source)]
          ^{:key (:id row)} [result-row row])]]])

(defn search-bar [data]
  (let [save #(update-data! data)
        clear #(reset! search-string "")]
    [:div.input-group
      [:input.form-control {:type "input"
                            :value @search-string
                            :placeholder "Enter search text"
                            :on-change #(reset! search-string (-> % .-target .-value))
                            :on-key-down #(case (.-which %)
                                            13 (save)
                                            27 (clear)
                                            nil)}]
      [:div.input-group-btn
        [:button.btn.btn-default {:on-click save} [:span.glyphicon.glyphicon-search] " Search"]]]))

(defn add-client []
  [:div
    [:button.btn.btn-default {:data-toggle "modal" :data-target (str "#" add-client-modal-name)}
      [:span.glyphicon.glyphicon-plus] " Add a new client"]])

(defn on-add-client-response [response]
  (if (contains? (:response response) :errors)
    (reset! add-client-page/validation-errors (:errors (:response response)))
    (do (components/close-modal! add-client-modal-name)
        (add-client-page/reset-form!))))

(defn on-add-client []
  (clients-data/add-client @add-client-page/client on-add-client-response))

(defn render [clients]
  (let [any-clients (not-empty (seq @clients))]
    [:div
      (components/modal-save {:id add-client-modal-name
                              :title "Add a new client"
                              :body add-client-page/render
                              :on-save on-add-client})
      [:div.row
        [:div.col-md-5
          [search-bar clients]]
        [:div-col-md-4
          [add-client]]]
      [:hr]
      (when @search-result-limited
        [:div.alert.alert-info {:role "alert"}
          [:strong "Warning! "]
          "The search results are limited because of too many hits. Please, specify your query further to get a better result!"])
      (when any-clients
        [:div.row
          [:div.col-md-12
            [result-table clients]]])]))
