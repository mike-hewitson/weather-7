(ns weather-7.test.log-tides
  (:require [weather-7.log-tides :refer :all]
            ; [ring.mock.request :refer :all]
            [midje.sweet :refer :all]))

(facts "about 'get-worldtide-reading'"
       (let [tides (get-worldtide-data ["51.317" "0.057"])]
         (fact "it should return a map"
               (map? tides) => true)
         (fact "it should return the right data"
               (vector? (:extremes tides)) => true)
         (fact "it should return all of the correct sections"
               (string? (:date (first (:extremes tides)))) => true
               (string? (:type (first (:extremes tides)))) => true)))
