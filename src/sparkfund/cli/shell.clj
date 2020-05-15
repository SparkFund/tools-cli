(ns sparkfund.cli.shell
  (:require [clojure.string :as str]
            [me.raynes.conch.low-level :as conch]
            [sparkfund.cli.io :as io]
            [sparkfund.cli.shell.quoting :as quoting]
            [sparkfund.cli.style :as style]))

(defn normalize-command
  "Flattens the command, separates & merges option maps, and removes nil
  values. This is to support flexible conditional construction of
  commands as detailed below in sh!"
  [& args]
  (let [args (filter some? (flatten args))]
    {:command (into [] (comp (remove map?) (map str)) args)
     :options (into {} (filter map?) args)}))

(defn human-readable-command
  "Takes a command and turns it into a string. This should correctly
  quote things that need quoting so that the human can see which
  argument is which."
  [& args]
  (let [{:keys [command]} (normalize-command args)]
    (str/join " " (map quoting/quote-bash-arg-if-needed command))))

(defn print-cmd!
  "Prints the command to stdout in a nicely styled hue to show what this program is doing."
  [& cmd]
  (->> cmd
       (human-readable-command)
       (style/command "[sh!] ")
       (println)))

(defn sh!
  "Runs a command and returns the result (stdout and stderr as strings, and exit code as a number).
  Normally you might use this like (sh! \"mkdir\" \"-p\" \"foo/bar/\").
  You can include one (or more) maps specifying options; non-map values are
  stringified and used as command arguments.

  Arguments are flattened and nil values are dropped so you can be
  fairly liberal in how you construct these args.  Example:

    (sh! \"imagemagick\" \"convert\" \"image.png\" (when-let [width ...] [\"--max-size\" width]) \"output.jpg\")

  would like one of these two values depending on whether or not width
  is defined:

    imagemagick convert image.png --max-size 1280 output.jpg
    imagemagick convert image.png output.jpg

  The options you may pass (and their defaults) are:

    {:on-error nil}
    {:on-error :throw}
    {:on-error :halt}
    Determines what to do if the command fails (defined as terminating with a nonzero exit code).
    The default (nil) does nothing. This fn returns a map describing the result, which includes the nonzero :exit code.
    Set to :throw to get a plain old exception when something goes wrong.
    Set to :halt to print a description of the problem and then immediate (System/exit).
    Halt is the spiritual equivalent to `set -e` in a bash script.

    {:print-cmd? true}
    Default is true. Should this print out the commands it runs before it runs them?  You'll
    probably want to set this to true for side-effectful commands, and false for idempotent ones.

    {:print-out? true}
    Default is true. Should the stdout of the program be shown as it is running? stdout is
    captured either way, but you can hide it if appropriate.

    {:dir nil}
    Allows you to specify a particular working dir for running the command (equivalent to `cd dir && command`)
    If you pass nil (default) the working directory is whatever the current working directory is.

    {:stdin nil}
    Allows you to feed a string to the stdin of the process. Default nil for no stdin.

    {:inherit-env? true}
    If true (the default), the command inherits the current environment variables along with the overrides you specify in :env
    If false/nil, the command only receives the environment variables you specify in :env

    {:env nil}
    {:env {\"K1\" \"V1\"}}
    Allows you to set env vars on the process.  Can either be nil (default) or a map of string->string
    to set specific environment variables when running the command (the command also receives all
    current environment variables unless you specify :clear-env?). These may override inherited env vars.
    To remove an inherited env var set its value to an empty string."

  [& args]
  (let [{:keys [options command]} (normalize-command args)
        {:keys [on-error print-cmd? print-out? dir inherit-env? env stdin]
         :or   {on-error nil, print-cmd? true, print-out? true, inherit-env? true, env nil, stdin nil}} options
        ;; pass through some options for conch.low-level/proc
        cmd (cond-> command
              (some? dir) (concat [:dir dir])
              (not inherit-env?) (concat [:clear-env true])
              (some? env) (concat [:env env]))]
    (when (contains? options :stop-on-error?)
      (throw (new IllegalArgumentException "Option :stop-on-error? was removed in v3.0.0, please use {:on-error :halt} instead!")))
    (when print-cmd? (print-cmd! cmd))
    (let [proc (apply conch/proc cmd)]
      (when (some? stdin)
        (if (string? stdin)
          (conch/feed-from-string proc stdin)
          (conch/feed-from proc stdin))
        (conch/done proc))
      (let [stdout (future (io/capturing-copy (:out proc) (when print-out? (System/out))))
            stderr (future (io/capturing-copy (:err proc) (when print-out? (System/err))))
            exit (conch/exit-code proc)
            result {:out @stdout, :err @stderr, :exit exit}]
        (if (or (zero? exit)
                (not on-error))
          result
          (let [ex (ex-info "Command failed with non-zero exit code" {:exit exit, :command command})]
            (case on-error
              :throw (throw ex)
              :halt (do (println ex)
                        (println (style/error "\nExiting"))
                        (flush)
                        (System/exit exit)))))))))
