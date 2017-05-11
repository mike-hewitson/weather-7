(ns weather-7.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [weather-7.core :as rc]
            [weather-7.pages.home :as ph]
            [weather-7.fixtures :as fix]))

(deftest test-days-to-next-spring
  (is (= (ph/days-to-next-spring 0) 14))
  (is (= (ph/days-to-next-spring 5) 9))
  (is (= (ph/days-to-next-spring 14) 14))
  (is (= (ph/days-to-next-spring 15) 13)))

(deftest test-moon-phase-text
  (is (= (ph/format-moon-phase-text 0) "new moon"))
  (is (= (ph/format-moon-phase-text 5) "9 days to new moon"))
  (is (= (ph/format-moon-phase-text 14) "full moon"))
  (is (= (ph/format-moon-phase-text 1) "13 days to new moon"))
  (is (= (ph/format-moon-phase-text 15) "13 days to full moon"))
  (is (= (ph/format-moon-phase-text 22) "6 days to full moon")))

(deftest test-moon-phase-text-with
 (let [reading-element (ph/create-reading-element
                        (first (:readings fix/home-page-data)))]
  (is (= true (vector? reading-element)))
  (is (= 3 (count reading-element)))
  (is (= 5 (count (second reading-element))))))
