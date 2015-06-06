(ns web-main.menu
  (:require [web-main.store :as store]))

(defn menu-item [{:keys [url icon text active count]}]
  [:li {:role "presentation" :class (when active "active")}
    [:a {:href url} [:span.glyphicon {:class icon}] [:span.left-buffer text]
      (when (pos? count) [:span.badge count])]])

(defn render [active-page]
  (let [active? #(= % active-page)]
    [:div.fixed
      [:div.row
        [:img.displayed {:src "/images/enterprisy-logo.png" :height 150 :width 190}]]
      [:div.row
        [:ul.nav.nav-pills.nav-stacked.top-buffer
          (menu-item {:url "#" :icon "glyphicon-home" :text "Home" :active (active? "home") :count 0})
          (menu-item {:url "#/todo" :icon "glyphicon-tasks" :text "Todo items" :active (active? "todo") :count (count @store/todo-items)})
          (menu-item {:url "#/workorders" :icon "glyphicon-wrench" :text "Work orders" :active (active? "workorders") :count 0})
          (menu-item {:url "#/clients" :icon "glyphicon-user" :text "Clients" :active (active? "clients") :count 0})]]]))
