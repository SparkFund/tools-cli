(ns sparkfund.cli.io-test
  (:require [clojure.test :refer :all]
            [sparkfund.cli.io :as io]))

(deftest test-capturing-copy
  (let [text "text"]
    (with-open [from (java.io.ByteArrayInputStream. (.getBytes text))
                to (java.io.ByteArrayOutputStream.)]
      (is (= text
             (io/capturing-copy from to))
          "side effect and capture output")
      (is (= text
             (str to))
          "text was copied"))))
