package com.yang.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamingDemo {
  
  def main(args: Array[String]): Unit = {
    
    val conf = new SparkConf().setMaster("local[2]").setAppName("ss")
    val ssc = new StreamingContext(conf, Seconds(5))
    
    ssc.sparkContext.setLogLevel("ERROR")
    ssc.checkpoint(".")
    
    val lines = ssc.socketTextStream("localhost", 9999)
    val wc = lines.flatMap(_.split(" ")).map((_, 1)).updateStateByKey[Int](updateFunction(_, _))
    
    wc.print()
    
    ssc.start()
    ssc.awaitTermination()
  }
  
  /**
    * 把当前的数据去更新已有的或者是老的数据
    *
    * @param curValues
    * @param preValues
    * @return
    */
  def updateFunction(curValues: Seq[Int], preValues: Option[Int]): Option[Int] = {
    val cur = curValues.sum
    val pre = preValues.getOrElse(0)
    
    Some(cur + pre)
  }
}
