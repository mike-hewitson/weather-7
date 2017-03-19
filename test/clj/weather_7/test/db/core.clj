(ns weather-7.test.db.core
  (:require [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [weather-7.db.core :as db]))

; TODO fix the deferable state error with testing db

; (facts "about 'get-latest'"
;   (let [data (first (db/get-latest))]
;     (fact "given nothing it should return a map"
;       (map? data) => true)
;     (fact "it should contain some correct data"
;       (:date data) => truthy
;       (:cloud-cover (first (:readings data))) => truthy
;       (:now-summary (first (:readings data))) => truthy)
;     (fact "it should contain three locations data"
;       (count (:readings data)) => 3)))

; (facts "about 'get-reading-at-time'"
;   (let [data (db/get-reading-at-time #inst "2017-03-05T13:49:57.796-00:00")
;         reading (first data)
;         readings (:readings reading)]
;     (fact "it should contain one reading"
;       (count data) => 1)
;     (fact "given nothing it should return a map"
;       (map? reading) => true)
;     (fact "it should contain some correct data"
;       (:date reading) => truthy
;       (:cloud-cover (first readings)) => truthy
;       (:now-summary (first readings)) => truthy)
;     (fact "it should contain three locations data"
;       (count readings) => 3)))
;
; (facts "about 'get-summary'"
;   (let [data (db/get-summary "Sandton")
;         first-record (first data)]
;     (fact "it should contain multiple records"
;       (> (count data) 1) => true)
;     (fact "it should return a sequence"
;       (seq? data) => true)
;     (fact "it should contain some correct data"
;       (:date (:_id first-record)) => truthy
;       (:count first-record) => truthy
;       (:avg-temp first-record) => truthy)))
