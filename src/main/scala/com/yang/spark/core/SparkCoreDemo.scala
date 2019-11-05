package com.yang.spark.core

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Hello world!
  *
  */
object SparkCoreDemo {
  
  def main(args: Array[String]): Unit = {
    val cnf = new SparkConf().setMaster("local[3]").setAppName("test")
    val sc = new SparkContext(cnf)
    
    /*
     * word count 示例
     */
    val path = getClass.getResource("/text.txt").getPath
    val rdd = sc.textFile(path)
    val wc = rdd.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    wc.foreach(println)
    
    /*
     * rdd 分区计算示例
     */
    val a = sc.parallelize(1 to 9, 3)
    a.foreach(println)
    def iterFunc[T](i: Iterator[T]): Iterator[(T, T)] = {
      var res = List[(T, T)]()
      var pre = i.next
      while (i.hasNext) {
        val cur = i.next
        res ::=  (pre, cur)
        pre = cur
      }
      res.iterator
    }
    a.mapPartitions(iterFunc).foreach(println)
  }
}
