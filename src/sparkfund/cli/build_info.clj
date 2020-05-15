(ns sparkfund.cli.build-info
  "Provides tools for generating a map of standard build info."
  (:require [clojure.string :as str]
            [sparkfund.cli.shell :as shell])
  (:import (java.time Instant)))

(defn- trims
  [s]
  (when s
    (when-let [trimmed (str/trim s)]
      (when-not (empty? trimmed)
        trimmed))))

(defn- sh
  "Grabs output of command as a string, or nil if no output"
  [& command]
  (let [{:keys [out err exit]} (shell/sh! {:print-cmd? false, :print-out? false} command)]
    (when-not (zero? exit)
      (binding [*out* *err*]
        (println out)
        (println err)
        (flush))
      (throw (ex-info "Error retrieving build info" {:command command})))
    (trims out)))

(defn- env-var
  "Grabs an environment variable's value; returns `nil` if the value is
  empty or missing"
  [name]
  (trims (System/getenv name)))

(defn- string->int
  "Tries to parse string as integer; returns nil if parsing fails"
  [string]
  (try
    (Integer/parseInt string)
    (catch NumberFormatException e
      nil)))

(defn- common-info
  []
  (let [now (Instant/now)]
    {:info-format 3
     :utc-ms (.toEpochMilli now)
     :utc-print (.toString now)
     :git-rev (sh "git" "rev-parse" "HEAD")
     :git-branch (sh "git" "symbolic-ref" "--short" "HEAD")}))

(defn- env-specific-info
  []
  (if (env-var "CIRCLECI")
    {:build-ci? true
     :build-type :circle-ci
     :build-num (some-> (env-var "CIRCLE_BUILD_NUM") (string->int))
     :build-user (if-let [ci-user (env-var "CIRCLE_USERNAME")]
                   [:github ci-user]
                   [:local (sh "whoami")])}
    {:build-ci? false
     :build-type :ad-hoc
     :build-num 0
     :build-user [:local (sh "whoami")]}))

(defn build-info
  "Provides a map describing the current time and build environment.
  Be sparing in calling this in a tight loop because it can be a bit
  slow, especially the operations querying Git."
  []
  (merge
   (common-info)
   (env-specific-info)))
