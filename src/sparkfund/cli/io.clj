(ns sparkfund.cli.io
  (:import (java.io InputStream OutputStream ByteArrayOutputStream)))

(defn capturing-copy
  "Copies input stream to output stream, but captures the output as a string too."
  [^InputStream from, ^OutputStream to]
  (let [chunk 1024
        buffer (make-array Byte/TYPE chunk)
        log (ByteArrayOutputStream. chunk)]
    (loop []
      (let [size (.read from buffer)]
        (if (pos? size)
          (do (when log (.write log buffer 0 size))
              (when to (.write to buffer 0 size) (.flush to))
              (recur))
          (do (.flush log)
              (str log)))))))
