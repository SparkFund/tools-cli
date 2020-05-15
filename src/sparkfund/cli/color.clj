(ns sparkfund.cli.color
  (:require [clojure.string :as str]))

(defn enabled?
  []
  (and (empty? (System/getenv "NO_COLOR"))
       (not (some (partial = "--no-color") *command-line-args*))))

(def sgr-codes
  "Standard SGR escape codes. From https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters"
  {:reset         0
   :bold          1
   :not-bold      22
   :italic        3
   :not-italic    23
   :underline     4
   :not-underline 24
   :blink         5
   :not-blink     25
   :inverse       7
   :not-inverse   27
   :black         30
   :red           31
   :green         32
   :yellow        33
   :blue          34
   :magenta       35
   :cyan          36
   :white         37
   :default       39
   :bg-black      40
   :bg-red        41
   :bg-green      42
   :bg-yellow     43
   :bg-blue       44
   :bg-magenta    45
   :bg-cyan       46
   :bg-white      47
   :bg-default    49})

(defn sgr-escape
  "Formats an SGR ANSI escape sequence. Accepts either a numeric SGR code,
  or one of the named keywords from the above map of sgr-codes."
  [style]
  (let [c (get sgr-codes style style)]
    (str "\u001b[" c "m")))


(defn rtrim-sgr-resets
  "Removes one or more SGR reset string literals (sgr 0) from the end of the string."
  [s]
  (let [reset (sgr-escape :reset)]
    (if (str/ends-with? s reset)
      (recur (subs s 0 (- (count s) (count reset))))
      s)))

(defn wrap
  "Wraps the given text with the given style.  If interior text happens to contain a string also
  produced by this function, it works the way you might hope, for example:

    (wrap :red 1 (wrap :blue 2) 3)

  Would return a string with a red '1', a blue '2', and a red '3'"
  [style & strings]
  (let [s (str/join (map str (flatten strings)))]
    (if (enabled?)
      (let [s (rtrim-sgr-resets s)
            escape (sgr-escape style)
            reset (sgr-escape :reset)]
        ;;TODO: instead of using the bludgeon of (sgr 0), what if we instead used more targeted reset codes (e.g. :default or :not-bold or :not-underline)
        (str escape
             (str/replace s reset (str reset escape))
             reset))
      s)))


;; Use these helpers!

(def bold (partial wrap :bold))
(def not-bold (partial wrap :not-bold))
(def italic (partial wrap :italic))
(def not-italic (partial wrap :not-italic))
(def underline (partial wrap :underline))
(def not-underline (partial wrap :not-underline))
(def blink (partial wrap :blink))
(def not-blink (partial wrap :not-blink))
(def inverse (partial wrap :inverse))
(def not-inverse (partial wrap :not-inverse))

(def black (partial wrap :black))
(def red (partial wrap :red))
(def green (partial wrap :green))
(def yellow (partial wrap :yellow))
(def blue (partial wrap :blue))
(def magenta (partial wrap :magenta))
(def cyan (partial wrap :cyan))
(def white (partial wrap :white))
(def default (partial wrap :default))

(def bg-black (partial wrap :bg-black))
(def bg-red (partial wrap :bg-red))
(def bg-green (partial wrap :bg-green))
(def bg-yellow (partial wrap :bg-yellow))
(def bg-blue (partial wrap :bg-blue))
(def bg-magenta (partial wrap :bg-magenta))
(def bg-cyan (partial wrap :bg-cyan))
(def bg-white (partial wrap :bg-white))
(def bg-default (partial wrap :bg-default))
