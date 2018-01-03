(ns vorpal.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [hiccup.core :refer [html]]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.java.jdbc :as db]
            [crypto.password.bcrypt :as password]
            [vorpal.layout :as layout]))


;; Sample database query
;; 
;; (for [s (db/query (env :database-url)
;;                   ["select content from sayings"])]
;;   (format "<li>%s</li>" (:content s)))

(defmacro root-page [id title f]
  `(defn ~id [& args#]
     (layout/application ~title (apply ~f args#))))

(defmacro page [id title body]
  `(root-page ~id ~title (fn [] (html ~body))))

(page splash "Vorpal" 
  [:div.main
    [:header#splash-header.central-header
      [:div.centered
        [:h1 "Vorpal"]
        [:h4 "Organizing Innovation"]]]
    [:form {:action "/signup"} 
     [:input {:type "submit" :value "Sign Up"}]]])

(page signup "Vorpal" 
    [:form {:action "/get-started" :method "post"}
     [:fieldset
      [:legend "Get started with vorpal:"]
       [:br]
       [:input {:type "text" :name "username" :placeholder "Username"}]
       [:br]
       [:input {:type "text" :name "password" :placeholder "Password"}]
       [:br]
       [:input {:type "text" :name "password" :placeholder "Password"}]
       [:br]
       [:input {:type "text" :name "address" :placeholder "Monero Address"}]
       [:br]
       [:input {:type "submit" :value "Submit"}]
       [:br]]])

;; (def encrypted (password/encrypt "test"))
;; (password/check "test" encrypted) ;; => true

(defn record [input]
  (db/insert! (env :database-url "postgres://localhost:5432/test")
              :sayings {:content input}))

;;(defn show []
  ;;(println (db/query (env :database-url "postgres://localhost:5432/test") ["select * from sayings"])))

(defn show []
  (println (db/query (env :database-url "postgres://localhost:5432/test") ["select content from sayings"])))

(defroutes app
  (GET "/test" {{input :input} :params}
       (record input)
       (splash))
  (GET "/signup" []
       (signup))
  (POST "/get-started" {params :params}
       (println params)
       (show)
       "You have signed up!")
  (GET "/" []
       (splash))
  (route/resources "/")
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
