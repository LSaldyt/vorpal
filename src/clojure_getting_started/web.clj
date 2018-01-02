(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [hiccup.core :refer [html]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.java.jdbc :as db]
            ))

;;(defn splash []
;;  {:status 200
;;   :headers {"Content-Type" "text/plain"}
;;   :body "Hello from Heroku"})

;; (defn splash []
;;   {:status 200
;;    :headers {"Content-Type" "text/html"}
;;    :body (concat "<link rel=\"stylesheet\" href=\"style.css\">"
;;            (for [kind ["test"]]
;;                    (format "<a href=\"/%s?input=%s\">%s %s</a><br />"
;;                            kind "test" kind "test"))
;;                  ["<hr /><ul>"]
;;                  (for [s (db/query (env :database-url)
;;                                    ["select content from sayings"])]
;;                    (format "<li>%s</li>" (:content s)))
;;                  ["</ul>"])})

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (html [:span {:class "foo"} "bar"])})

(defn record [input]
  (db/insert! (env :database-url "postgres://localhost:5432/test")
              :sayings {:content input}))

(defroutes app
  (GET "/test" {{input :input} :params}
       (record input)
       (splash))
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
