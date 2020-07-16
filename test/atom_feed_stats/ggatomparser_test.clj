(ns atom-feed-stats.ggatomparser-test
  (:require [clojure.test :refer :all]
            [java-time :as jt]
            [atom-feed-stats.ggatomparser :refer :all]))

(deftest atom-parsing
  (let [feed (parse-atom "deal-review-atom-feed.xml")
        entry {:tag     :entry,
               :attrs   nil,
               :content [{:tag     :author,
                          :attrs   nil,
                          :content [{:tag :name, :attrs nil, :content ["Thingy Ma Bob"]}
                                    {:tag :email, :attrs nil, :content ["thingymabob@thoughtworks.com"]}]}
                         {:tag :updated, :attrs nil, :content ["2020-06-16T13:52:02Z"]}
                         {:tag     :id,
                          :attrs   nil,
                          :content ["https://groups.google.com/a/my-domain.example/d/topic/uk-deal-review/foobar"]}
                         {:tag     :link,
                          :attrs   {:href "https://groups.google.com/a/my-domain.example/d/topic/uk-deal-review/foobar"},
                          :content nil}
                         {:tag :title, :attrs {:type "text"}, :content ["[DEAL REVIEW] Some Deal"]}
                         {:tag :summary, :attrs {:type "html"}, :content ["Hi All, Here's a project."]}]}]
    (testing "get a named nested element by it's :tag value"
      (is (= "[DEAL REVIEW] Some Deal" (-> (get-element entry :title) :content first))))
    (testing "parse a named resource to a clojure map"
      (is (= :feed (:tag feed))))
    (testing "transform feed map to a sequence of Entry"
      (let [entries (feed-to-entry-seq feed)]
        (is (= (count entries) 100))
        (is (record? (first entries)))
        (is (record? (last entries)))
        (is (-> entries first :email string?))
        (is (-> entries first :name string?))))))

(deftest feed-processing
  (testing "find newest and oldest entries"
    ; (defrecord Entry [thread-id name email updated title summary])
    (let [entry1 (->Entry "foo" "Chris" "chris@email" (jt/instant "2020-06-16T13:52:02Z") "[DEAL REVIEW] This Deal" "Hi All, Here's a project.")
          entry2 (->Entry "bar" "Tara" "tara@email.com" (jt/instant "2020-05-12T15:35:41Z") "[DEAL REVIEW] That Deal" "Hi All, It's a big deal.")
          entry3 (->Entry "bar" "Rand" "rand@email.com" (jt/instant "2020-05-14T16:31:51Z") "[DEAL REVIEW] That Deal" "Not such a big deal.")
          entries (list entry1 entry2 entry3)
          oldest (jt/instant "2020-05-12T15:35:41Z")
          newest (jt/instant "2020-06-16T13:52:02Z")]

      (is (= (jt/min newest oldest) oldest))
      (is (= (oldest-entry-instant entries) oldest))
      (is (= (newest-entry-instant entries) newest))))
  (testing "transform Entry seq and group by thread-id"
    (let [entry1 (->Entry "foo" "Chris" "chris@email" (jt/instant "2020-06-16T13:52:02Z") "[DEAL REVIEW] This Deal" "Hi All, Here's a project.")
          entry2 (->Entry "bar" "Tara" "tara@email.com" (jt/instant "2020-05-12T15:35:41Z") "[DEAL REVIEW] That Deal" "Hi All, It's a big deal.")
          entry3 (->Entry "bar" "Rand" "rand@email.com" (jt/instant "2020-05-14T16:31:51Z") "[DEAL REVIEW] That Deal" "Not such a big deal.")
          entries (list entry1 entry2 entry3)]
      (is (= 0 (-> '() group-entries-by-thread-id count)))
      (is (= 2 (-> entries group-entries-by-thread-id count)))
      (is (= 2 (-> entries group-entries-by-thread-id (get "bar") count)))
      (is (= 1 (-> entries group-entries-by-thread-id (get "foo") count))))))

(deftest thread-stat-processing
  (testing "transform grouped Entry records into ThreadStat records"
    (let [entry1 (->Entry "foo" "Chris" "chris@email" (jt/instant "2020-06-16T13:52:02Z") "[DEAL REVIEW] This Deal" "Hi All, Here's a project.")
          entry2 (->Entry "bar" "Tara" "tara@email.com" (jt/instant "2020-05-12T15:35:41Z") "[DEAL REVIEW] That Deal" "Hi All, It's a big deal.")
          entry3 (->Entry "bar" "Rand" "rand@email.com" (jt/instant "2020-05-15T16:31:51Z") "[DEAL REVIEW] That Deal" "Not such a big deal.")
          entry4 (->Entry "bar" "Rand" "rand@email.com" (jt/instant "2020-05-16T11:39:11Z") "[DEAL REVIEW] That Deal" "Woohoo! Winner.")
          threads (group-entries-by-thread-id (list entry1 entry2 entry3 entry4))
          threadstats (map-to-ThreadStat threads)]
      (is (= 0 (-> {} map-to-ThreadStat count)))
      (is (= 2 (count threadstats)))
      (is (= "bar" (-> threadstats last :thread-id)))
      (is (= "[DEAL REVIEW] That Deal" (-> threadstats last :title)))
      (is (= "Tara" (-> threadstats last :initiator)))
      (is (= 3 (-> threadstats last :email-count)))
      (is (= 3 (-> threadstats last :days-span)))
      (is (= 2 (-> threadstats last :unique-contributor-count)))
      (is (= ["tara@email.com" "rand@email.com"] (-> threadstats last :unique-contributors)))
      ))

  (testing "protocols on records"
    (let [stat (->ThreadStat "id" "deal" "chris" "1" "28" "1" ["chris@email"])]
      (is (= 7 (-> stat get-values count))))))

(deftest thread-stat-csv
  (testing "should transform a threadstat into csv format"
    (let [stat (->ThreadStat "id" "deal" "chris" "1" "28" "1" ["chris@email"])
          threadstat-csv (to-str stat)]
      (= ("id, deal, chris, 1, 28, 1, chris@email" threadstat-csv)))))