(ns web-main.pages.home-page
  (:require [reagent.core :as reagent :refer [atom]]))

(defn event-row [event]
  (let [time (:event-time event)
        text (:text event)]
    [:div.well.well-sm
      [:span.label.label-primary time]
      [:samp.left-buffer text]]))

(defn show-event-list [events]
  [:div
    (for [event (reverse (sort-by :event-time events))]
      ^{:key (:id event)} [event-row event])])

(defn render [events]
  [:div
    [:div.jumbotron
      [:h2 "Welcome to Enterprisy!"]
      [:p "Please, satisfy your enterprisy needs in this application!"]]
    [:div.page-header
      [:h3 "Recent events"]]
    (if (empty? @events)
      [:div.alert.alert-info [:strong "There are no recent events!"]]
      [show-event-list @events])])
