(defproject vorpal "1.0.0-SNAPSHOT"
  :description "Demo Clojure web app"
  :url "http://clojure-getting-started.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.4.0"]
                 [crypto-password "0.2.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [environ "1.0.0"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.postgresql/postgresql "9.4-1201-jdbc4"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "vorpal-standalone.jar"
  :main vorpal.web
  :profiles {:production {:env {:production true}}})
