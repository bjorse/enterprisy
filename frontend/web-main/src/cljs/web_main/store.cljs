(ns web-main.store
  (:require [reagent.core :as reagent :refer [atom]]))

(def clients (atom []))

(def current-client-id (atom nil))

(def current-client (atom nil))

(def current-client-workorders (atom []))

(def workorders (atom []))

(def current-workorder (atom nil))

(def todo-items (atom []))

(def events (atom []))
