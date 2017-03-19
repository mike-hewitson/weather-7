(ns weather-7.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [weather-7.services.home :as srv]))

(s/defschema Latest
    {:date s/Inst
     :readings
      [{:sunset s/Inst
         :cloud-cover s/Num
         :pressure s/Num
         :day-summary s/Str
         :wind-speed s/Num
         :sunrise s/Inst
         :icon s/Str
         :humidity s/Num
         :precip-intensity s/Num
         :wind-bearing s/Num
         :now-summary s/Str
         :wind-direction s/Str
         :temperature-max s/Num
         :precip-probability s/Num
         :location s/Str
         :temperature s/Num
         :week-summary s/Str}]})

(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Weather API"
                           :description "Weather data services"}}}}

  (context "/api" []
    :tags ["weather data retrieval"]

    (GET "/latest" []
      :return       Latest
      :query-params []
      :summary      "returns the current weather conditions"
      (ok (srv/format-home-page-data)))

    (POST "/minus" []
      :return      Long
      :body-params [x :- Long, y :- Long]
      :summary     "x-y with body-parameters."
      (ok (- x y)))

    (GET "/times/:x/:y" []
      :return      Long
      :path-params [x :- Long, y :- Long]
      :summary     "x*y with path-parameters"
      (ok (* x y)))

    (POST "/divide" []
      :return      Double
      :form-params [x :- Long, y :- Long]
      :summary     "x/y with form-parameters"
      (ok (/ x y)))

    (GET "/power" []
      :return      Long
      :header-params [x :- Long, y :- Long]
      :summary     "x^y with header-parameters"
      (ok (long (Math/pow x y))))))
