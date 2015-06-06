(ns web-main.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [web-main.store :as store]
              [web-main.menu :as menu]
              [web-main.pages.home-page :as home-page]
              [web-main.pages.clients-page :as clients-page]
              [web-main.pages.client-page :as client-page]
              [web-main.data.clients-data :as clients-data]
              [web-main.data.workorders-data :as workorders-data])
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
  [:div
    [:h3 "This is where the todo items will go!"]])

(defn workorders-page []
  [:div
    [:h1 "Work orders!"]])

(defn current-page-name []
  (session/get :current-page-name))

(defn current-page []
  [:div
    [:div.col-md-2
      (menu/render (current-page-name))]
    [:div.col-md-10
      [:div.top-buffer
        [(session/get :current-page)]]]])

(defn reset-current-client! []
  (reset! store/current-client nil)
  (reset! store/current-client-workorders []))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page-name "home")
  (session/put! :current-page #'home-page))

(secretary/defroute "/clients" []
  (reset-current-client!)
  (session/put! :current-page-name "clients")
  (session/put! :current-page #'clients-page))

(secretary/defroute "/clients/:id" [id]
  (let [client-id (js/parseInt id)]
    (reset-current-client!)
    (workorders-data/get-workorders-for-client client-id #(reset! store/current-client-workorders %))
    (clients-data/get-client client-id #(reset! store/current-client %))
    (session/put! :current-page-name "clients")
    (session/put! :current-page #'client-page)))

(secretary/defroute "/todo" []
  (session/put! :current-page-name "todo")
  (session/put! :current-page #'todo-page))

(secretary/defroute "/workorders" []
  (workorders-data/get-workorders #(reset! store/workorders))
  (session/put! :current-page-name "workorders")
  (session/put! :current-page #'workorders-page))

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
  (hook-browser-navigation!)
  (mount-root))
