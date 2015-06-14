(ns web-main.rest
  (:require [ajax.core :as ajax]
            [web-main.states :as states]))

(defn default-handler [response]
  (.log js/console (str response)))

(defn default-error-handler [{:keys [status status-text]}]
  (.log js/console (str "Bad HTTP request: " status " - " status-text)))

(def default-ops
  {:handler default-handler
   :error-handler default-error-handler
   :response-format :json
   :keywords? true})

(defn wrap-loading-state-handler [handler]
  (let [id (states/add-loading-state!)]
    (fn [response]
      (states/remove-loading-state! id)
      (handler response))))

(defn wrap-handler [handler]
  (fn [response]
    (handler (last response))))

(defn update-opts [opts]
  (merge default-ops opts))

(defn GET [url & [opts]]
  (let [updated-opts (update-opts opts)
        handler (:handler updated-opts)]
    (ajax/GET url (merge updated-opts {:handler (wrap-loading-state-handler handler)}))))

(defn POST [url opts]
  (let [new-opts (update-opts opts)]
    (ajax/ajax-request
      {:uri url
       :method :post
       :params (:params new-opts)
       :handler (wrap-loading-state-handler (wrap-handler (:handler new-opts)))
       :format (ajax/json-request-format)
       :response-format (ajax/json-response-format {:keywords? true})})))

(defn PUT [url opts]
  (let [new-opts (update-opts opts)]
    (ajax/ajax-request
      {:uri url
       :method :put
       :params (:params new-opts)
       :handler (wrap-loading-state-handler (wrap-handler (:handler new-opts)))
       :format (ajax/json-request-format)
       :response-format (ajax/json-response-format {:keywords? true})})))
