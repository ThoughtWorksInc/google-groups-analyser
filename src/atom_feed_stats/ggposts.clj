(ns atom-feed-stats.ggposts
  (:require [atom-feed-stats.ggcrawler :as ggc]
            [atom-feed-stats.ggatomparser :as gga]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]
            [java-time :as jt]
            [hickory.core :as hick]
            [hickory.select :as hicks])
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

(defn all-string-content [hickory-map]
  (->> hickory-map
       (hicks/select (hicks/tag :div))
       (map #(:content %))
       flatten
       (filter string?)
       (interpose " ")
       (apply str)))

(defprotocol PostSummaryFn
  (to-str [_]))

(defrecord PostSummary [post-id topic-id title author date snippet email-link]
  PostSummaryFn
  (to-str [_]
    (str post-id "/" topic-id ", " title ", " author ", " date ", [" "], " email-link)))

(defn to-local-date [date-str]
  (jt/local-date "M/d/yy H:ss a" date-str))

(defn gg-row->PostSummary [hickory-gg-row]
  (try
    (let [a                                        (->> hickory-gg-row (hicks/select (hicks/tag :a)) first)
          author                                   (->> hickory-gg-row
                                                        (hicks/select (hicks/child (hicks/class "author") (hicks/tag :span))) first)
          post-date                                (->> hickory-gg-row
                                                        (hicks/select (hicks/class "lastPostDate")) first)
          jt-date                                  (to-local-date (-> post-date :content first))
          [link enterprise forum topic-id post-id] (re-find post-id-re (ggc/attrs->href a))
          snippet                                  (->> hickory-gg-row
                                                        (hicks/select (hicks/and (hicks/class "snippet") (hicks/tag :td))) first)]
      (->PostSummary post-id
                     topic-id
                     (-> a ggc/attrs->title (str/replace "," ""))
                     (-> author :content first)
                     jt-date
                     (-> snippet all-string-content (str/replace "," "") (str/replace "/n" ""))
                     (to-raw-url enterprise forum topic-id post-id)))
    (catch Exception e
      (println "\n" (.getMessage e)))))

(def posts
  "takes a sequence of 'hickory parsed google group topic pages' and returns a sequence of PostSummary records"
  (comp #(map gg-row->PostSummary %)
        ggc/table-rows))

(defn summarise [posts]
  "groups PostSummary records by their topic-id"
  (->> posts
       (group-by :topic-id)
       gga/map-to-ThreadStat
       ))



