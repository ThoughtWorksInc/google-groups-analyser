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
             (let [lazy-topics      (topics (take 2 docs))
                   topic1           (first lazy-topics)
                   topic2           (second lazy-topics)
                   topic3           (nth lazy-topics 2)
                   topic4           (nth lazy-topics 3)
                   eight-topics     (take 8 lazy-topics)]
               ;               (pp/pprint (take 2 lazy-topic-pages))
               ;               (pp/pprint first-4-topics)
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
                              :content ["18/06/20"]}]}]} ; /tbody]}
                         ; /table
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
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Suneet Jindal"]}]}
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
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Emily Carey"]}]}
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
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/ZaKGUk4k4dE",
                                                                                                :title
                                                                                                ["Discover Core"]},
                                                                                      :tag     :a,
                                                                                      :content ["Discover Core"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Greg Murray"]}]}
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
                                                                                      :content ["Emily Carey (via Google Docs)"]}]}
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
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/G-11e0dFOLA",
                                                                                                :title
                                                                                                "Extended Alpha January"},
                                                                                      :tag     :a,
                                                                                      :content ["Extended Alpha January"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Emily Carey (via Google Drive)"]}]}
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
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/0da9ltXInMQ",
                                                                                                :title
                                                                                                "alpha beta gamma"},
                                                                                      :tag     :a,
                                                                                      :content ["alpha beta gamma"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Emily Carey (via Google Drive)"]}]}
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
                                                                                      :content ["Nicki Czerska (via Google Drive)"]}]}
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
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/qXJMFhi_qxM",
                                                                                                :title
                                                                                                "Digital Compatibility"},
                                                                                      :tag     :a,
                                                                                      :content ["Digital Compatibility"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["David Howell"]}]}
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
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/AT8cy511mH8",
                                                                                                :title
                                                                                                "Organisation and Simplification"},
                                                                                      :tag     :a,
                                                                                      :content ["Organisation and Simplification"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Darren Jackson"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["18/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type  :element,
                                                                                      :attrs {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/JMbrEv9rJgg",
                                                                                              :title
                                                                                              "21st Avenue"},
                                                                                      :tag   :a,
                                                                                      :content
                                                                                      ["21st Avenue"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Jeantine Mankelow"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["18/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/_t_0ylhelM4",
                                                                                                :title
                                                                                                "Software Development"},
                                                                                      :tag     :a,
                                                                                      :content ["Software Development"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["David Howell"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["18/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/OWCiU-xHKo4",
                                                                                                :title
                                                                                                "Investment for London"},
                                                                                      :tag     :a,
                                                                                      :content ["Investment for London"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Phil Hingley"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["18/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/eADRCxz1kgA",
                                                                                                :title
                                                                                                "Data Service Streams"},
                                                                                      :tag     :a,
                                                                                      :content ["Data Service Streams"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Vindy Hansra"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["17/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/zhcMWaBcGJU",
                                                                                                :title
                                                                                                "Management - Request for access"},
                                                                                      :tag     :a,
                                                                                      :content ["Management - Request for access"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Nathalie Smith (via Google Drive)"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["16/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https : //groups.google.com/a/domain.com/d/topic/forum-title/ycpb8_xPV2o",
                                                                                                :title
                                                                                                "DR        (Heads up only - actual request to follow tomorrow) : Lloyd's of London    - Reserach"},
                                                                                      :tag     :a,
                                                                                      :content ["DR (Heads up only - actual request to follow tomorrow) : Lloyd's of London - Reserach"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Milind Dhotre"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["13/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https : //groups.google.com/a/domain.com/d/topic/forum-title/JCofTIQs8lY",
                                                                                                :title
                                                                                                "Re :                                                                          Deal Review    | Etsy      | Amendment #3 to        SOW #26       (Observability)"},
                                                                                      :tag     :a,
                                                                                      :content ["Re: Deal Review | Etsy | Amendment #3 to SOW #26 (Observability)"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Miguel Guillamon Gil"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["10/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type  :element,
                                                                                      :attrs {:href  "https://groups.google.com/a/domain.com/d/topic/forum-title/qPPFJGPP_J0",
                                                                                              :title "Draft   Outcome SOW     for Cabinet Office}, :tag     :a, :content [Draft Outcome SOW for Cabinet Office"}}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Laura Paterson"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["09/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/vYMusxPUfZ0",
                                                                                                :title
                                                                                                "Deal       Review (IN)       Data Leadership Program Support"},
                                                                                      :tag     :a,
                                                                                      :content ["Deal Review (IN) Data Leadership Program Support"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Simon Carden"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["04/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/yKuc57t-3eQ",
                                                                                                :title
                                                                                                "NHBC  RFI review"},
                                                                                      :tag     :a,
                                                                                      :content ["NHBC RFI review"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type :element, :attrs nil, :tag :span, :content ["Suneet Jindal"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["03/06/20"]}]}

                                                               {:type    :element,
                                                                :attrs   nil,
                                                                :tag     :tr,
                                                                :content [{:type    :element,
                                                                           :attrs   {:class "subject"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   {:href "https://groups.google.com/a/domain.com/d/topic/forum-title/FTj5Ll9bhHs",
                                                                                                :title
                                                                                                "Management - Request for access"},
                                                                                      :tag     :a,
                                                                                      :content ["Management - Request for access"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "author"},
                                                                           :tag     :td,
                                                                           :content [{:type    :element,
                                                                                      :attrs   nil,
                                                                                      :tag     :span,
                                                                                      :content ["Emily Margo (via Google Drive)"]}]}
                                                                          {:type    :element,
                                                                           :attrs   {:class "lastPostDate"},
                                                                           :tag     :td,
                                                                           :content ["02/06/20"]}]}]}]}
                                         {:type    :element,
                                          :attrs   {:href "https://groups.google.com/a/domain.com/forum/?_escaped_fragment_=forum/forum-title%5B21-40%5D"},
                                          :tag     :a,
                                          :content ["More topics »"]}]}]}]}
        docs            (repeat doc)
        select-html-tag (comp :tag second :content)
        forum-title  (comp :content first :content second :content second :content)]

    (testing "is 'doc' well formed?"
             (is (map? doc))
             (is (= :html (select-html-tag doc)))
             (is (= ["Forum Title"] (forum-title doc)))
             (is (= :html (select-html-tag (first docs)))))
    (testing "should transform a parsed html doc seq to rows"
             (let [rows       (table-rows (take 2 docs))]
               (is (= 30 (count (take 30 rows))))
               (is (map? (first rows)))))
    (testing "should transform a parsed html doc seq to Topics"
             (let [topics (topics (take 3 docs))]
               (is (record? (first topics)))
               (is (string? (to-str (first topics))))
               (is (= "Suneet Jindal" (:author (first topics))))
               (is
                (= "SaaS Platform"
                   (:title (first topics))))
               (is (= "Emily Carey" (:author (second topics))))
               (is (= "Emily Margo (via Google Drive)" (:author (nth topics 19))))
               (is (= "Management - Request for access" (:title (nth topics 19))))
               (is (= "Suneet Jindal" (:author (nth topics 20))))
               (is
                (= "SaaS Platform"
                   (:title (nth topics 20))))
               (is (= "Emily Margo (via Google Drive)" (:author (nth topics 39))))
               (is (= "Management - Request for access" (:title (nth topics 39))))
               (is (= "Management - Request for access" (:title (nth topics 59))))))))

(deftest process-topic-pages
  (testing "should only fetch X number of topics"
           (let [docs                (forum-page-sequence "https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4")
                 sixty-topics        (topics (take 3 docs))]
             (is (= (count (take 42 sixty-topics)) 42))
             (is (record? (first sixty-topics)))
             (is (string? (to-str (first sixty-topics)))))))

(deftest hickory-pages
  (testing "should fetch a hickory page from a url and turn it into a sequence of hickory maps"
    (is (contains? (first (html-hickory-pages ["https://groups.google.com/forum/#!forum/django-users"]))
                   :content))))
