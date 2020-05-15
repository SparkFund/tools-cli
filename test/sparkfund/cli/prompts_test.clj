(ns sparkfund.cli.prompts-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [sparkfund.cli.prompts :as prompts]))

(deftest test-prompt!
  (is (string/includes? 
       (with-out-str
         (with-in-str "n"
           (prompts/prompt! "text")))
       "text"))
  (is (string/includes?
       (with-out-str
         (with-in-str "n\ny\n"
           (prompts/choice "text" {"y" "ok"})))
       "expecting one of: (y)"))
  (is (= "ok"
         (with-in-str "n\ny\n"
           (prompts/choice "text" {"y" "ok"}))))
  (is (string/includes?
       (with-out-str
         (with-in-str "n"
           (prompts/yes-no "text" true)))
       "[Y/n]"))
  (is (with-out-str
        (with-in-str "delete-production"
          (prompts/keyword-or-quit! "delete-production"))))
  ;; dont test the unhappy case, it exits
  (is (= "test/sparkfund/cli/prompts_test.clj"
         (with-in-str "invalid\nprompts_test.clj"
           (prompts/file "text" "test/sparkfund/cli/")))))
