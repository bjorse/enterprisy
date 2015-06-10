(defproject workorder-process-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.7"]
                 [org.clojure/data.json "0.2.6"]
                 [postgresql/postgresql "9.3-1102.jdbc41"]
                 [com.novemberain/langohr "3.2.0"]
                 [clj-time "0.9.0"]]
  :main ^:skip-aot workorder-process-service.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
