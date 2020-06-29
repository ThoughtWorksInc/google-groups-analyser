(ns atom-feed-stats.ggcrawler
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-http.client :as client]
            [hickory.core :as hick]
            [hickory.select :as hicks])
  (:gen-class))

(def cookies (atom ""))

(defn init-cookie-store
  "Given a string of cookies (biscuits here in the UK) store it to send with future crawl requests"
  [biccies]
  (reset! cookies biccies))

(def topic-id-re #".{11}$")
(def attrs->href (comp :href :attrs))
(def attrs->title (comp :title :attrs))
(def html->hickory (comp hick/as-hickory hick/parse :body))

(defprotocol TopicFn
  (to-str [_])
  (to-topic-id [_]))

(defrecord Topic [title author link]
  TopicFn
  (to-str [_] (str author ", " title ", " link))
  (to-topic-id [_] (re-find topic-id-re link)))

(defn new-topic
  "Annoying redundant fn due to my cut and paste testing data"
  [author, title, link]
  (->Topic title author link))

(defn forum-page-urls
  "returns a lazy sequence of google group urls using default of 20 topics per page"
  ([url] (forum-page-urls url 1))
  ([url page]
   (lazy-seq
     (cons (str url "%5B" page "-" (+ page 19) "%5D") (forum-page-urls url (+ page 20))))))

(defn html-hickory-pages
  "returns a lazy seq of hickory maps representing the parsed html body"
  [page-url-seq]
  (as-> page-url-seq VAL
        (first VAL)
        (client/get VAL {:headers (if (empty? @cookies) {} {"cookie" @cookies}) :debug false})
        (html->hickory VAL)
        ;        (printlnret VAL)
        (lazy-seq (cons VAL (html-hickory-pages (rest page-url-seq))))))

(defn get-more-topics-link
  "returns the href for the next forum page.  Not used, prefer to generate a lazy sequence of all possible pages."
  [google-group-hickory-doc]
  (->> google-group-hickory-doc
       (hicks/select (hicks/child (hicks/tag :body) (hicks/tag :a)))
       last
       attrs->href))

(defn table-rows
  "takes a sequence of hickory docs and produces a lazy seq of rows"
  [google-group-hickory-docs]
  (when-let [s (seq google-group-hickory-docs)]
    (lazy-seq
      (concat (hicks/select (hicks/child (hicks/tag :body) (hicks/tag :table) (hicks/tag :tbody) (hicks/tag :tr)) (first s))
              (table-rows (rest google-group-hickory-docs))))))

(defn gg-row->Topic [hickory-gg-row]
  (let [a      (->> hickory-gg-row (hicks/select (hicks/tag :a)) first)
        author (->> hickory-gg-row
                    (hicks/select (hicks/child (hicks/class "author") (hicks/tag :span))) first)]
    (->Topic (attrs->title a) (-> author :content first) (attrs->href a))))

(def topics
  "takes a sequence of 'hickory parsed google group pages' and returns a sequence of Topic records"
  (comp #(map gg-row->Topic %) table-rows))

(defn get-topic-id-from-url
  "Gets the end bit of the URL, useful for creating an indexed map.  Not used.  Put in Topic protocol."
  [topic-url] (re-find topic-id-re topic-url))


(def forum-page-sequence
  "given an href for a google group, it'll return an infinite sequence of forum pages"
  (comp html-hickory-pages forum-page-urls))
