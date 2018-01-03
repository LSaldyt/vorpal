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
     [:input {:type "submit" :value "Sign Up"}]]
    [:form {:action "/login"} 
     [:input {:type "submit" :value "Login"}]]])

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

(defn add-user [id password address]
  (db/insert! (env :database-url "postgres://localhost:5432/test")
              :users {:id id :crypt (password/encrypt password) :address address}))

(defn show []
  (println (db/query (env :database-url "postgres://localhost:5432/test") ["select * from users"])))

(defn show-address [id]
  (println (db/query (env :database-url "postgres://localhost:5432/test") [(str "select address from users where id='" id "'")])))

(defroutes app
  (GET "/signup" []
       (signup))
  (POST "/get-started" {{username :username pass :password address :address} :params}
        (if (not (= () (show-address username)))
          (add-user username (password/encrypt (first pass)) address))
       ;;(println params)
       (show-address "not-present")
       ;;(add-user "test" "mctest" "@test")
       (show)
       (show-address "test")
       "You have signed up!")
  (GET "/login" []
       (str "Login success"))
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
