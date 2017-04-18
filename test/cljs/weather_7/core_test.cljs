(ns weather-7.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [weather-7.core :as rc]
            [weather-7.pages.home :as ph]))

(deftest test-days-to-next-spring
  (is (= (ph/days-to-next-spring 0) 14))
  (is (= (ph/days-to-next-spring 5) 9))
  (is (= (ph/days-to-next-spring 14) 14))
  (is (= (ph/days-to-next-spring 15) 13)))

(deftest test-moon-phase-text
  (is (= (ph/format-moon-phase-test 0) "full moon"))
  (is (= (ph/format-moon-phase-test 5) "9 days to full moon"))
  (is (= (ph/format-moon-phase-test 14) "new moon"))
  (is (= (ph/format-moon-phase-test 1) "13 days to full moon"))
  (is (= (ph/format-moon-phase-test 15) "13 days to new moon"))
  (is (= (ph/format-moon-phase-test 22) "6 days to new moon")))
