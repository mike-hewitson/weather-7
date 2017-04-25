(ns weather-7.log-data
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [clojure.math.numeric-tower :as m]
            [monger.core :as mg]
            [monger.collection :as mc]))

(def icons-transform
  {"day-sunny" "wi-day-sunny"
   "clear-night" "wi-night-clear"
   "rain" "wi-rain"
   "wind" "wi-strong-wind"
   "snow" "wi-snow"
   "sleet" "wi-sleet"
   "strong-wind" "wi-wind"
   "fog" "wi-fog"
   "cloudy" "wi-cloudy"
   "day-cloudy" "wi-day-cloudy"
   "partly-cloudy-day" "wi-day-sunny-overcast"
   "night-cloudy" "wi-night-cloudy"
   "partly-cloudy-night" "wi-night-partly-cloudy"
   "hail" "wi-hail"
   "thunderstorm" "wi-thunderstorm"
   "tornado" "wi-tornado"
   "clear-day" "wi-day-sunny"});

(defn float-and-round
  "cast to float after rounding to one decimal"
  [x]
  (float (/ (m/round (* x 10)) 10)))

(def reading-names
  [[["week-summary"]
    ["temperature-max" float-and-round]
    ["temperature-min" float-and-round]
    ["sunrise" (fn [x] (java.util.Date. (* 1000 x)))]
    ["sunset" (fn [x] (java.util.Date. (* 1000 x)))]
    ["day-summary"]
    ["now-summary"]
    ["icon" icons-transform]
    ["temperature" float-and-round]
    ["wind-speed" (fn [x] (float-and-round (* x 3.6)))]
    ["wind-bearing" long]
    ["pressure" float-and-round]
    ["humidity" float]
    ["precip-probability" float]
    ["precip-intensity" float]
    ["cloud-cover" float]]
   [[:daily :summary]
    [:data :temperatureMax]
    [:data :temperatureMin]
    [:data :sunriseTime]
    [:data :sunsetTime]
    [:data :summary]
    [:currently :summary]
    [:currently :icon]
    [:currently :temperature]
    [:currently :windSpeed]
    [:currently :windBearing]
    [:currently :pressure]
    [:currently :humidity]
    [:currently :precipProbability]
    [:currently :precipIntensity]
    [:currently :cloudCover]]])

(def locations
  '(["London" "51.317,0.057"]
    ["Sandton" "-26.097,28.053"]
    ["Paradise Beach" "-34.0521,24.5412"]))

(defn get-darksky-data
  "retrieve a set of readings from darksky.io for a gps location"
  [gps]
  (let [my-url
        (str
         "https://api.darksky.net/forecast/62888a9ff1907377b60a866701cf3338/"
         gps
         "?units=si&exclude=minutely,hourly,alerts,flags")]
    (:body (client/get my-url {:as :json}))))

(defn extract-reading-data
  "extract the required data elements from the darksky message body"
  [body]
  (zipmap (first reading-names)
          (map (fn [[k v]]
                 (let [data (v (first (:data (:daily body))))]
                   (cond
                     (= k :daily) (v (:daily body))
                     (= k :data) data
                     (= k :currently) (v (:currently body))
                     :else nil)))
               (last reading-names))))

; TODO this feels wrong, need to refactor
(defn create-update
  "create a hash that contains data for a location"
  [location reading-map]
  (assoc (apply merge (map (fn [[[name & [my-cast]] value]]
                             {name (if my-cast
                                     (my-cast value)
                                     value)})
                           reading-map))
         "location" location))

(defn build-readings
  []
  (map (fn [[location gps]]
         (->> (get-darksky-data gps)
              (extract-reading-data)
              (create-update location)))
       locations))

(def now
  (new java.util.Date))

(defn log-readings []
  (let [uri (:database-url
             (load-config :merge
                          [(source/from-system-props)
                           (source/from-env)]))
        db (:db (mg/connect-via-uri uri))]
    (mc/insert db "readings" {:date now :readings (build-readings)})))

(defn -main []
  (log-readings)
  (log/info "Logged one set of readings")
  (System/exit 0))
