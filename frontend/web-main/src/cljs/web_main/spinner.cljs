(ns web-main.spinner)

(defn render [states]
  [:div.spinner-overlay {:class (when (empty? @states) "hidden")}])
