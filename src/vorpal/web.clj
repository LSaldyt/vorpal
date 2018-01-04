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
            [vorpal.layout :as layout])
  (:import  [stellar-sdk.org.stellar.sdk]))

;; Sample database query
;; 
;; (for [s (db/query (env :database-url)
;;                   ["select content from sayings"])]
;;   (format "<li>%s</li>" (:content s)))

(def +title+ "Vorpal")
(def +subtitle+ "Organizing Innovation")

(defmacro root-page [id f]
  `(defn ~id [& args#]
     (layout/application +title+ (apply ~f args#))))

(defmacro page [id body]
  `(root-page ~id (fn [] (html ~body))))

(page splash 
  [:div.main
    [:header#splash-header.central-header
      [:div.centered
        [:h1 +title+]
        [:h4 +subtitle+]]]
    [:form {:action "/signup"} 
     [:input {:type "submit" :value "Sign Up"}]]
    [:form {:action "/login"} 
     [:input {:type "submit" :value "Login"}]]])

(page signup 
    [:form {:action "/get-started" :method "post"}
     [:fieldset
      [:legend (str "Get started with " +title+ ":")]
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

(page login 
    [:form {:action "/user" :method "post"}
     [:fieldset
      [:legend (str "Log in to " +title+ ":")]
       [:br]
       [:input {:type "text" :name "username" :placeholder "Username"}]
       [:br]
       [:input {:type "text" :name "password" :placeholder "Password"}]
       [:br]
       [:input {:type "submit" :value "Submit"}]
       [:br]]])

(def db-url "postgres://localhost:5432/test")
(def db-spec (env :database-url db-url))

(defn add-user [id password address]
  (db/insert! db-spec
              :users {:id id :crypt (password/encrypt password) :address address}))

(defn get-address [id]
  (db/query db-spec [(str "select address from users where id='" id "'")]))

(defn get-crypt [id]
  (:crypt (first (db/query db-spec [(str "select crypt from users where id='" id "'")]))))

(defn authenticated? [id pass]
  (let [crypt (get-crypt id)]
    (println "stored:")
    (println (type crypt))
    (println crypt)
    (println "pass:")
    (println pass)
    (println "checks:")
    (println (password/check pass crypt))
    (if (nil? crypt)
        false
        (password/check pass crypt))))

(defroutes app
  (GET "/signup" []
       (signup))
  (POST "/get-started" {{username :username pass :password address :address} :params}
        (do
          (println (get-address username))
          (if (not (get-address username))
            (add-user username (first pass) address))
         "You have signed up!"))
  (GET "/login" []
       (login))
  (POST "/user" {{username :username pass :password} :params}
        (if (authenticated? username pass)
           (str "Login success")
           (str "Login failure")))
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
