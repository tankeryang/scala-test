package com.yang.spark.etl


object SpqrkETLExecutor {

  def main(args: Array[String]): Unit = {

    val executor = new SparkETL(args)
    executor.execute
  }
}
