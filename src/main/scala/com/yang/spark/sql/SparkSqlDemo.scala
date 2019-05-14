package com.yang.spark.sql

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.reflect.internal.util.TableDef.Column

object SparkSqlDemo {
  
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder()
      .appName("SparkSqlDemo")
      .master("local[2]")
      .getOrCreate()
    
    
    val test = ss.read.json("src/main/resources/test.json")
    val info1 = inferReflection(ss)
    val info2 = programSpecify(ss)
    
    test.show()
    info1.show()
    info2.show()
    
    info1.select((info1("age") + 1).as("age+1")).show()
    
    ss.close()
    ss.stop()
    
  }
  
  case class Info(id: Int, name: String, age: Int)
//  def parseRDDLine(line: Seq[String]): Info = {
//    val id = line.head.toInt
//    val name = line(1)
//    val age = line(2).toInt
//    Info(id, name, age)
//  }
  /**
    * 通过 case class 反射来将 RDD 转换成 DataFrame
    * @param ss: SparkSession 对象
    * @return infoDF:
    */
  def inferReflection(ss: SparkSession): DataFrame = {
    import ss.implicits._
    
    val infoDF = ss.sparkContext.textFile("src/main/resources/test.txt")
      .map(_.split(","))
//      .map(line => parseRDDLine(line))
      .map(line => Info(line(0).toInt, line(1), line(2).toInt))
      .toDF()
    
    infoDF
  }
  
  /**
    * 通过编程指定 schema 结构(StructType) 的方式来将 RDD 转换为 DataFrame
    * @param ss: SparkSession 对象
    * @return
    */
  def programSpecify(ss: SparkSession): DataFrame = {
    val infoRDD = ss.sparkContext.textFile("src/main/resources/test.txt")
      .map(_.split(","))
      .map(line => Row(line(0).toInt, line(1), line(2).toInt))
    
    val infoStructType = StructType(Array(
      StructField("id", IntegerType, nullable=true),
      StructField("name", StringType, nullable=true),
      StructField("id", IntegerType, nullable=true)
    ))
    
    // 通过 createDataFrame 方法, 传入 RDD 和 StructType 对象来构建 DataFrame
    val infoDF = ss.createDataFrame(infoRDD, infoStructType)
    
    infoDF
  }
}
