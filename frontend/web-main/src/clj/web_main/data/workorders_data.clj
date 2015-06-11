(ns web-main.data.workorders-data
  (:require [clj-http.client :as client]
            [web-main.util :as util]))

(def workorder-data-service-url "http://localhost:3020/workorders")

(defn get-workorder [id]
  (let [url (str workorder-data-service-url "/" id)]
    (util/extract-body (client/get url))))

(defn filter-workorders-by-client-id [client-id]
  (util/extract-body (client/get workorder-data-service-url {:query-params {:client-id client-id} :accept :json})))

(defn add-workorder! [workorder]
  (println (str "Adding workorder: " workorder))
  (let [result (client/post workorder-data-service-url {:body (util/convert-map-to-json {:workorder workorder})
                                                        :content-type :json
                                                        :accept :json
                                                        :throw-exceptions false})]
    {:status (:status result) :body (util/extract-body result)}))

(defn update-workorder! [workorder]
  (println (str "Updating workorder: " (util/remove-nil-values workorder)))
  (let [clean-workorder (util/remove-nil-values workorder)
        result (client/put workorder-data-service-url {:body (util/convert-map-to-json {:workorder clean-workorder})
                                                       :content-type :json
                                                       :accept :json
                                                       :throw-exceptions false})]
    {:status (:status result) :body (util/extract-body result)}))
