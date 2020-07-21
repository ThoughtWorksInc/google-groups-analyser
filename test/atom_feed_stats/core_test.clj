(ns atom-feed-stats.core-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pp]
            [java-time :as jt]
            [atom-feed-stats.core :refer :all]
            [atom-feed-stats.ggatomparser :refer (->ThreadStat)]
            [atom-feed-stats.ggposts :refer (->PostSummary)]))

(deftest to-string
  (testing "should transform a PostSummary into csv format"
    (let [post (->PostSummary "post-id"
                              "topic-id"
                              "this is a title"
                              "author"
                              "2013-01-14"
                              "snippet"
                              "some-link")]
      (is (= "post-id, topic-id, this is a title, author, 2013-01-14, snippet, some-link"
             (to-str post)))))
  (testing "should transform ThreadStats into csv format"
    (let [threadstat (->ThreadStat "thread-id"
                                   "this is a title"
                                   "author"
                                   "email-count"
                                   "days"
                                   "unique contributors"
                                   ["email1" "email2"])]
      (is (= "thread-id, this is a title, author, email-count, days, unique contributors, [\"email1\" \"email2\"]"
             (to-str threadstat))))))