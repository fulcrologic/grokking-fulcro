(ns demo.app-macros)

(defmacro ! [& body]
  `(try
     ~@body
     (catch :default ~'_)))
