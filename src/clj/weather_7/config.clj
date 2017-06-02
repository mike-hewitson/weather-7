(ns weather-7.config
  (:require [cprop.core   :as cprop :refer [load-config]]
            [cprop.source :as source]
            [mount.core   :as mount :refer [args defstate]]))


(mount/defstate env :start (cprop/load-config)
                      :merge
                      [(mount/args)
                       (source/from-system-props)
                       (source/from-env)])
