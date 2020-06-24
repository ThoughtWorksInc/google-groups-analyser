(ns atom-feed-stats.ggposts-test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pp]
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
               (is (= :document (:type (first topic-posts))))
               ))))

(deftest parse-posts
  (let [topic-post
        {:type :document,
         :content
         [{:type :document-type,
           :attrs
           {:name "html",
            :publicid "-//W3C//DTD HTML 4.01//EN",
            :systemid "http://www.w3.org/TR/html4/strict.dtd"}}
          {:type :element,
           :attrs nil,
           :tag :html,
           :content
           [{:type :element,
             :attrs nil,
             :tag :head,
             :content
             ["\n\n"
              {:type :element,
               :attrs nil,
               :tag :title,
               :content ["PM SECTION - Google Groups"]}
              "\n"
              {:type :element,
               :attrs
               {:rel "canonical",
                :href
                "https://groups.google.com/d/topic/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s"},
               :tag :link,
               :content nil}
              "\n"
              {:type :element,
               :attrs {:property "og:title", :content "PM SECTION"},
               :tag :meta,
               :content nil}
              "\n"
              {:type :element,
               :attrs {:property "og:type", :content "website"},
               :tag :meta,
               :content nil}
              "\n"
              {:type :element,
               :attrs
               {:property "og:url",
                :content
                "https://groups.google.com/forum/#!topic/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s"},
               :tag :meta,
               :content nil}
              "\n"
              {:type :element,
               :attrs
               {:property "og:image",
                :content
                "http://www.google.com/images/icons/product/groups-128.png"},
               :tag :meta,
               :content nil}
              "\n"
              {:type :element,
               :attrs {:property "og:site_name", :content "Google Groups"},
               :tag :meta,
               :content nil}
              "\n"
              {:type :element,
               :attrs
               {:property "og:description", :content "Posted 29/01/13 07:09"},
               :tag :meta,
               :content nil}]}
            "\n"
            {:type :element,
             :attrs nil,
             :tag :body,
             :content
             ["\n\n\n"
              {:type :element, :attrs nil, :tag :h2, :content ["PM SECTION"]}
              "\n"
              {:type :element,
               :attrs nil,
               :tag :i,
               :content ["Showing 1-1 of 1 messages"]}
              "\n"
              {:type :element,
               :attrs {:border "0", :cellspacing "0"},
               :tag :table,
               :content
               [{:type :element,
                 :attrs nil,
                 :tag :tbody,
                 :content
                 [{:type :element,
                   :attrs nil,
                   :tag :tr,
                   :content
                   [{:type :element,
                     :attrs {:class "subject"},
                     :tag :td,
                     :content
                     [{:type :element,
                       :attrs
                       {:href
                        "https://groups.google.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J",
                        :title "PM SECTION"},
                       :tag :a,
                       :content ["PM SECTION"]}]}
                    "\n"
                    {:type :element,
                     :attrs {:class "author"},
                     :tag :td,
                     :content
                     [{:type :element,
                       :attrs nil,
                       :tag :span,
                       :content ["jw12203"]}]}
                    "\n"
                    {:type :element,
                     :attrs {:class "lastPostDate"},
                     :tag :td,
                     :content ["29/01/13 07:09"]}
                    "\n"
                    {:type :element,
                     :attrs {:class "snippet"},
                     :tag :td,
                     :content
                     [{:type :element,
                       :attrs {:style "overflow:auto"},
                       :tag :div,
                       :content
                       [{:type :element,
                         :attrs {:style "max-height:10000px"},
                         :tag :div,
                         :content
                         [{:type :element,
                           :attrs {:dir "ltr"},
                           :tag :div,
                           :content ["See PM section"]}]}]}]}]}]}]}
              "\n"]}]}]}
        topic-posts (repeat topic-post)
        ]
    (testing "parsing posts"
             (is (map? topic-post))
             (is (= :document (:type topic-post)))
             )
    (testing "should transform a parsed html row list to a Topic list"
             (let [rows (ggc/table-rows (take 2 topic-posts))]
               (is (= 2 (count rows)))
               (is (= :tr (-> rows first :tag)))
               (is (vector? (-> rows first :content)))
               (is (= :tr (-> rows second :tag)))
               (is (vector? (-> rows second :content)))))
    (testing "should transform a parsed html doc seq to Topics"
             (let [all-posts (posts (take 2 topic-posts))]
               (is (record? (first all-posts)))
               (is (string? (to-str (first all-posts))))
               (is (= "jw12203" (:author (first all-posts))))
               (is (= "6R-GZotaWk8J" (:post-id (first all-posts))))
               (is (= "aLpvj-J1W2s" (:topic-id (first all-posts))))
               (is (= "29/01/13 07:09" (:date (first all-posts))))
               (is (= "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J" (:email-link (first all-posts))))
    ))))

(deftest transform-message-url-to-parseable-url
  (testing "should convert 'https://groups.google.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8' to 'https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J'"
           (is (= (to-raw-url "https://groups.google.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J") "https://groups.google.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J"))
           (is (= (to-raw-url "https://groups.google.com/a/domain.com/d/msg/ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J") "https://groups.google.com/a/domain.com/forum/message/raw?msg=ubu-comp-sci-masters-project-group-4/aLpvj-J1W2s/6R-GZotaWk8J"))))