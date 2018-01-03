(ns vorpal.layout
  (:use [hiccup.page :only (html5 include-css include-js)]))

(defn application [title & content]
  (html5 {:ng-app "vorpal" :lang "en"}
         [:head
          [:title title]
          (include-css "/style.css")
          [:body
           [:div {:class "container"} content ]]]))
