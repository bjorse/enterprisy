(ns web-main.states
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-uuid-utils.core :as uuid]))

(def loading-states (atom []))

(defn add-loading-state! []
  (let [id (uuid/uuid-string (uuid/make-random-uuid))]
    (reset! loading-states (conj @loading-states id))
    id))

(defn remove-loading-state! [id]
  (reset! loading-states (filter #(not= id %) @loading-states)))
