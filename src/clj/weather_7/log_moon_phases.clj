(ns weather-7.log-moon-phases
  (:require [clj-http.client       :as client]
            [clojure.tools.logging :as log]
            [cprop.core            :as cprop :refer [load-config]]
            [cprop.source          :as source]
            [monger.core           :as mg]
            [monger.collection     :as mc]))

; TODO move locations to database

(def moon-locations
  '(["Paradise Beach" "ZA/Jeffreys_Bay"]
    ["Sandton" "ZA/Sandton"]
    ["Salt River" "ZA/Salt_River"]))
    ; ["London" "UK/London"]))


(defonce now
  (new java.util.Date))


(defn get-moon-phase-data [search-string]
  "retrieve a set of readings from wunderorldtide.com for a gps location"
  (let [my-url (str
                "http://api.wunderground.com/api/1054d74a239c7946/astronomy/q/"
                search-string
                ".json")]
    (:body (client/get my-url {:as :json}))))


(defn build-moon-phases []
  (map (fn [[location search-string]]
         {:location location
          :phases (get-moon-phase-data search-string)})
       moon-locations))


(defn log-moon-phases []
  (let [uri (:database-url
             (cprop/load-config :merge
                          [(source/from-system-props)
                           (source/from-env)]))
        db (:db (mg/connect-via-uri uri))]
    (mc/insert db "moon" {:date now :locations (build-moon-phases)})))


(defn -main []
  (log-moon-phases)
  (log/info "Logged one set of moon phases")
  (System/exit 0))
