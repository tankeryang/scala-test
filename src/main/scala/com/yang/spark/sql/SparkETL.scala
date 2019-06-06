package com.yang.spark.sql

import org.apache.spark.sql.SparkSession
import scalaj.http.Http

import scala.collection.mutable


object SparkETL {

  var sqlFile: mutable.ListMap[String, String] = mutable.ListMap[String, String]()

  def main(args: Array[String]): Unit = {

    val ss = SparkSession.builder()
      .appName(args(0))
      .getOrCreate()

    val url: String = args(1)
    val sqlNames: Array[String] = args(2).split(",")
  }

  def getSql(url: String) = {
    val response = Http(url).asString

    if (response.isError) System.exit(1)
    else response.body.stripLineEnd
  }

  def getSqlFile = {

  }
}
