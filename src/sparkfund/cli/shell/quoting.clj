(ns sparkfund.cli.shell.quoting
  (:require [clojure.string :as str])
  (:import (java.util.regex Pattern)))

(def quotable-chars (str/split "$`! \r\n\t\"\\<>$;()" #""))
(def quotable-chars-regex (re-pattern (str/join "|" (map #(Pattern/quote %) quotable-chars))))

(def substitutions
  "Only works for strings that are part of quotable-chars"
  {"$"  "\\$"
   "`"  "\\`"
   "!"  "\\!"
   "\r" "\\r"
   "\n" "\\n"
   "\"" "\\\""
   "\\" "\\\\"})

(defn needs-quoting
  [s]
  (boolean (re-find quotable-chars-regex s)))

(defn quote-bash-arg-if-needed
  "Double-quotes a value for bash, but only if needed.  Uses the
  double-quoting conventions described in
  https://www.gnu.org/software/bash/manual/html_node/Double-Quotes.html
  and assumes that ! could be active"
  [s]
  (if (needs-quoting s)
    (str \" (str/replace s quotable-chars-regex #(get substitutions % %)) \")
    s))
