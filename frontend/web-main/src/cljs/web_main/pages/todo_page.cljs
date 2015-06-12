(ns web-main.pages.todo-page
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [web-main.utils :as utils]))

(defn create-link [{:keys [type type-id]}]
  (case type
    "workorder" (str "#/workorders/" type-id)
    nil))

(defn create-link-text [{:keys [type type-id]}]
  (case type
    "workorder" (str "Workorder #" type-id)
    nil))

(defn render-todo-item [todo-item]
  (let [todo-link (create-link todo-item)
        link-text (create-link-text todo-item)
        priority (:priority todo-item)
        color (utils/get-priority-color priority)]
    [:div.alert {:class (str "alert-" color)}
      [:h3.reduce-top-margin (:description todo-item)]
      [:div.big
        [:a {:href todo-link} link-text]
        [:span.left-buffer-sm "- " (:title todo-item)]]
      [:div.small.top-buffer
        [:span.label {:class (str "label-" (utils/get-priority-color priority))}
          [:span.glyphicon {:class (utils/get-priority-icon priority)}]
          [:span.left-buffer-sm (utils/get-priority-text priority)]]
        [:span.text-muted.left-buffer (str "Added " (:added todo-item))]]]))

(defn render [todo-items]
  (let [any-todo-items? (pos? (count @todo-items))]
    [:div
      [:div.page-header
        [:h3 [:span.glyphicon.glyphicon-tasks] " All todo items sorted on priority"]]
      (when any-todo-items?
        (for [todo-item (reverse (sort-by :priority @todo-items))]
          ^{:key (:id todo-item)} [render-todo-item todo-item]))
      (when (not any-todo-items?)
        [:div.big
          [:em.text-muted "There are currently no todo items in this list..."]])]))
