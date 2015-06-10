(ns workorder-todo-service.core
  (:require [workorder-todo-service.todo :as todo]
            [workorder-todo-service.queuing :as queuing]))

(defn -main [& args]
  (queuing/listen! todo/handle-message))
