(ns atom-feed-stats.ggatomparser
  (:require [java-time :as jt]
            [clojure.java.io :as io]
            [clojure.xml :as xml])
  (:gen-class))

(defrecord Entry [thread-id name email updated title summary])

(defn parse-atom [resource]
  (-> resource io/resource io/file xml/parse))

(defn get-element [entry tag-name]
  (->> entry
       :content
       (filter #(-> % :tag (= tag-name)))
       first))

(defn get-element-content [entry tag-name]
  (-> (get-element entry tag-name)
      :content
      first))

(defn feed-to-entry-seq [feed]
  (->> feed
       :content
       (filter #(= (:tag %) :entry))
       (map
        #(->Entry
          (get-element-content % :id)
          (get-element-content (get-element % :author) :name)
          (get-element-content (get-element % :author) :email)
          (jt/instant (get-element-content % :updated))
          (get-element-content % :title)
          (get-element-content % :summary)))))

(defn entry-reducer [entry-seq entry-field func]
  (->> entry-seq
       (map #(-> % entry-field))
       (apply func)))

(defn oldest-entry-instant [entry-seq]
  (entry-reducer entry-seq :updated jt/min))

(defn newest-entry-instant [entry-seq]
  (entry-reducer entry-seq :updated jt/max))

(defn group-entries-by-thread-id [entries]
  (group-by :thread-id entries))

(defn group-entries-by-email [entries]
  (group-by :email entries))

(defprotocol Values (get-values [_]))

(defrecord ThreadStat
  [thread-id
   title
   initiator
   email-count
   days-span
   unique-contributor-count
   unique-contributors]
  Values
  (get-values [_] [thread-id
                   title
                   initiator
                   email-count
                   days-span
                   unique-contributor-count
                   unique-contributors]))

(defn map-to-ThreadStat [threads]
  (map
   #(let [k             (key %)
          v             (val %)
          f             (first v)
          unique-emails (group-entries-by-email v)]
     (->ThreadStat k
                   (:title f)
                   (:name f)
                   (count v)
                   (jt/time-between (oldest-entry-instant v) (newest-entry-instant v) :days)
                   (count unique-emails)
                   (keys unique-emails)))
   threads))