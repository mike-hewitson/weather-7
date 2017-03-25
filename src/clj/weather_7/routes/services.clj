(ns weather-7.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [weather-7.services.home :as srvh]
            [weather-7.services.summary :as srvs]))

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

(s/defschema Summary
    [{:location s/Str
      :summary
         [{:date s/Inst
           :max-temp s/Num
           :avg-temp s/Num
           :max-wind s/Num
           :min-temp s/Num
           :count s/Num
           :min-wind s/Num
           :avg-wind s/Num}]}])

; TODO add tide data to schema

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
      (ok (srvh/format-home-page-data)))

    (POST "/summary" []
      :return      Summary
      :query-params []
      :summary     "returns summary of weather conditions"
      (ok (srvs/build-summary-data)))))
