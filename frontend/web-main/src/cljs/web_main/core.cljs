(ns web-main.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [web-main.dispatcher :as dispatcher]
              [web-main.store :as store]
              [web-main.menu :as menu]
              [web-main.pages.home-page :as home-page]
              [web-main.pages.clients-page :as clients-page]
              [web-main.pages.client-page :as client-page]
              [web-main.pages.todo-page :as todo-page]
              [web-main.pages.workorder-page :as workorder-page])
    (:import goog.History))

;; -------------------------
;; Views

(defn home-page []
  (home-page/render))

(defn clients-page []
  (clients-page/render store/clients))

(defn client-page []
  (client-page/render))

(defn todo-page []
  (todo-page/render store/todo-items))

(defn workorders-page []
  [:div
    [:h1 "Work orders!"]])

(defn workorder-page []
  (workorder-page/render store/current-workorder))

(defn current-page-name []
  (session/get :current-page-name))

(defn current-page []
  [:div
    [:div.col-md-2
      (menu/render (current-page-name))]
    [:div.col-md-10
      [:div.top-buffer
        [(session/get :current-page)]]]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page-name "home")
  (session/put! :current-page #'home-page))

(secretary/defroute "/clients" []
  (dispatcher/reset-current-client!)
  (session/put! :current-page-name "clients")
  (session/put! :current-page #'clients-page))

(secretary/defroute "/clients/:id" [id]
  (let [client-id (js/parseInt id)]
    (dispatcher/update-current-client! id)
    (session/put! :current-page-name "clients")
    (session/put! :current-page #'client-page)))

(secretary/defroute "/todo" []
  (session/put! :current-page-name "todo")
  (session/put! :current-page #'todo-page))

(secretary/defroute "/workorders" []
  (dispatcher/update-workorders!)
  (session/put! :current-page-name "workorders")
  (session/put! :current-page #'workorders-page))

(secretary/defroute "/workorders/:id" [id]
  (let [workorder-id (js/parseInt id)]
    (dispatcher/update-current-workorder! workorder-id)
    (session/put! :current-page-name "workorders")
    (session/put! :current-page #'workorder-page)))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (dispatcher/update-todo-items!)
  (hook-browser-navigation!)
  (mount-root))
