(ns sparkfund.cli.style-test
  (:require [clojure.test :refer :all]
            [sparkfund.cli.style :as style]))

(deftest test-wrap-with-emoji
  (is (= "🥞 somestringsaboutpancakes 🥞"
         (style/wrap-with-emoji "🥞" "some" "strings" "about" "pancakes"))))
