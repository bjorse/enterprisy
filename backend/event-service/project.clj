(defproject event-service "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/data.json "0.2.6"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.3.4"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [http-kit "2.1.18"]
                 [jarohen/chord "0.6.0"]
                 [com.novemberain/langohr "3.2.0"]]
  :main ^:skip-aot event-service.core
  :plugins [[lein-ring "0.8.13"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
