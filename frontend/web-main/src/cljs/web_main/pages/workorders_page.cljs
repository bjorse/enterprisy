(ns web-main.pages.workorders-page
  (:require [web-main.components.workorder-list :as workorder-list]
            [web-main.utils :as utils]))

(defn render [workorders]
  [:div
    [:div.page-header
      [:h3 [:span.glyphicon.glyphicon-wrench] " Filter on all available work orders"]]
    [workorder-list/render workorders]])
