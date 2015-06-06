(ns web-main.pages.home-page)

(defn render []
  [:div
    [:div.jumbotron
      [:h2 "Welcome to Enterprisy!"]
      [:p "Please, satisfy your enterprisy needs in this application!"]]
    [:div.page-header
      [:h3 "Recent events"]]
    [:div.alert.alert-info [:strong "There are no recent events!"]]])
