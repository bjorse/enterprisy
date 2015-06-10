(ns workorder-process-service.core
  (:require [workorder-process-service.process :as process]
            [workorder-process-service.queuing :as queuing]))

(defn -main [& args]
  (queuing/listen! process/handle-message))
