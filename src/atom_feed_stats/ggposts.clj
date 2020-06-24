(ns atom-feed-stats.ggposts
  (:require [atom-feed-stats.ggcrawler :refer [TopicFn] :as ggc]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]
            [hickory.core :as hick]
            [hickory.select :as hicks])
  (:import atom_feed_stats.ggcrawler.Topic)
  (:gen-class))

(def post-id-re #".{11}/.{12}$")

(defn topic-post-urls [topics forum-url-prefix] (map #(str (str/replace forum-url-prefix #"_=forum/" "_=topic/") "/" (ggc/to-topic-id %)) topics))

;(defn post-urls [topics forum-url-prefix] (map #(str (str/replace forum-url-prefix #"_=forum/" "_=topic/") "/" (ggc/to-topic-id %)) topics))
