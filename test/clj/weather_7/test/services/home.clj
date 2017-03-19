(ns weather-7.test.services.home
  (:require [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [weather-7.services.home :as r]
            [weather-7.test.fixtures :as fix]))


(facts "about 'get-direction'"
    (fact "given some bearings, its should return their direction"
      (r/get-direction 0) => "N"
      (r/get-direction 45) => "NE"
      (r/get-direction 44.9) => "NE"
      (r/get-direction 50) => "NE"
      (r/get-direction 359) => "N"))


(facts "about 'create-map-for-template'"
       (fact "given a specific input, it should produce the expected output"
             (r/create-map-for-template fix/latest-reading) => fix/home-page-data))
