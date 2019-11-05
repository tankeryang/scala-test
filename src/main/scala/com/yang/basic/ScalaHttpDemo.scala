package com.yang.basic

import scalaj.http._


object ScalaHttpDemo {
  
  def main(args: Array[String]): Unit = {
    
    val request = Http("https://code.aliyun.com/trendy-bigdata/etl-files/raw/prod-crm/dwh/sql/ads/crm/cic_main_page/fully.sql")
    
    println(request.asString.body)
  }
}
