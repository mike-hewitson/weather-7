(ns weather-7.test.services.home
  (:require [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [weather-7.services.home :as r]
            [weather-7.test.fixtures :as fix]))

(facts "about 'get-direction'"
       (fact "given some bearings, its should return their direction"
             (r/get-direction 0) => "Northerly"
             (r/get-direction 45) => "North-easterly"
             (r/get-direction 44.9) => "North-easterly"
             (r/get-direction 50) => "North-easterly"
             (r/get-direction 359) => "Northerly"))

(facts "about 'format-readings-for-merge'"
       (let [readings (r/format-readings-for-merge fix/latest-reading)]
         (fact "it should return a map"
               (map? readings) => true)
         (fact "it should contain 3 items"
               (count readings) => 3)
         (fact "elements should contain 10 items"
               (count (val (first readings))) => 10)))

(facts "about 'create-directions-for-merge'"
       (let [directions (r/create-directions-for-merge fix/latest-reading)]
         (fact "it should return a map"
               (map? directions) => true)
         (fact "it should contain 3 items"
               (count directions) => 3)
         (fact "elements should contain 1 items"
               (count (val (first directions))) => 1)
         (fact "it should contain wind-direction"
               (:wind-direction (val (first directions))) => truthy)
         (fact "it should contain some correct data"
               (string? (:wind-direction (val (first directions)))) => true)))

; (facts "about 'create-next-tide-list'"
;   (let [tides (r/create-next-tide-list fix/tides-data)]
;     (fact "it should return a map"
;      (map? tides) => true)
;     (fact "it should contain 2 items"
;      (count tides) => 2)
;     (fact "elements should contain 4 items"
;      (count (val (first tides))) => 4)
;     (fact "it should contain a date field"
;       (:date (val (first tides))) => truthy)
;     (fact "it should contain some correct data"
;       (string? (:type (val (first tides)))) => true)))
