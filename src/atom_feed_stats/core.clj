(ns atom-feed-stats.core
  (:require [atom-feed-stats.ggcrawler :as ggc]
            [java-time :as jt]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.xml :as xml])
  (:gen-class))

(defn -main [& args]
  (if (< (count args) 2)

    (doseq [s ["usage: <command> <google-group-forum-url> <optional-path-to-cookies>"
               " 0: google-group-forum-url:  For the 'archlinuxvn forum' it would be https://groups.google.com/forum/?_escaped_fragment_=forum/archlinuxvn"
               "                             For private enterprise SSO, it would be https://groups.google.com/a/my-enterprise.example/forum/?_escaped_fragment_=forum/foo-bar"
               " 1: no-of-pages-to-crawl:    Topics are returned 20 per page, 5 pages = 100 topics"
               " 2: optional-path-to-cookies:         If the group is private and if you use an enterprise SSO, you'll have a lot of cookies."
               " "
               "Try $> lein run https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4 2 "
               " "]]
      (println s))

    (ggc/crawl args)))
