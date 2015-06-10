(defproject todo-data-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [org.clojure/data.json "0.2.6"]
                 [postgresql/postgresql "9.3-1102.jdbc41"]
                 [com.novemberain/langohr "3.2.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [ring/ring-json "0.3.1"]
                 [clj-time "0.9.0"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler todo-data-service.handler/app
         :port 3030}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
