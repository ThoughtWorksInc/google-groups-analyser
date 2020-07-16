(ns atom-feed-stats.ggposts-test
  (:require [clojure.test :refer :all]
            [java-time :as jt]
            [atom-feed-stats.ggcrawler :as ggc]
            [atom-feed-stats.ggposts :refer :all]))

(deftest get-posts
  (let [topics
        [(ggc/new-topic "jw12203", "PM SECTION", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s")
         (ggc/new-topic "jw12203", "TO DO LIST TUESDAY 22ND JANUARY....", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/e9uMeq-0Xyg")
         (ggc/new-topic "Matt", "Latest (Totally Awesome!) Version", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/12bVEm_Bbzk")
         (ggc/new-topic "Matt", "Latest (Currently not really working) version", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/9qrFrhGnwrE")
         (ggc/new-topic "Matt", "Level Selection", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/1y4HYKuwmOI")
         (ggc/new-topic "jw12203", "boss", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/r8hTOyF7F20")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "User Manual", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/76qh8Jz_qG4")
         (ggc/new-topic "Matt", "Level 5", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/roG_h87mfk0")
         (ggc/new-topic "yc12143", "Level4", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4")
         (ggc/new-topic "Matt", "Updated code with working enemy sprites", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/4RpCgJDx7uA")
         (ggc/new-topic "jw12203", "Project Management Section....", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/S3FYEqDrenc")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "IF FUNCTION - Final Version", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/d87dTK2TwGI")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "IF Function", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/-463YuVaC3k")
         (ggc/new-topic "Matt", "Updated Integrated Sophisticated Game", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/bx12CglPiG0")
         (ggc/new-topic "Matt", "Combined", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/GIxU1KAC1ok")
         (ggc/new-topic "Matt", "Loops", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/gFds_Ztxidc")
         (ggc/new-topic "jw12203", "2nd level with collsion and death etc etc", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/rlZv8ACrdIk")
         (ggc/new-topic "Matt", "Tightened Code", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/07TKj7s5Eto")
         (ggc/new-topic "jw12203", "Meeting on Monday 10am", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/V2YnK1nKsOU")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "Final Version Map Editor and jump enabled platform", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/zaVWcdPsfjI")
         (ggc/new-topic "Matt", "Working First Level (without the tutorial messages)", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/LXo6ziN07CU")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "map editor v1", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/Vi55ERJftz4")
         (ggc/new-topic "jw12203", "Second level", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/auEuySdIP9k")
         (ggc/new-topic "jw12203", "Exit", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/nperimz7H78")
         (ggc/new-topic "Matt", "New one", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/h-IuHt97IHg")
         (ggc/new-topic "jw12203", "Alpha Shiz", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/SqBV_khKHow")
         (ggc/new-topic "jw12203", "UPDATE!!!! LOOK AT THIS", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/672bitmejyE")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "Task that i working on", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/4PqnW19kfec")
         (ggc/new-topic "Matt", "Camera", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/RgcqtkG_hOs")
         (ggc/new-topic "Matt", "Collision", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/boqLB-3swkk")
         (ggc/new-topic "jw12203", "Hilarious 4 frame walk animation", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/ytRuKUQDxQg")
         (ggc/new-topic "mm1.. . @my.bristol.ac.uk", "Code for animation", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/tii3J0vov6k")
         (ggc/new-topic "jw12203", "Start of next week", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/hUUMP-DERO8")
         (ggc/new-topic "jw12203", "Code update", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/x71g4vpHrHM")
         (ggc/new-topic "Matt", "Weekly Code", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/YkpUGAw7clY")
         (ggc/new-topic "Matt", "Mine and Mohd's codes combined", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/TswB7bP8BrY")
         (ggc/new-topic "Louis", "about the background things", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/BU72qXLoqoA")
         (ggc/new-topic "Arwyn Davies", "MEETING AT 1300h OUTSIDE THE LINUX LABS. THEN:TEAM SOCIAL.", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/sp6_hGpfFH8")
         (ggc/new-topic "Matt", "Open and Close Console", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/iyKjtfnzUdE")
         (ggc/new-topic "jw12203", "Draft of the Stakeholder Analysis", "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/IRbn54xvN7o")]
        urls (topic-post-urls topics "https://groups.google.com/forum/?_escaped_fragment_=forum/ubu-comp-sci-masters-project-group-4")]
    (testing "get URLs for posts"
      (is (= 40 (count urls)))
      (is
        (= "https://groups.google.com/forum/?_escaped_fragment_=topic/ubu-comp-sci-masters-project-group-4/IRbn54xvN7o"
           (last urls))))
    (testing "fetching posts"
      (let [topic-posts (ggc/html-hickory-pages urls)]
        (is (seq? topic-posts))
        (is (map? (first topic-posts)))
        (is (= :document (:type (first topic-posts))))))))

(deftest parse-posts
  (let [table-row {:type  :element,
                   :attrs nil,
                   :tag   :tr,
                   :content
                          [{:type  :element,
                            :attrs {:class "subject"},
                            :tag   :td,
                            :content
                                   [{:type    :element,
                                     :attrs
                                              {:href
                                                      "https://groups.google.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J",
                                               :title "PM SECTION"},
                                     :tag     :a,
                                     :content ["PM SECTION"]}]}
                           {:type  :element,
                            :attrs {:class "author"},
                            :tag   :td,
                            :content
                                   [{:type    :element,
                                     :attrs   nil,
                                     :tag     :span,
                                     :content ["jw12203"]}]}
                           {:type    :element,
                            :attrs   {:class "lastPostDate"},
                            :tag     :td,
                            :content ["1/29/13 7:09 AM"]}
                           {:type  :element,
                            :attrs {:class "snippet"},
                            :tag   :td,
                            :content
                                   [{:type  :element,
                                     :attrs {:style "overflow:auto"},
                                     :tag   :div,
                                     :content
                                            [{:type  :element,
                                              :attrs {:style "max-height:10000px"},
                                              :tag   :div,
                                              :content
                                                     [{:type    :element,
                                                       :attrs   {:dir "ltr"},
                                                       :tag     :div,
                                                       :content ["See PM section"]}]}
                                             {:type  :element,
                                              :attrs {:style "max-height:10000px"},
                                              :tag   :div,
                                              :content
                                                     [{:type    :element,
                                                       :attrs   {:dir "ltr"},
                                                       :tag     :div,
                                                       :content ["And then see a different thing."]}]}]}]}]}
        topic-post {:type :document,
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
                                   [{:type    :element,
                                     :attrs   nil,
                                     :tag     :head,
                                     :content []}
                                    {:type  :element,
                                     :attrs nil,
                                     :tag   :body,
                                     :content
                                            [{:type :element, :attrs nil, :tag :h2, :content ["PM SECTION"]}
                                             {:type  :element,
                                              :attrs {:border "0", :cellspacing "0"},
                                              :tag   :table,
                                              :content
                                                     [{:type  :element,
                                                       :attrs nil,
                                                       :tag   :tbody,
                                                       :content
                                                              [table-row]}]}]}]}]}]
    (testing "should transform a table row to a PostSummary"
      (let [post-summary (gg-row->PostSummary table-row)]
        (is (record? post-summary))
        (is (string? (to-str post-summary)))
        (is (= "jw12203" (:author post-summary)))
        (is (= "6R-GZotaWk8J" (:post-id post-summary)))
        (is (= "aLpvj-J1W2s" (:topic-id post-summary)))
        (is (= 2013 (jt/as (:date post-summary) :year)))
        (is
         (= "See PM section And then see a different thing."
            (:snippet post-summary)))
        (is
         (= "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J"
            (:email-link post-summary)))))
    (testing "should transform a parsed html doc seq to Topics"
      (let [all-posts (posts (repeat 2 topic-post))]
        (is (record? (second all-posts)))))))

(deftest transform-message-url-to-parseable-url
  (testing "should convert 'https://groups.google.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8'
   to 'https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J'"
    (is
      (=
        (to-raw-url "https://groups.google.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J")
        "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J"))
    (is
      (=
        (to-raw-url "https://groups.google.com/a/domain.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J")
        "https://groups.google.com/a/domain.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J"))))

(deftest all-the-strings
  (let [snippet-td {:type    :element,
                    :attrs   {:class "snippet"},
                    :tag     :td,
                    :content [{:type  :element,
                               :attrs {:style "overflow:auto"},
                               :tag   :div,
                               :content
                                      [{:type  :element,
                                        :attrs {:style "max-height:10000px"},
                                        :tag   :div,
                                        :content
                                               [{:type    :element,
                                                 :attrs   {:dir "ltr"},
                                                 :tag     :div,
                                                 :content ["See PM section" "And QT"]}]}
                                       {:type  :element,
                                        :attrs {:style "max-height:10000px"},
                                        :tag   :div,
                                        :content
                                               [{:type    :element,
                                                 :attrs   {:dir "ltr"},
                                                 :tag     :div,
                                                 :content ["A thing."]}]}]}]}]
    (testing "should return strings"
      (is (= "See PM section And QT A thing." (all-string-content snippet-td))))))

(deftest summarise-topics
  (let
    [post-summaries
     [(->PostSummary "6R-GZotaWk8J", "aLpvj-J1W2s", "PM SECTION", "jw12203", "2013-01-29", "[clojure.lang.LazySeq@5b645dee]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J")
      (->PostSummary "gvltIJFyPE0J", "e9uMeq-0Xyg", "TO DO LIST TUESDAY 22ND JANUARY....", "jw12203", "2013-01-21", "[clojure.lang.LazySeq@e8347808]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/e9uMeq-0Xyg/gvltIJFyPE0J")
      (->PostSummary "XgPycajFyYoJ", "e9uMeq-0Xyg", "Re: TO DO LIST TUESDAY 22ND JANUARY....", "jw12203", "2013-01-21", "[clojure.lang.LazySeq@199116dc]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/e9uMeq-0Xyg/XgPycajFyYoJ")
      (->PostSummary "DlZokN0NkOoJ", "12bVEm_Bbzk", "Latest (Totally Awesome!) Version", "Matt", "2013-01-21", "[clojure.lang.LazySeq@a84ecc74]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/12bVEm_Bbzk/DlZokN0NkOoJ")
      (->PostSummary "IFzbHayRSzQJ", "12bVEm_Bbzk", "Re: Latest (Totally Awesome!) Version", "jw12203", "2013-01-21", "[clojure.lang.LazySeq@3629a09e]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/12bVEm_Bbzk/IFzbHayRSzQJ")
      (->PostSummary "_eX2PEf2snIJ", "9qrFrhGnwrE", "Latest (Currently not really working) version", "Matt", "2013-01-21", "[clojure.lang.LazySeq@c947d040]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/9qrFrhGnwrE/_eX2PEf2snIJ")
      (->PostSummary "azH6zlcSf0cJ", "9qrFrhGnwrE", "Re: Latest (Currently not really working) version", "jw12203", "2013-01-21", "[clojure.lang.LazySeq@934db914]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/9qrFrhGnwrE/azH6zlcSf0cJ")
      (->PostSummary "c--JdvqUQh8J", "1y4HYKuwmOI", "Level Selection", "Matt", "2013-01-21", "[clojure.lang.LazySeq@1]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/1y4HYKuwmOI/c--JdvqUQh8J")
      (->PostSummary "rDulxr2OnXIJ", "1y4HYKuwmOI", "Re: Level Selection", "Matt", "2013-01-21", "[clojure.lang.LazySeq@4c9fcb44]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/1y4HYKuwmOI/rDulxr2OnXIJ")
      (->PostSummary "mRLOyyxYL40J", "1y4HYKuwmOI", "Re: Level Selection", "yc12143", "2013-01-21", "[clojure.lang.LazySeq@1]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/1y4HYKuwmOI/mRLOyyxYL40J")
      (->PostSummary "gDkGQaE_0ckJ", "r8hTOyF7F20", "boss", "jw12203", "2013-01-21", "[clojure.lang.LazySeq@1]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/r8hTOyF7F20/gDkGQaE_0ckJ")
      (->PostSummary "5h6iTBPx7_4J", "76qh8Jz_qG4", "User Manual", "mm1...@my.bristol.ac.uk", "2013-01-20", "[clojure.lang.LazySeq@d6d45588]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/76qh8Jz_qG4/5h6iTBPx7_4J")
      (->PostSummary "c4u0eyWtSN4J", "roG_h87mfk0", "Level 5", "Matt", "2013-01-20", "[clojure.lang.LazySeq@e8957618]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/roG_h87mfk0/c4u0eyWtSN4J")
      (->PostSummary "YlsOFj29BFAJ", "X4IgmxGo5j4", "Level4", "yc12143", "2013-01-18", "[clojure.lang.LazySeq@1]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/YlsOFj29BFAJ")
      (->PostSummary "MUUH7O_xFt0J", "X4IgmxGo5j4", "Re: Level4", "yc12143", "2013-01-18", "[clojure.lang.LazySeq@2909ef6e]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/MUUH7O_xFt0J")
      (->PostSummary "04U97qdv10QJ", "X4IgmxGo5j4", "Re: Level4", "jw12203", "2013-01-19", "[clojure.lang.LazySeq@5987d1d5]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/04U97qdv10QJ")
      (->PostSummary "71r-0arYfqEJ", "X4IgmxGo5j4", "Re: Level4", "yc12143", "2013-01-20", "[clojure.lang.LazySeq@ea045a81]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/71r-0arYfqEJ")
      (->PostSummary "a-s7QloCNBQJ", "X4IgmxGo5j4", "Re: Level4", "jw12203", "2013-01-20", "[clojure.lang.LazySeq@c00935e6]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/a-s7QloCNBQJ")
      (->PostSummary "vrB-bXFuRe0J", "X4IgmxGo5j4", "Re: Level4", "jw12203", "2013-01-20", "[clojure.lang.LazySeq@aa6f3f76]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/vrB-bXFuRe0J")
      (->PostSummary "xVU2IVy7LQEJ", "X4IgmxGo5j4", "Re: Level4", "jw12203", "2013-01-20", "[clojure.lang.LazySeq@e6f69ba4]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/xVU2IVy7LQEJ")
      (->PostSummary "wxM0NbOQgtMJ", "X4IgmxGo5j4", "Re: Level4", "jw12203", "2013-01-20", "[clojure.lang.LazySeq@424e1030]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/X4IgmxGo5j4/wxM0NbOQgtMJ")
      (->PostSummary "Q5hOXH4554UJ", "4RpCgJDx7uA", "Updated code with working enemy sprites", "Matt", "2013-01-19", "[clojure.lang.LazySeq@b83ee883]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/4RpCgJDx7uA/Q5hOXH4554UJ")
      (->PostSummary "n7TpuBaF9sgJ", "S3FYEqDrenc", "Project Management Section....", "jw12203", "2013-01-19", "[clojure.lang.LazySeq@292a5fa1]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/S3FYEqDrenc/n7TpuBaF9sgJ")
      (->PostSummary "xWlSAds9roIJ", "S3FYEqDrenc", "Re: Project Management Section....", "jw12203", "2013-01-19", "[clojure.lang.LazySeq@4e7c7f50]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/S3FYEqDrenc/xWlSAds9roIJ")
      (->PostSummary "enDANcw6kUsJ", "S3FYEqDrenc", "Re: Project Management Section....", "jw12203", "2013-01-19", "[clojure.lang.LazySeq@9b696b82]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/S3FYEqDrenc/enDANcw6kUsJ")
      (->PostSummary "JAiziAWSpNUJ", "d87dTK2TwGI", "IF FUNCTION - Final Version", "mm1...@my.bristol.ac.uk", "2013-01-18", "[clojure.lang.LazySeq@e4f90959]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/d87dTK2TwGI/JAiziAWSpNUJ")
      (->PostSummary "6gMoYeoY18cJ", "-463YuVaC3k", "IF Function", "mm1...@my.bristol.ac.uk", "2013-01-17", "[clojure.lang.LazySeq@95b7b169]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/-463YuVaC3k/6gMoYeoY18cJ")
      (->PostSummary "IrbjfhfAjY4J", "-463YuVaC3k", "Re: IF Function", "mm1...@my.bristol.ac.uk", "2013-01-17", "[clojure.lang.LazySeq@1ccae150]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/-463YuVaC3k/IrbjfhfAjY4J")
      (->PostSummary "SOFpPc_lFFQJ", "-463YuVaC3k", "Re: IF Function", "mm1...@my.bristol.ac.uk", "2013-01-17", "[clojure.lang.LazySeq@eb910f85]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/-463YuVaC3k/SOFpPc_lFFQJ")
      (->PostSummary "p0fTnau0VHQJ", "-463YuVaC3k", "Re: IF Function", "mm1...@my.bristol.ac.uk", "2013-01-17", "[clojure.lang.LazySeq@b27d01f4]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/-463YuVaC3k/p0fTnau0VHQJ")
      (->PostSummary "6caBy9JeYs4J", "-463YuVaC3k", "Re: IF Function", "mm1...@my.bristol.ac.uk", "2013-01-18", "[clojure.lang.LazySeq@6135fdd6]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/-463YuVaC3k/6caBy9JeYs4J")
      (->PostSummary "aHs6S8anWRsJ", "-463YuVaC3k", "Re: IF Function", "mm1...@my.bristol.ac.uk", "2013-01-18", "[clojure.lang.LazySeq@1]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/-463YuVaC3k/aHs6S8anWRsJ")
      (->PostSummary "AEAMsIVCqbAJ", "bx12CglPiG0", "Updated Integrated Sophisticated Game", "Matt", "2013-01-16", "[clojure.lang.LazySeq@c12b7843]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/bx12CglPiG0/AEAMsIVCqbAJ")
      (->PostSummary "U9YsuzPKSIkJ", "GIxU1KAC1ok", "Combined", "Matt", "2013-01-15", "[clojure.lang.LazySeq@7078b6f0]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/GIxU1KAC1ok/U9YsuzPKSIkJ")
      (->PostSummary "7MzE8lqBtH8J", "GIxU1KAC1ok", "Re: Combined", "jw12203", "2013-01-15", "[clojure.lang.LazySeq@c8c5aa05]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/GIxU1KAC1ok/7MzE8lqBtH8J")
      (->PostSummary "UUt7v378T2kJ", "GIxU1KAC1ok", "Re: Combined", "Matt", "2013-01-15", "[clojure.lang.LazySeq@3e802e79]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/GIxU1KAC1ok/UUt7v378T2kJ")
      (->PostSummary "Nv1Ssf4w1DkJ", "gFds_Ztxidc", "Loops", "Matt", "2013-01-14", "[clojure.lang.LazySeq@5b7cef1d]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/gFds_Ztxidc/Nv1Ssf4w1DkJ")
      (->PostSummary "MULID9rbOAwJ", "gFds_Ztxidc", "Re: Loops", "jw12203", "2013-01-14", "[clojure.lang.LazySeq@c37b68cf]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/gFds_Ztxidc/MULID9rbOAwJ")
      (->PostSummary "WelQ5RlP8_EJ", "rlZv8ACrdIk", "2nd level with collsion and death etc etc", "jw12203", "2013-01-14", "[clojure.lang.LazySeq@6595a7d7]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/rlZv8ACrdIk/WelQ5RlP8_EJ")
      (->PostSummary "2f7w--CY1XEJ", "rlZv8ACrdIk", "Re: 2nd level with collsion and death etc etc", "jw12203", "2013-01-14", "[clojure.lang.LazySeq@be220040]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/rlZv8ACrdIk/2f7w--CY1XEJ")
      (->PostSummary "ZPVTc9e_dP0J", "07TKj7s5Eto", "Tightened Code", "Matt", "2013-01-14", "[clojure.lang.LazySeq@b76358de]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/07TKj7s5Eto/ZPVTc9e_dP0J")
      (->PostSummary "HbqA7ietFlUJ", "07TKj7s5Eto", "Re: Tightened Code", "Matt", "2013-01-14", "[clojure.lang.LazySeq@4af48185]", "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/07TKj7s5Eto/HbqA7ietFlUJ")]]
    (testing "should group PostSummarys by topic ID and give summary information "
      (let [topic-summaries (summarise post-summaries)]
        (is (map? topic-summaries))
        (is (< (count topic-summaries) (count post-summaries)))
        (is (= 18 (count topic-summaries)))
        (is (= (get (first (get topic-summaries "bx12CglPiG0")) :post-id)
               "AEAMsIVCqbAJ"))
        ))))
