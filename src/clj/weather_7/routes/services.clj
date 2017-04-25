(ns weather-7.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [weather-7.services.home :as srvh]
            [weather-7.services.summary :as srvs]
            [weather-7.services.history :as srvhis]))

(s/defschema Latest
  {:date s/Inst
   :readings
   [{:sunset s/Inst
     :day-summary s/Str
     :wind-speed s/Num
     :sunrise s/Inst
     :icon s/Str
     :wind-bearing s/Num
     :wind-direction s/Str
     :temperature-max s/Num
     :temperature-min s/Num
     :location s/Str
     :temperature s/Num
     :week-summary s/Str
     :moon-phase-icon s/Str
     :phase-of-moon s/Str
     :age-of-moon s/Num
     (s/optional-key :date) s/Inst
     (s/optional-key :dt) s/Num
     (s/optional-key :type) s/Str
     (s/optional-key :height) s/Num}]})

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

(s/defschema History
  [{:location s/Str
    :history
    [{:date s/Inst
      :temperature s/Num
      :wind-speed s/Num
      :location s/Str}]}])

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
      (ok (srvh/prepare-home-page-data)))

    (GET "/summary" []
      :return      Summary
      :query-params []
      :summary     "returns summary of weather conditions"
      (ok (srvs/prepare-summary-data)))

    (GET "/history" []
      :return      History
      :query-params []
      :summary     "returns history of weather conditions"
      (ok (srvhis/prepare-history-data)))))
