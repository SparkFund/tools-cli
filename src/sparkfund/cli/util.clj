(ns sparkfund.cli.util
  "misc cli helper functions"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [sparkfund.cli.shell :as shell]
            [sparkfund.cli.style :as style]))

(defn exit!
  ([] (exit! 0))
  ([status] (exit! status nil))
  ([status message]
   (when-not (empty? message)
     (let [dye (if (zero? status) style/important style/error)]
       (println (dye "\n------------------------------------\n" message "\n"))))
   (System/exit status)))

(defn dir-exists?
  [path]
  (let [f (io/file path)]
    (and (.exists f) (.isDirectory f))))

(defn file-exists?
  [path]
  (let [f (io/file path)]
    (and (.exists f) (.isFile f))))

(defn git-branch
  "An example helper that shells out to figure out the current git branch"
  []
  (some->> ["git" "symbolic-ref" "--short" "HEAD"]
           (shell/sh! {:print-cmd? false, :print-out? false})
           (:out)
           (str/trim)))
