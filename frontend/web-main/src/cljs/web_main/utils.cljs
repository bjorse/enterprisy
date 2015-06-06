(ns web-main.utils)

(defn list-data [alist filter-text filter-function]
 (if-let [filter-text (some-> filter-text not-empty .toLowerCase)]
   (filter #(-> (filter-function %)
                .toLowerCase
                (.indexOf filter-text)
                (not= -1))
           alist)
   alist))
