(ns weather-7.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [weather-7.core-test]))

(doo-tests 'weather-7.core-test)
