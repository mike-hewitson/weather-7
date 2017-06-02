(ns weather-7.routes.home
  (:require [weather-7.layout :as layout]
            [compojure.core   :as compojure :refer [defroutes GET]]))

(defn home-page []
  (layout/render "home.html"))

(compojure/defroutes home-routes
  (compojure/GET "/" []
    (home-page)))
