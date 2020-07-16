(ns atom-feed-stats.ggcrawler-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pp]
            [atom-feed-stats.ggcrawler :refer :all]))

(deftest cookie-store-test
  (testing "put cookies in the cookie store"
           (let [cookies "SEARCH_SAMESITE=CgQIhY8B; CONSENT=YES+GB.en+20150816-15-0; ANID=OPT_OUT;"]
             (is (= cookies (init-cookie-store cookies))))))

(deftest gen-url-sequences
  (testing "should get a sequence of group urls"
           (let [urls (forum-page-urls "https://a.b.com/?frag=group")]
             (are [x y] (= x y)
                  "https://a.b.com/?frag=group%5B1-20%5D" (first urls)
                  "https://a.b.com/?frag=group%5B21-40%5D" (second urls)
                  "https://a.b.com/?frag=group%5B41-60%5D" (nth urls 2)
                  "https://a.b.com/?frag=group%5B101-120%5D" (nth urls 5)))))

(deftest fetch-topics
  (let [cookies (init-cookie-store "SEARCH_SAMESITE=CgQIhY8B; CONSENT=YES+GB.en+20150816-15-0; ANID=OPT_OUT;")]
    (testing "lazily fetch topics"
             (let [docs (forum-page-sequence "https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4")]
               (is (seq? docs))
               (is (map? (first docs)))
               (is (map? (second docs)))
               (is (map? (nth docs 2)))))))

(deftest filter-topics
  (testing "should filter out Topics with 'request for access' in the title"
    (let [topic1 (->Topic "Thread Title" "Jane Doe" "https://link")
          topic2 (->Topic "Thread Title - Request for access" "Jane Doe" "https://link")
          topic3 (->Topic "Thread Title" "John Doe (via Google)" "https://link2")]
      (is (= [topic1] (filtered-topics [topic1 topic2 topic3]))))))

(deftest process-topic-pages
  (testing "should only fetch X number of topics"
           (let [docs                (forum-page-sequence "https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4")
                 sixty-topics        (topics (take 3 docs))]
             (is (= (count (take 42 sixty-topics)) 42))
             (is (record? (first sixty-topics)))
             (is (string? (to-str (first sixty-topics)))))))

(deftest hickory-pages
  (testing "should fetch a hickory page from a url and turn it into a hickory map"
    (is (contains? (url->hickory "https://groups.google.com/forum/#!forum/django-users")
                   :content)))
  (testing "should turn a collection of urls into a collection of hickory pages"
    (is (html-hickory-pages ["https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4"
                             "https://groups.google.com/forum/#!forum/django-users"]))))


(deftest lazily-process-topics
  (let [doc
        {:type :document,
         :content
               [{:type :document-type,
                 :attrs
                       {:name     "html",
                        :publicid "-//W3C//DTD HTML 4.01//EN",
                        :systemid "http://www.w3.org/TR/html4/strict.dtd"}}
                {:type  :element,
                 :attrs nil,
                 :tag   :html,
                 :content
                        [{:type  :element,
                          :attrs nil,
                          :tag   :head,
                          :content
                                 ["\n"
                                  {:type    :element,
                                   :attrs   nil,
                                   :tag     :title,
                                   :content ["Some Content"]}
                                  "\n"
                                  {:type    :element,
                                   :attrs
                                            {:rel "canonical",
                                             :href
                                                  "https://groups.google.com/a/my-domain.example/d/forum/my-forum%5B1-20%5D"},
                                   :tag     :link,
                                   :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs   {:property "og:title", :content "My Forum"},
                                   :tag     :meta,
                                   :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs   {:property "og:type", :content "website"},
                                   :tag     :meta,
                                   :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs
                                            {:property "og:url",
                                             :content
                                                       "https://groups.google.com/a/my-domain.example/forum/#!forum/my-forum%5B1-20%5D"},
                                   :tag     :meta,
                                   :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs
                                            {:property "og:image",
                                             :content
                                                       "http://www.google.com/images/icons/product/groups-128.png"},
                                   :tag     :meta,
                                   :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs   {:property "og:site_name", :content "Google Groups"},
                                   :tag     :meta,
                                   :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs   {:property "og:description", :content "Google Group"},
                                   :tag     :meta,
                                   :content nil}]}
                         "\n"
                         {:type  :element,
                          :attrs nil,
                          :tag   :body,
                          :content
                                 ["\n"
                                  {:type    :element,
                                   :attrs   nil,
                                   :tag     :h2,
                                   :content ["My Forum"]}
                                  "\n\n\n"
                                  {:type :element, :attrs nil, :tag :ul, :content nil}
                                  "\n"
                                  {:type    :element,
                                   :attrs   nil,
                                   :tag     :i,
                                   :content ["Showing 1-20 of 1442 topics"]}
                                  "\n"
                                  {:type  :element,
                                   :attrs {:border "0", :cellspacing "0"},
                                   :tag   :table,
                                   :content
                                          [{:type  :element,
                                            :attrs nil,
                                            :tag   :tbody,
                                            :content
                                                   [" "
                                                    {:type  :element,
                                                     :attrs nil,
                                                     :tag   :tr,
                                                     :content
                                                            [{:type  :element,
                                                              :attrs {:class "subject"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type :element,
                                                                       :attrs
                                                                             {:href
                                                                              "https://groups.google.com/a/my-domain.example/d/topic/my-forum/ZaKGUk4k4dE",
                                                                              :title
                                                                              "[ONE] Thread Number 1"},
                                                                       :tag  :a,
                                                                       :content
                                                                             ["[ONE] Thread Number 1"]}]}
                                                             "\n"
                                                             {:type  :element,
                                                              :attrs {:class "author"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type    :element,
                                                                       :attrs   nil,
                                                                       :tag     :span,
                                                                       :content ["Joe Bloggs"]}]}
                                                             "\n"
                                                             {:type    :element,
                                                              :attrs   {:class "lastPostDate"},
                                                              :tag     :td,
                                                              :content ["05:56"]}]}
                                                    " "
                                                    {:type  :element,
                                                     :attrs nil,
                                                     :tag   :tr,
                                                     :content
                                                            [{:type  :element,
                                                              :attrs {:class "subject"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type :element,
                                                                       :attrs
                                                                             {:href
                                                                              "https://groups.google.com/a/my-domain.example/d/topic/my-forum/qXJMFhi_qxM",
                                                                              :title
                                                                              "[TWO] Thread Number 2"},
                                                                       :tag  :a,
                                                                       :content
                                                                             ["[TWO] Thread Number 2"]}]}
                                                             "\n"
                                                             {:type  :element,
                                                              :attrs {:class "author"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type    :element,
                                                                       :attrs   nil,
                                                                       :tag     :span,
                                                                       :content ["Jane Doe"]}]}
                                                             "\n"
                                                             {:type    :element,
                                                              :attrs   {:class "lastPostDate"},
                                                              :tag     :td,
                                                              :content ["05:12"]}]}
                                                    " "
                                                    {:type  :element,
                                                     :attrs nil,
                                                     :tag   :tr,
                                                     :content
                                                            [{:type  :element,
                                                              :attrs {:class "subject"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type :element,
                                                                       :attrs
                                                                             {:href
                                                                              "https://groups.google.com/a/my-domain.example/d/topic/my-forum/AT8cy511mH8",
                                                                              :title
                                                                              "[THREE] Thread Number 3"},
                                                                       :tag  :a,
                                                                       :content
                                                                             ["[THREE] Thread Number 3"]}]}
                                                             "\n"
                                                             {:type  :element,
                                                              :attrs {:class "author"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type    :element,
                                                                       :attrs   nil,
                                                                       :tag     :span,
                                                                       :content ["David Jacobson"]}]}
                                                             "\n"
                                                             {:type    :element,
                                                              :attrs   {:class "lastPostDate"},
                                                              :tag     :td,
                                                              :content ["18/06/20"]}]}
                                                    " "
                                                    {:type  :element,
                                                     :attrs nil,
                                                     :tag   :tr,
                                                     :content
                                                            [{:type  :element,
                                                              :attrs {:class "subject"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type :element,
                                                                       :attrs
                                                                             {:href
                                                                              "https://groups.google.com/a/my-domain.example/d/topic/my-forum/_t_0ylhelM4",
                                                                              :title
                                                                              "[FOUR] Thread Number 4"},
                                                                       :tag  :a,
                                                                       :content
                                                                             ["[FOUR] Thread Number 4"]}]}
                                                             "\n"
                                                             {:type  :element,
                                                              :attrs {:class "author"},
                                                              :tag   :td,
                                                              :content
                                                                     [{:type    :element,
                                                                       :attrs   nil,
                                                                       :tag     :span,
                                                                       :content ["Jane Doe"]}]}
                                                             "\n"
                                                             {:type    :element,
                                                              :attrs   {:class "lastPostDate"},
                                                              :tag     :td,
                                                              :content ["18/06/20"]}]}]}]}
                                  ; /table
                                  "\n"
                                  {:type    :element,
                                   :attrs
                                            {:href
                                             "https://groups.google.com/a/my-domain.example/forum/?_escaped_fragment_=forum/my-forum%5B21-40%5D"},
                                   :tag     :a,
                                   :content ["More topics »"]}]}]}]}
        docs (repeat doc)]
    (testing "should transform a parsed html row list to a Topic list"
      (let [rows (table-rows (take 2 docs))]
        (is (= 8 (count rows)))
        (is (= :tr (-> rows first :tag)))
        (is (vector? (-> rows first :content)))
        (is (= :tr (-> rows second :tag)))
        (is (vector? (-> rows second :content)))))
    (testing "transform topic rows to topic records"
      (let [lazy-topics (topics (take 2 docs))
            topic1 (first lazy-topics)
            topic2 (second lazy-topics)
            topic4 (nth lazy-topics 3)]
        (is (record? topic1))
        (is (= "[ONE] Thread Number 1" (:title topic1)))
        (is (= "[TWO] Thread Number 2" (:title topic2)))
        (is (= "Jane Doe" (:author topic4)))
        (is (= "[FOUR] Thread Number 4" (:title topic4)))
        (is
          (= "https://groups.google.com/a/my-domain.example/d/topic/my-forum/_t_0ylhelM4"
             (:link topic4)))
        (is
          (= "_t_0ylhelM4"
             (get-topic-id-from-url (:link topic4)))))))
  (testing "should get a nil result when there are no more topics"
    (let [last-page
          {:type :document,
           :content
                 [{:type :document-type,
                   :attrs
                         {:name     "html",
                          :publicid "-//W3C//DTD HTML 4.01//EN",
                          :systemid "http://www.w3.org/TR/html4/strict.dtd"}}
                  {:type  :element,
                   :attrs nil,
                   :tag   :html,
                   :content
                          [{:type  :element,
                            :attrs nil,
                            :tag   :head,
                            :content
                                   ["\n"
                                    {:type    :element,
                                     :attrs   nil,
                                     :tag     :title,
                                     :content ["Some Content"]}]}
                           "\n"
                           "\n"
                           {:type  :element,
                            :attrs nil,
                            :tag   :body,
                            :content
                                   ["\n"
                                    {:type    :element,
                                     :attrs   nil,
                                     :tag     :h2,
                                     :content ["My Forum"]}
                                    "\n\n\n"
                                    {:type :element, :attrs nil, :tag :ul, :content nil}
                                    "\n"
                                    {:type    :element,
                                     :attrs   nil,
                                     :tag     :i,
                                     :content ["Showing 1-20 of 1442 topics"]}
                                    "\n"
                                    {:type  :element,
                                     :attrs {:border "0", :cellspacing "0"},
                                     :tag   :table,
                                     :content
                                            [{:type  :element,
                                              :attrs nil,
                                              :tag   :tbody,
                                              :content
                                                     [" "
                                                      {:type  :element,
                                                       :attrs nil,
                                                       :tag   :tr,
                                                       :content
                                                              [{:type  :element,
                                                                :attrs {:class "subject"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type :element,
                                                                         :attrs
                                                                               {:href
                                                                                "https://groups.google.com/a/my-domain.example/d/topic/my-forum/ZaKGUk4k4dE",
                                                                                :title
                                                                                "[ONE] Thread Number 1"},
                                                                         :tag  :a,
                                                                         :content
                                                                               ["[ONE] Thread Number 1"]}]}
                                                               "\n"
                                                               {:type  :element,
                                                                :attrs {:class "author"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type    :element,
                                                                         :attrs   nil,
                                                                         :tag     :span,
                                                                         :content ["Joe Bloggs"]}]}
                                                               "\n"
                                                               {:type    :element,
                                                                :attrs   {:class "lastPostDate"},
                                                                :tag     :td,
                                                                :content ["05:56"]}]}
                                                      " "
                                                      {:type  :element,
                                                       :attrs nil,
                                                       :tag   :tr,
                                                       :content
                                                              [{:type  :element,
                                                                :attrs {:class "subject"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type :element,
                                                                         :attrs
                                                                               {:href
                                                                                "https://groups.google.com/a/my-domain.example/d/topic/my-forum/qXJMFhi_qxM",
                                                                                :title
                                                                                "[TWO] Thread Number 2"},
                                                                         :tag  :a,
                                                                         :content
                                                                               ["[TWO] Thread Number 2"]}]}
                                                               "\n"
                                                               {:type  :element,
                                                                :attrs {:class "author"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type    :element,
                                                                         :attrs   nil,
                                                                         :tag     :span,
                                                                         :content ["Jane Doe"]}]}
                                                               "\n"
                                                               {:type    :element,
                                                                :attrs   {:class "lastPostDate"},
                                                                :tag     :td,
                                                                :content ["05:12"]}]}
                                                      " "
                                                      {:type  :element,
                                                       :attrs nil,
                                                       :tag   :tr,
                                                       :content
                                                              [{:type  :element,
                                                                :attrs {:class "subject"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type :element,
                                                                         :attrs
                                                                               {:href
                                                                                "https://groups.google.com/a/my-domain.example/d/topic/my-forum/AT8cy511mH8",
                                                                                :title
                                                                                "[THREE] Thread Number 3"},
                                                                         :tag  :a,
                                                                         :content
                                                                               ["[THREE] Thread Number 3"]}]}
                                                               "\n"
                                                               {:type  :element,
                                                                :attrs {:class "author"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type    :element,
                                                                         :attrs   nil,
                                                                         :tag     :span,
                                                                         :content ["David Jacobson"]}]}
                                                               "\n"
                                                               {:type    :element,
                                                                :attrs   {:class "lastPostDate"},
                                                                :tag     :td,
                                                                :content ["18/06/20"]}]}
                                                      " "
                                                      {:type  :element,
                                                       :attrs nil,
                                                       :tag   :tr,
                                                       :content
                                                              [{:type  :element,
                                                                :attrs {:class "subject"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type :element,
                                                                         :attrs
                                                                               {:href
                                                                                "https://groups.google.com/a/my-domain.example/d/topic/my-forum/_t_0ylhelM4",
                                                                                :title
                                                                                "[FOUR] Thread Number 4"},
                                                                         :tag  :a,
                                                                         :content
                                                                               ["[FOUR] Thread Number 4"]}]}
                                                               "\n"
                                                               {:type  :element,
                                                                :attrs {:class "author"},
                                                                :tag   :td,
                                                                :content
                                                                       [{:type    :element,
                                                                         :attrs   nil,
                                                                         :tag     :span,
                                                                         :content ["Jane Doe"]}]}
                                                               "\n"
                                                               {:type    :element,
                                                                :attrs   {:class "lastPostDate"},
                                                                :tag     :td,
                                                                :content ["18/06/20"]}]}]}
                                             "\n"]}]}]}]}]
      (is (nil? (get-more-topics-link last-page)))))
  (testing "print a topic record nicely"
    (let [topic (->Topic "Jane Doe" "Thread Title" "https://link")]
      (is (= "Thread Title, Jane Doe, https://link" (to-str topic))))))


(deftest lazily-process-topics-2
  (let [doc
        {:type    :document,
         :content [{:type  :document-type,
                    :attrs {:name     "html",
                            :publicid "-//W3C//DTD HTML 4.01 //EN, :systemid http : //www.w3.org/TR/html4/strict.dtd"}}
                   {:type    :element,
                    :attrs   nil,
                    :tag     :html,
                    :content [{:type    :element,
                               :attrs   nil,
                               :tag     :head,
                               :content [{:type    :element,
                                          :attrs   nil,
                                          :tag     :title,
                                          :content ["Forum Title - Google Groups"]}
                                         {:type    :element,
                                          :attrs   {:rel  "canonical",
                                                    :href "https://groups.google.com/a/domain.com/d/forum/forum-title%5B1-20%5D"},
                                          :tag     :link,
                                          :content nil}
                                         {:type    :element,
                                          :attrs   {:property "og:title", :content "Forum Title"},
                                          :tag     :meta,
                                          :content nil}
                                         {:type    :element,
                                          :attrs   {:property "og:type", :content "website"},
                                          :tag     :meta,
                                          :content nil}
                                         {:type    :element,
                                          :attrs   {:property "og:url",
                                                    :content  "https://groups.google.com/a/domain.com/forum/#!forum/forum-title%5B1-20%5D"},
                                          :tag     :meta,
                                          :content nil}
                                         {:type    :element,
                                          :attrs   {:property "og:image",
                                                    :content  "http://www.google.com/images/icons/product/groups-128.png"},
                                          :tag     :meta,
                                          :content nil}
                                         {:type    :element,
                                          :attrs   {:property "og:site_name", :content "Google Groups"},
                                          :tag     :meta,
                                          :content nil}
                                         {:type    :element,
                                          :attrs   {:property "og:description", :content "Google Group"},
                                          :tag     :meta,
                                          :content nil}]}
                              {:type    :element,
                               :attrs   nil,
                               :tag     :body,
                               :content [{:type :element, :attrs nil, :tag :h2, :content ["Forum Title"]}
                                         {:type :element, :attrs nil, :tag :ul, :content nil}
                                         {:type :element, :attrs nil, :tag :i, :content ["Showing 1-20 of 1447 topics"]}
                                         {:type    :element,
                                          :attrs   {:border 0, :cellspacing 0},
                                          :tag     :table,
                                          :content [{:type    :element,
                                                     :attrs   nil,
                                                     :tag     :tbody,
                                                     :content [{:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/36cy_bBbnY4",
                                                                                                :title
                                                                                                      "SaaS Platform"},
                                                                                      :tag     :a,
                                                                                      :content ["SaaS Platform"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Suneet"]}]}
                                                                          {:type :element, :attrs {:class "lastPostDate"}, :tag :td, :content [02 :43]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href  "https://groups.google.com/a/domain.com/d/topic/forum-title/vku9W4H4ZhE,"
                                                                                                :title "Innovation Service"},
                                                                                      :tag     :a,
                                                                                      :content ["Innovation Service"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Emily"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["22/06/20"]}]}
                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/nwfgYTp19J4",
                                                                                                :title
                                                                                                      "Extended Alpha - Invitation to edit"},
                                                                                      :tag     :a,
                                                                                      :content ["Extended Alpha - Invitation to edit"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Emily (via Google Docs)"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["21/06/20"]}]}
                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/rveRqbtTJG8",
                                                                                                :title
                                                                                                      "ZXA 5 - Request for access"},
                                                                                      :tag     :a,
                                                                                      :content ["ZXA 5 - Request for access"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Nicki"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["19/06/20"]}]}
                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https : //groups.google.com/a/domain.com/d/topic/forum-title/ycpb8_xPV2o",
                                                                                                :title "This is a DR"},
                                                                                      :tag     :a,
                                                                                      :content ["DR"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Milind"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["13/06/20"]}]}]}]}
                                         {:type    :element,
                                          :attrs   {:href "https://groups.google.com/a/domain.com/forum/?_escaped_fragment_=forum/forum-title%5B21-40%5D"},
                                          :tag     :a,
                                          :content ["More topics »"]}]}]}]}
        docs (repeat doc)
        select-html-tag (comp :tag second :content)
        forum-title (comp :content first :content second :content second :content)]

    (testing "is 'doc' well formed?"
      (is (map? doc))
      (is (= :html (select-html-tag doc)))
      (is (= ["Forum Title"] (forum-title doc)))
      (is (= :html (select-html-tag (first docs)))))
    (testing "should transform a parsed html doc seq to rows"
      (let [rows (table-rows (take 2 docs))]
        (is (= 10 (count rows)))
        (is (map? (first rows)))))
    (testing "should transform a parsed html doc seq to Topics"
      (let [topics (topics (repeat 3 doc))]
        (is (record? (first topics)))
        (is (string? (to-str (first topics))))
        (is (= "Suneet" (:author (first topics))))
        (is (= "SaaS Platform" (:title (first topics))))
        (is (= "Emily" (:author (second topics))))
        (is (= "Milind" (:author (last topics))))
        (is (= "This is a DR" (:title (last topics))))))))
