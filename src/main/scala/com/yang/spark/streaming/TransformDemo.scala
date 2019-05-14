package com.yang.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * 黑名单过滤
  */
object TransformDemo {
  
  def main(args: Array[String]): Unit = {
    
    val conf = new SparkConf().setMaster("local[2]").setAppName("ss")
    val ssc = new StreamingContext(conf, Seconds(5))
    
    ssc.sparkContext.setLogLevel("ERROR")
    ssc.checkpoint(".")
  
    /*
     * 构建黑名单
     */
    val blackList = List("zs", "ls")
    val blackListRDD = ssc.sparkContext.parallelize(blackList).map((_,  true))
    
    val lines = ssc.socketTextStream("localhost", 9999)
    val clickLog = lines.map(x => (x.split(", ")(1), x)).transform(rdd => {
      rdd.leftOuterJoin(blackListRDD)
        .filter(x => !x._2._2.getOrElse(false))
        .map(x => x._2._1)
    })
    
    clickLog.print()
    
    ssc.start()
    ssc.awaitTermination()
  }
  

}
