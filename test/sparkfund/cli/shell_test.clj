(ns sparkfund.cli.shell-test
  (:require [clojure.test :refer :all]
            [sparkfund.cli.shell :as shell]))

(deftest test-normalize-command
  (is (= {:command ["a" "b" "c" "f"]
          :options {:d "e" :g "h"}}
         (shell/normalize-command "a" "b" nil "c" {:d "e"} "f" {:g "h"}))))
