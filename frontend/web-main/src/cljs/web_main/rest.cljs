(ns web-main.rest
  (:require [ajax.core :as ajax]))

(defn default-handler [response]
  (.log js/console (str response)))

(defn default-error-handler [{:keys [status status-text]}]
  (.log js/console (str "Bad HTTP request: " status " - " status-text)))

(def default-ops
  {:handler default-handler
   :error-handler default-error-handler
   :response-format :json
   :keywords? true})

(defn wrap-handler [handler]
  (fn [response]
    (handler (last response))))

(defn update-opts [opts]
  (merge default-ops opts))

(defn GET [url & [opts]]
  (ajax/GET url (update-opts opts)))

(defn POST [url opts]
  (let [new-opts (update-opts opts)]
    (ajax/ajax-request
      {:uri url
       :method :post
       :params (:params new-opts)
       :handler (wrap-handler (:handler new-opts))
       :format (ajax/json-request-format)
       :response-format (ajax/json-response-format {:keywords? true})})))

(defn PUT [url opts]
  (ajax/PUT url (update-opts opts)))
