(ns sparkfund.cli.prompts
  (:require [clojure.string :as string]
            [sparkfund.cli.style :as style]
            [sparkfund.cli.util :as cli]))

(defn prompt!
  [prompt]
  (print (str prompt " " style/<input> "  "))
  (flush)
  (read-line))

(defn choice
  "Prompts the user to type something, typically one of a few short
  values.  Coerces the user input case-insensitively based on the
  provided mapping, and automatically prompts the user again if they
  don't input one of the provided values.

  This doesn't directly present the options to the user; instead, you
  should print that out before prompting them.  If these options are
  short, such as in `yes-no` below, you can easily bake them into the
  prompt string itself

  If you'd like to provide a default value for when the user doesn't
  enter anything, use the empty string as the key in your map."
  [prompt m]
  (let [r (some-> (prompt! prompt) (string/trim) (string/lower-case))]
    (if (contains? m r)
      (get m r)
      (do (println "expecting one of:" (keys (dissoc m "")))
          (recur prompt m)))))

(defn yes-no
  "Asks the user to answer a yes or no question.  Optionally takes a
  second argument so the user can just hit enter to accept the
  default.  If a default is not provided the user is harangued to
  provide a definitive answer."
  ([prompt]
   (let [m {"y"   true
            "yes" true
            "n"   false
            "no"  false}]
     (choice (str prompt "  [y/n]") m)))
  ([prompt default]
   (let [m {"y"   true
            "yes" true
            ""    default
            "n"   false
            "no"  false}]
     (choice (str prompt "  " (if default "[Y/n]" "[y/N]")) m))))


(defn keyword-or-quit!
  "Asks the user to enter in a specific case-insensitive word at the prompt.
  If the user fails to do so, this immediately quits. This is a useful
  helper in scripts, where you might want to display a summary to a
  user and ask them to confirm a dangerous option (for example,
  'please type DEPLOY TO PRODUCTION' to continue:')"
  ([keyword]
   (let [prompt (format "Please type %s to continue:" (style/warn keyword))
         resp (some-> (prompt! prompt) (string/trim) (string/lower-case))]
     (if (not= resp (string/lower-case keyword))
       (cli/exit! 2 "User did not correctly acknowledge, aborting.")
       (println))))
  ([keyword preamble]
   (println preamble)
   (keyword-or-quit! keyword)))


(defn file
  "Asks the user to specify the path to a file."
  [prompt relative-dir]
  (let [file (some->> (prompt! prompt) (string/trim) (str relative-dir))]
    (if (and file (cli/file-exists? file))
      file
      (do (println "Missing or unreadable file:" (style/path file))
          (recur prompt relative-dir)))))
