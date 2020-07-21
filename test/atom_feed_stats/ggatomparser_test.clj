(ns atom-feed-stats.ggatomparser-test
  (:require [clojure.test :refer :all]
            [java-time :as jt]
            [atom-feed-stats.ggatomparser :refer :all]
            [atom-feed-stats.ggposts :refer (->PostSummary)]
            [clojure.pprint :as pp]))

(deftest post-processing
  (testing "find newest and oldest entries"
    (let [oldest (jt/local-date "2013-01-29")
          newest (jt/local-date "2020-06-16")
          entry1 (->PostSummary "foo" "some-id" "[DEAL REVIEW] This Deal" "Chris" oldest "Hi All, Here's a project." "link")
          entry2 (->PostSummary "bar" "some-id" "[DEAL REVIEW] This Deal" "Tara" (jt/local-date "2014-02-28") "Hi All, Here's a project." "link")
          entry3 (->PostSummary "baz" "some-id" "[DEAL REVIEW] This Deal" "Rand" newest "Hi All, Here's a project." "link")
          entries (list entry1 entry2 entry3)]
      (is (= (jt/min newest oldest) oldest))
      (is (= (oldest-entry-instant entries) oldest))
      (is (= (newest-entry-instant entries) newest))))
  (testing "group entries by email"
    (let [thread [{:post-id    "foo",
                   :topic-id   "some-id2",
                   :title      "[DEAL REVIEW] That Deal",
                   :author     "Tara",
                   :date       "2020-06-08",
                   :snippet    "Hi All, Here's a project.",
                   :email-link "link"}
                  {:post-id    "bar",
                   :topic-id   "some-id2",
                   :title      "[DEAL REVIEW] That Deal",
                   :author     "Rand",
                   :date       "2020-06-16",
                   :snippet    "Hi All, Here's a project.",
                   :email-link "link"}
                  {:post-id    "baz",
                   :topic-id   "some-id2",
                   :title      "[DEAL REVIEW] That Deal",
                   :author     "Rand",
                   :date       "2020-06-16",
                   :snippet    "Hi All, Here's a project.",
                   :email-link "link"}]]
      (is (= (thread-emails-with-counts thread)
             ["Tara 1" "Rand 2"])))))



(deftest thread-stat-processing
  (testing "transform grouped PostSummary records into ThreadStat records"
    (let [entry1 (->PostSummary "foo" "some-id1" "[DEAL REVIEW] This Deal" "Chris" (jt/local-date "2013-01-29") "Hi All, Here's a project." "link")
          entry2 (->PostSummary "bar" "some-id2" "[DEAL REVIEW] That Deal" "Tara" (jt/local-date "2020-06-08") "Hi All, Here's a project." "link")
          entry3 (->PostSummary "baz" "some-id2" "[DEAL REVIEW] That Deal" "Rand" (jt/local-date "2020-06-16") "Hi All, Here's a project." "link")
          entry4 (->PostSummary "bam" "some-id1" "[DEAL REVIEW] This Deal" "Julie" (jt/local-date "2020-06-16") "Hi All, Here's a project." "link")
          threads (group-by :topic-id (list entry1 entry2 entry3 entry4))
          threadstats (map-to-ThreadStat threads)]
      (is (= 0 (-> {} map-to-ThreadStat count)))
      (is (= 2 (count threadstats)))
      (is (= "some-id2" (-> threadstats last :thread-id)))
      (is (= "[DEAL REVIEW] That Deal" (-> threadstats last :title)))
      (is (= "Tara" (-> threadstats last :initiator)))
      (is (= 2 (-> threadstats last :email-count)))
      (is (= 8 (-> threadstats last :days-span)))
      (is (= 2 (-> threadstats last :unique-contributor-count)))
      (is (= ["Tara 1" "Rand 1"] (-> threadstats last :unique-contributors)))
      ))

  (testing "protocols on records"
    (let [stat (->ThreadStat "id" "deal" "chris" "1" "28" "1" ["chris@email"])]
      (is (= 7 (-> stat get-values count))))))

