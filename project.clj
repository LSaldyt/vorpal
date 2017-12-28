(defproject vorpal "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  ;;:license {:name "Eclipse Public License v1.0"
            ;;:url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [environ "1.0.0"]]
  ;;:plugins [[lein-ring "0.9.7"]]
  :plugins [[environ/environ.lein "0.3.1"]]
  :ring {:handler vorpal.handler/app}
  :hooks [environ.leiningen.hooks]
  :uberjar-name "clojure-getting-started-standalone.jar"
  :profiles {:production {:env {:production true}}})
;  :profiles
;  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
;                        [ring/ring-mock "0.3.0"]]}})
;
