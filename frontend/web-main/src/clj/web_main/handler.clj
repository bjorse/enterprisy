(ns web-main.handler
  (:require [compojure.core :refer [GET POST PUT defroutes context]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [prone.middleware :refer [wrap-exceptions]]
            [environ.core :refer [env]]
            [web-main.data.clients-data :as clients-data]
            [web-main.data.workorders-data :as workorders-data]
            [web-main.data.todo-data :as todo-data]))

(def home-page
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     [:title "Enterprisy"]
     (include-css (if (env :dev) "css/bootstrap.css" "css/bootstrap.min.css"))
     (include-css (if (env :dev) "css/site.css" "css/site.min.css"))]
    [:body
      [:div.container
        [:div#app]]]
     (include-js (if (env :dev) "js/jquery-2.1.4.js" "js/jquery-2.1.4.min.js"))
     (include-js (if (env :dev) "js/bootstrap.js" "js/bootstrap.min.js"))
     (include-js "js/app.js")]))

(defroutes routes
  (GET "/" [] home-page)
  (wrap-json-response (wrap-json-body (context "/api" []
    (GET "/clients" [query] {:body (clients-data/filter-clients query)})
    (GET "/clients/:id" [id] {:body (clients-data/get-client (Integer/parseInt id))})
    (POST "/clients" {body :body} (clients-data/add-client! (:client body)))
    (GET "/workorders" [client-id] {:body (workorders-data/filter-workorders-by-client-id (Integer/parseInt client-id))})
    (GET "/workorders/:id" [id] {:body (workorders-data/get-workorder (Integer/parseInt id))})
    (POST "/workorders" {body :body} (workorders-data/add-workorder! (:workorder body)))
    (PUT "/workorders" {body :body} (workorders-data/update-workorder! (:workorder body)))
    (GET "/todo-items" [] {:body (todo-data/get-todo-items)})){:keywords? true}))
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults routes (merge site-defaults {:security {:anti-forgery false}}))]
    (if (env :dev) (wrap-exceptions handler) handler)))
