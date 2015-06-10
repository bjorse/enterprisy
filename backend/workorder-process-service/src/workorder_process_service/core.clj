(ns workorder-process-service.core
  (:require [workorder-process-service.process :as process]
            [workorder-process-service.queuing :as queing]))

(defn -main [& args]
  (queing/listen! process/handle-message))
