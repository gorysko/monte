(ns monte.miners.core
  (:use monte.miners.impl
        [monte.logger :only [dbg err]])
  (:require [monte.dummies :as dummies]
            [clojure.reflect :as r])
  ;(:gen-class)
  )


(def miner-init-cfg {}) ; init data for miners TBD configured later

(def miners-impls (atom {})) ; memoize for miners


(defmacro from-ns[nmsps & body] 
  "launches body from namespace"
  `(binding 
     [*ns* (find-ns ~nmsps)] 
       (eval
          (quote (do ~@body)))))

 
(defprotocol Miner
  "Miner abstraction"
  (f [this] "run miner")
  (get-schema[this] "return a scheme(empty configuration) for miner"))


(defmacro defminer[miner-name & body]
  "defines a miner"
  `(from-ns 'monte.miners.impl
      (deftype ~miner-name [~'config])   
      (extend-type ~miner-name Miner ~@body))
      ; tbd: (import 'monte.miners.impl + miner-name) ?
  )

(defmacro defminer-map[miner-name & body]
  "defines a miner"
    `(defminer ~miner-name 
       (~'f [~'x] ((~@(get (eval `'~@body) :f)) ~'x ))
       (~'get-schema [~'x] ((~@(get (eval `'~@body) :get-schema)) ~'x )))) 
  

(defmacro list-types-implementing[protocol] ; macro as I thought it would find test namespace
  "list all types that implement specified protocol in miner-ns" ; todo: refactor
  `(remove nil? 
     (map #(let [[k# v#] %
         [_# miner-ns# miner-fn#] (re-find #"(.*)\.(.*)$" (.getName k#))]
      (cond (not (nil? (find-ns (symbol miner-ns#))))
        [k# (ns-resolve (find-ns (symbol miner-ns#)) (symbol (str "->" miner-fn#)))]
        :else 
          (do 
            (println (str "Can't load " miner-ns# "/->" miner-fn#))
            nil)
      )
     ) (:impls ~protocol))))


(defn list-all-miners[]
  "returns list[miner-class miner-constructor-func] of all loaded miners"
  (list-types-implementing Miner))


(defn load-extern-miners[path]
  "loads miners from file path"
  ;; WARNING: not secure. Use on your own risk
  (binding [*ns* (find-ns 'monte.miners.core)] (load-file path))) 


(defn get-miner-impl [type]
  (when-not (@miners-impls type) 
    (when-let [miner (first (filter #(= (first %) type) (list-all-miners)))]
      (swap! miners-impls merge { type ((miner 1) miner-init-cfg) })))
  (@miners-impls type))


(defn list-miners [cb]
  "return all miners formatted by callback cb(miner-type, miner-impl)"
  (map #(let [[type _] %  impl (get-miner-impl type)]
                (cb type impl)) (list-all-miners)))



;;;;;;;;;;;;
; miner impls

  
(defminer JIRAMiner
  (f [this]     
     (let [cfg (.config this)] ; use cfg later
       monte.dummies/tasks))
  
  (get-schema [this] 
    {})) ; tbd

#_(defminer VCSMiner
  (f [this]     
     (let [cfg (.config this)] ; use cfg later
       monte.dummies/commits))
  
  (get-schema [this] 
    {})) ; tbd


#_(defminer SRCMiner
  (f [this]     
     (let [cfg (.config this)] ; use cfg later
       monte.dummies/src-analysis-data))
  
  (get-schema [this] 
    {})) ; tbd
