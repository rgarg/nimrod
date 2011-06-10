(ns nimrod.core.metrics-test
 (:use
   [clojure.test]
   [nimrod.core.metrics])
 )

(defn- read-status [status-ns status-id]
  (read-metric (metric-types :statuses) status-ns status-id)
  )

(defn- update-status
  ([status-ns status-id timestamp value]
    (set-metric (metric-types :statuses) status-ns status-id timestamp value #{}))
  ([status-ns status-id timestamp value tags]
    (set-metric (metric-types :statuses) status-ns status-id timestamp value tags))
  )

(defn- list-statuses [status-ns]
  (list-metrics (metric-types :statuses) status-ns)
  )

(defn- read-status-history
  ([status-ns status-id]
    ((read-history (metric-types :statuses) status-ns status-id nil) :values)
    )
  ([status-ns status-id tags]
    ((read-history (metric-types :statuses) status-ns status-id tags) :values)
    )
  )

(defn- reset-status-history [status-ns status-id limit]
  (reset-history (metric-types :statuses) status-ns status-id limit)
  )

(defn- flush-statuses-in [status-ns]
  (flush-metrics (metric-types :statuses) status-ns)
  )

(defn- read-gauge [gauge-ns gauge-id]
  (read-metric (metric-types :gauges) gauge-ns gauge-id)
  )

(defn- update-gauge
  ([gauge-ns gauge-id timestamp value]
    (set-metric (metric-types :gauges) gauge-ns gauge-id timestamp value #{}))
  ([gauge-ns gauge-id timestamp value tags]
    (set-metric (metric-types :gauges) gauge-ns gauge-id timestamp value tags))
  )

(defn- list-gauges [gauge-ns]
  (list-metrics (metric-types :gauges) gauge-ns)
  )

(defn- read-gauge-history
  ([gauge-ns gauge-id]
    ((read-history (metric-types :gauges) gauge-ns gauge-id nil) :values)
    )
  ([gauge-ns gauge-id tags]
    ((read-history (metric-types :gauges) gauge-ns gauge-id tags) :values)
    )
  )

(defn- reset-gauge-history [gauge-ns gauge-id limit]
  (reset-history (metric-types :gauges) gauge-ns gauge-id limit)
  )

(defn- flush-gauges-in [gauge-ns]
  (flush-metrics (metric-types :gauges) gauge-ns)
  )

(defn- read-counter [counter-ns counter-id]
  (read-metric (metric-types :counters) counter-ns counter-id)
  )

(defn- update-counter 
  ([counter-ns counter-id timestamp value]
    (set-metric (metric-types :counters) counter-ns counter-id timestamp value #{}))
  ([counter-ns counter-id timestamp value tags]
    (set-metric (metric-types :counters) counter-ns counter-id timestamp value tags))
  )

(defn- list-counters [counter-ns]
  (list-metrics (metric-types :counters) counter-ns)
  )

(defn- read-counter-history
  ([counter-ns counter-id]
    ((read-history (metric-types :counters) counter-ns counter-id nil) :values)
    )
  ([counter-ns counter-id tags]
    ((read-history (metric-types :counters) counter-ns counter-id tags) :values)
    )
  )

(defn- reset-counter-history [counter-ns counter-id limit]
  (reset-history (metric-types :counters) counter-ns counter-id limit)
  )

(defn- flush-counters-in [counter-ns]
  (flush-metrics (metric-types :counters) counter-ns)
  )

(defn- read-timer [timer-ns timer-id]
  (read-metric (metric-types :timers) timer-ns timer-id)
  )

(defn- update-timer 
  ([timer-ns timer-id timestamp value]
    (set-metric (metric-types :timers) timer-ns timer-id timestamp value #{}))
  ([timer-ns timer-id timestamp value tags]
    (set-metric (metric-types :timers) timer-ns timer-id timestamp value tags))
  )

(defn- list-timers [timer-ns]
  (list-metrics (metric-types :timers) timer-ns)
  )

(defn- read-timer-history
  ([timer-ns timer-id]
    ((read-history (metric-types :timers) timer-ns timer-id nil) :values)
    )
  ([timer-ns timer-id tags]
    ((read-history (metric-types :timers) timer-ns timer-id tags) :values)
    )
  )

(defn- reset-timer-history [timer-ns timer-id limit]
  (reset-history (metric-types :timers) timer-ns timer-id limit)
  )

(defn- flush-timers-in [timer-ns]
  (flush-metrics (metric-types :timers) timer-ns)
  )

(deftest status-metrics
  (testing "Null status"
    (is (nil? (read-status "status-metrics" "1")))
    )
  (testing "Initial status value"
    (update-status "status-metrics" "1" "1" "v1")
    (flush-statuses-in "status-metrics")
    (is (not (nil? (read-status "status-metrics" "1"))))
    (is (= 1 ((read-status "status-metrics" "1") :timestamp)))
    (is (= "v1" ((read-status "status-metrics" "1") :status)))
    )
  (testing "Updated status value"
    (update-status "status-metrics" "1" "2" "v2")
    (flush-statuses-in "status-metrics")
    (is (not (nil? (read-status "status-metrics" "1"))))
    (is (= 2 ((read-status "status-metrics" "1") :timestamp)))
    (is (= "v2" ((read-status "status-metrics" "1") :status)))
    )
  (testing "List statuses"
    (is (= ["1"] (list-statuses "status-metrics")))
    )
  )

(deftest status-history
  (testing "Reset status history"
    (reset-status-history "status-history" "1" 2)
    )
  (testing "Status history under limit"
    (update-status "status-history" "1" "1" "v1")
    (update-status "status-history" "1" "2" "v2")
    (flush-statuses-in "status-history")
    (is (= 1 ((first (read-status-history "status-history" "1")) :timestamp)))
    (is (= 2 ((second (read-status-history "status-history" "1")) :timestamp)))
    )
  (testing "Status history over limit"
    (update-status "status-history" "1" "3" "v3")
    (flush-statuses-in "status-history")
    (is (= 2 ((first (read-status-history "status-history" "1")) :timestamp)))
    (is (= 3 ((second (read-status-history "status-history" "1")) :timestamp)))
    )
  )

(deftest status-history-with-tags
  (testing "Status history with tags"
    (update-status "status-history-with-tags" "1" "1" "v1" #{"tag1", "tag2"})
    (update-status "status-history-with-tags" "1" "2" "v2" #{"tag3"})
    (flush-statuses-in "status-history-with-tags")
    (is (= 1 (count (read-status-history "status-history-with-tags" "1" #{"tag1", "tag2"}))))
    (is (= 1 ((first (read-status-history "status-history-with-tags" "1" #{"tag1", "tag2"})) :timestamp)))
    )
  )

(deftest gauge-metrics
  (testing "Null gauge"
    (is (nil? (read-gauge "gauge-metrics" "1")))
    )
  (testing "Initial gauge values"
    (update-gauge "gauge-metrics" "1" "2" "4")
    (flush-gauges-in "gauge-metrics")
    (is (not (nil? (read-gauge "gauge-metrics" "1"))))
    (is (= 2 ((read-gauge "gauge-metrics" "1") :timestamp)))
    (is (= 4 ((read-gauge "gauge-metrics" "1") :gauge)))
    (is (= 4 ((read-gauge "gauge-metrics" "1") :gauge-average)))
    (is (= 0 ((read-gauge "gauge-metrics" "1") :gauge-variance)))
    (is (= 0 ((read-gauge "gauge-metrics" "1") :interval-average)))
    (is (= 0 ((read-gauge "gauge-metrics" "1") :interval-variance)))
    )
  (testing "Updated gauge values"
    (update-gauge "gauge-metrics" "1" "4" "6")
    (flush-gauges-in "gauge-metrics")
    (is (not (nil? (read-gauge "gauge-metrics" "1"))))
    (is (= 4 ((read-gauge "gauge-metrics" "1") :timestamp)))
    (is (= 6 ((read-gauge "gauge-metrics" "1") :gauge)))
    (is (= 5 ((read-gauge "gauge-metrics" "1") :gauge-average)))
    (is (= 2 ((read-gauge "gauge-metrics" "1") :gauge-variance)))
    (is (= 2 ((read-gauge "gauge-metrics" "1") :interval-average)))
    (is (= 0 ((read-gauge "gauge-metrics" "1") :interval-variance)))
    )
  (testing "List gauges"
    (is (= ["1"] (list-gauges "gauge-metrics")))
    )
  )

(deftest gauge-history
  (testing "Reset gauge history"
    (reset-gauge-history "gauge-history" "1" 2)
    )
  (testing "Gauge history under limit"
    (update-gauge "gauge-history" "1" "1" "1")
    (update-gauge "gauge-history" "1" "2" "2")
    (flush-gauges-in "gauge-history")
    (is (= 1 ((first (read-gauge-history "gauge-history" "1")) :timestamp)))
    (is (= 2 ((second (read-gauge-history "gauge-history" "1")) :timestamp)))
    )
  (testing "Gauge history over limit"
    (update-gauge "gauge-history" "1" "3" "3")
    (flush-gauges-in "gauge-history")
    (is (= 2 ((first (read-gauge-history "gauge-history" "1")) :timestamp)))
    (is (= 3 ((second (read-gauge-history "gauge-history" "1")) :timestamp)))
    )
  )

(deftest gauge-history-with-tags
  (testing "Gauge history with tags"
    (update-gauge "gauge-history-with-tags" "1" "1" "1" #{"tag1", "tag2"})
    (update-gauge "gauge-history-with-tags" "1" "2" "2" #{"tag3"})
    (flush-gauges-in "gauge-history-with-tags")
    (is (= 1 (count (read-gauge-history "gauge-history-with-tags" "1" #{"tag1", "tag2"}))))
    (is (= 1 ((first (read-gauge-history "gauge-history-with-tags" "1" #{"tag1", "tag2"})) :timestamp)))
    )
  )

(deftest counter-metrics
  (testing "Null counter"
    (is (nil? (read-counter "counter-metrics" "1")))
    )
  (testing "Initial counter values"
    (update-counter "counter-metrics" "1" "2" "4")
    (flush-counters-in "counter-metrics")
    (is (not (nil? (read-counter "counter-metrics" "1"))))
    (is (= 2 ((read-counter "counter-metrics" "1") :timestamp)))
    (is (= 4 ((read-counter "counter-metrics" "1") :counter)))
    (is (= 4 ((read-counter "counter-metrics" "1") :increment-average)))
    (is (= 0 ((read-counter "counter-metrics" "1") :increment-variance)))
    (is (= 4 ((read-counter "counter-metrics" "1") :latest-increment)))
    (is (= 0 ((read-counter "counter-metrics" "1") :interval-average)))
    (is (= 0 ((read-counter "counter-metrics" "1") :interval-variance)))
    (is (= 0 ((read-counter "counter-metrics" "1") :latest-interval)))
    )
  (testing "Updated counter values"
    (update-counter "counter-metrics" "1" "4" "6")
    (flush-counters-in "counter-metrics")
    (is (not (nil? (read-counter "counter-metrics" "1"))))
    (is (= 4 ((read-counter "counter-metrics" "1") :timestamp)))
    (is (= 10 ((read-counter "counter-metrics" "1") :counter)))
    (is (= 5 ((read-counter "counter-metrics" "1") :increment-average)))
    (is (= 2 ((read-counter "counter-metrics" "1") :increment-variance)))
    (is (= 6 ((read-counter "counter-metrics" "1") :latest-increment)))
    (is (= 2 ((read-counter "counter-metrics" "1") :interval-average)))
    (is (= 0 ((read-counter "counter-metrics" "1") :interval-variance)))
    (is (= 2 ((read-counter "counter-metrics" "1") :latest-interval)))
    )
  (testing "List counters"
    (is (= ["1"] (list-counters "counter-metrics")))
    )
  )

(deftest counter-history
  (testing "Reset counter history"
    (reset-counter-history "counter-history" "1" 2)
    )
  (testing "Counter history under limit"
    (update-counter "counter-history" "1" "1" "1")
    (update-counter "counter-history" "1" "2" "2")
    (flush-counters-in "counter-history")
    (is (= 1 ((first (read-counter-history "counter-history" "1")) :timestamp)))
    (is (= 2 ((second (read-counter-history "counter-history" "1")) :timestamp)))
    )
  (testing "Counter history over limit"
    (update-counter "counter-history" "1" "3" "3")
    (flush-counters-in "counter-history")
    (is (= 2 ((first (read-counter-history "counter-history" "1")) :timestamp)))
    (is (= 3 ((second (read-counter-history "counter-history" "1")) :timestamp)))
    )
  )

(deftest counter-history-with-tags
  (testing "Counter history with tags"
    (update-counter "counter-history-with-tags" "1" "1" "1" #{"tag1", "tag2"})
    (update-counter "counter-history-with-tags" "1" "2" "2" #{"tag3"})
    (flush-counters-in "counter-history-with-tags")
    (is (= 1 (count (read-counter-history "counter-history-with-tags" "1" #{"tag1", "tag2"}))))
    (is (= 1 ((first (read-counter-history "counter-history-with-tags" "1" #{"tag1", "tag2"})) :timestamp)))
    )
  )

(deftest timer-metrics
  (testing "Null timer"
    (is (nil? (read-timer "timer-metrics" "1")))
    )
  (testing "Start timer"
    (update-timer "timer-metrics" "1" "2" "start")
    (flush-timers-in "timer-metrics")
    (is (not (nil? (read-timer "timer-metrics" "1"))))
    (is (= 2 ((read-timer "timer-metrics" "1") :start)))
    (is (= 0 ((read-timer "timer-metrics" "1") :end)))
    (is (= 0 ((read-timer "timer-metrics" "1") :elapsed-time)))
    (is (= 0 ((read-timer "timer-metrics" "1") :elapsed-time-average)))
    (is (= 0 ((read-timer "timer-metrics" "1") :elapsed-time-variance)))
    )
  (testing "Stop timer"
    (update-timer "timer-metrics" "1" "4" "stop")
    (flush-timers-in "timer-metrics")
    (is (not (nil? (read-timer "timer-metrics" "1"))))
    (is (= 2 ((read-timer "timer-metrics" "1") :start)))
    (is (= 4 ((read-timer "timer-metrics" "1") :end)))
    (is (= 2 ((read-timer "timer-metrics" "1") :elapsed-time)))
    (is (= 2 ((read-timer "timer-metrics" "1") :elapsed-time-average)))
    (is (= 0 ((read-timer "timer-metrics" "1") :elapsed-time-variance)))
    )
  (testing "Restart timer"
    (update-timer "timer-metrics" "1" "6" "start")
    (flush-timers-in "timer-metrics")
    (is (not (nil? (read-timer "timer-metrics" "1"))))
    (is (= 6 ((read-timer "timer-metrics" "1") :start)))
    (is (= 0 ((read-timer "timer-metrics" "1") :end)))
    (is (= 0 ((read-timer "timer-metrics" "1") :elapsed-time)))
    (is (= 2 ((read-timer "timer-metrics" "1") :elapsed-time-average)))
    (is (= 0 ((read-timer "timer-metrics" "1") :elapsed-time-variance)))
    (update-timer "timer-metrics" "1" "10" "stop")
    (flush-timers-in "timer-metrics")
    (is (not (nil? (read-timer "timer-metrics" "1"))))
    (is (= 6 ((read-timer "timer-metrics" "1") :start)))
    (is (= 10 ((read-timer "timer-metrics" "1") :end)))
    (is (= 4 ((read-timer "timer-metrics" "1") :elapsed-time)))
    (is (= 3 ((read-timer "timer-metrics" "1") :elapsed-time-average)))
    (is (= 2 ((read-timer "timer-metrics" "1") :elapsed-time-variance)))
    )
  (testing "List timers"
    (is (= ["1"] (list-timers "timer-metrics")))
    )
  )

(deftest timer-history
  (testing "Reset timer history"
    (reset-timer-history "timer-history" "1" 2)
    )
  (testing "Timer history under limit"
    (update-timer "timer-history" "1" "1" "start")
    (update-timer "timer-history" "1" "2" "stop")
    (flush-timers-in "timer-history")
    (is (= 1 ((first (read-timer-history "timer-history" "1")) :timestamp)))
    (is (= 2 ((second (read-timer-history "timer-history" "1")) :timestamp)))
    )
  (testing "Timer history over limit"
    (update-timer "timer-history" "1" "3" "start")
    (flush-timers-in "timer-history")
    (is (= 2 ((first (read-timer-history "timer-history" "1")) :timestamp)))
    (is (= 3 ((second (read-timer-history "timer-history" "1")) :timestamp)))
    )
  )

(deftest timer-history-with-tags
  (testing "Timer history with tags"
    (update-timer "timer-history-with-tags" "1" "1" "start" #{"tag1", "tag2"})
    (update-timer "timer-history-with-tags" "1" "2" "stop" #{"tag3"})
    (flush-timers-in "timer-history-with-tags")
    (is (= 1 (count (read-timer-history "timer-history-with-tags" "1" #{"tag1", "tag2"}))))
    (is (= 1 ((first (read-timer-history "timer-history-with-tags" "1" #{"tag1", "tag2"})) :timestamp)))
    )
  )