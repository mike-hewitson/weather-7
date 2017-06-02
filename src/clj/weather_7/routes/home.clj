(ns weather-7.routes.home
  (:require [weather-7.layout :as layout]
            [compojure.core   :refer [defroutes GET]]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" []
    (home-page)))
