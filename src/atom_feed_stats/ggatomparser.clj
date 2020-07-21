(ns atom-feed-stats.ggatomparser
  (:require [java-time :as jt])
  (:gen-class))


(defn entry-reducer [entry-seq entry-field func]
  (->> entry-seq
       (map #(-> % entry-field))
       (apply func)
       (jt/local-date)))

(defn oldest-entry-instant [entry-seq]
  (entry-reducer entry-seq :date jt/min))

(defn newest-entry-instant [entry-seq]
  (entry-reducer entry-seq :date jt/max))

(defn thread-emails-with-counts [entries]
  (->> entries
       (group-by :author)
       (map #(str (key %) " " (count (val %))))
       sort))

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
    #(let [topic-id (key %)
           thread (val %)
           first-thread (first thread)
           thread-emails-by-author (thread-emails-with-counts thread)]
       (->ThreadStat topic-id
                     (:title first-thread)
                     (:author first-thread)
                     (count thread)
                     (jt/time-between (oldest-entry-instant thread) (newest-entry-instant thread) :days)
                     (count thread-emails-by-author)
                     (seq thread-emails-by-author)))
    threads))