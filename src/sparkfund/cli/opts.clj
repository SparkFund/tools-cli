(ns sparkfund.cli.opts
  (:require [clojure.tools.cli :as tools.cli]
            [sparkfund.cli.util :as cli]
            [sparkfund.cli.style :as style]))

(defn parse-or-exit!
  "Parses the command line options per clojure.tools.cli If there are
  problems, or if the user has requested help via the `--help` flag,
  this prints problems and a usage summary, and immediately quits with
  a nonzero exit code.

  Defaults to parsing *command-line-args* but you could pass in any
  vec-of-strings that you'd like to parse as the second argument

  Accepts a vec of parser-opts which is provided directly to
  clojure.tools.cli/parse-opts For example: [:no-defaults true].
  parser-opts may be nil.

  For details on how to specify options see the clojure.tools.cli
  documentation at https://github.com/clojure/tools.cli#quick-start"
  ([options-specs] (parse-or-exit! options-specs *command-line-args*))
  ([options-specs command-line-args] (parse-or-exit! options-specs command-line-args nil))
  ([options-specs command-line-args parser-opts]
   (let [parsed (apply tools.cli/parse-opts command-line-args options-specs parser-opts)
         {:keys [options errors summary]} parsed
         help? (or (boolean (some (partial contains? #{"--help"}) command-line-args))
                   (:help options)
                   (:help? options))]
     (if (or help? errors)
       (do (doseq [error errors] (println (style/error "Error: " error)))
           (println summary)
           (cli/exit! 1))
       parsed))))
