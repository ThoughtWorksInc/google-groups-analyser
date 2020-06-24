(ns atom-feed-stats.ggposts
  (:require [atom-feed-stats.ggcrawler
             :refer [TopicFn]
             :as    ggc]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]
            [hickory.core :as hick]
            [hickory.select :as hicks])
  (:import atom_feed_stats.ggcrawler.Topic)
  (:gen-class))

(def post-id-re #"^https://groups\.google\.com/(.*)d/msg/(.*)/(.{11})/(.{12})$")

(defn to-raw-url
  "takes a google link to it's forum post web app page and converts it to a link where the contents can be parsed"
  ([url]
   (let [[_ enterprise forum topic-id post-id] (re-find post-id-re url)]
     (to-raw-url enterprise forum topic-id post-id)))
  ([enterprise forum topic-id post-id]
   (str "https://groups.google.com/" enterprise "forum/message/raw?msg=" forum "/" topic-id "/" post-id)))

(defn topic-post-urls [topics forum-url-prefix]
  (map
   #(str (str/replace forum-url-prefix #"_=forum/" "_=topic/") "/" (ggc/to-topic-id %))
   topics))

(defprotocol PostSummaryFn
  (to-str [_]))

(defrecord PostSummary [post-id topic-id title author date snippet email-link]
  PostSummaryFn
  (to-str [_]
    (str post-id "/" topic-id ", " title ", " author ", " date ", [" snippet "], " email-link)))

(defn gg-row->PostSummary [hickory-gg-row]
  (let [a                                        (->> hickory-gg-row (hicks/select (hicks/tag :a)) first)
        author                                   (->> hickory-gg-row
                                                      (hicks/select (hicks/child (hicks/class "author") (hicks/tag :span))) first)
        post-date                                (->> hickory-gg-row
                                                      (hicks/select (hicks/class "lastPostDate")) first)
        [link enterprise forum topic-id post-id] (re-find post-id-re (ggc/attrs->href a))]
    (->PostSummary post-id topic-id (ggc/attrs->title a) (-> author :content first) (-> post-date :content first) "snippet" (to-raw-url enterprise forum topic-id post-id))))

(def posts
  "takes a sequence of 'hickory parsed google group topic pages' and returns a sequence of PostSummary records"
  (comp #(map gg-row->PostSummary %) ggc/table-rows))