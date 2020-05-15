(ns sparkfund.cli.style
  "Standard formats for CLI things"
  (:require [sparkfund.cli.color :as c]))

(def id (comp c/bold c/cyan))
(def env id)
(def git-branch-ok (comp c/bold c/magenta))
(def git-branch-bad (comp c/bold c/yellow))
(def path c/blue)
(def command c/cyan)
(def aside c/italic)
(def important (comp c/bold c/white))

(defn prepend-emoji
  [emoji & strs]
  (str emoji " " (apply str (flatten strs))))

(defn append-emoji
  [emoji & strs]
  (str (apply str (flatten strs)) " " emoji))

(defn wrap-with-emoji
  [emoji & strs]
  (->> (apply str (flatten strs))
       (prepend-emoji emoji)
       (append-emoji emoji)))

(def <!> "⚠️ ")
(def <tada> "🎉 🎉 🎉 ")
(def <input> "✏️")

(def fatal-banner (comp c/bold c/bg-red (partial wrap-with-emoji <!> "FATAL: ")))
(def error (comp c/bold c/red))
(def warn (comp c/bold c/yellow))
(def warn-banner (comp warn (partial wrap-with-emoji <!> "WARNING: ")))
(def danger-prompt (comp c/bold c/red (partial prepend-emoji <!>)))
(def success c/green)

(def added c/green)
(def changed c/yellow)
(def deleted c/red)
