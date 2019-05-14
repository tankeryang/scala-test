package com.yang.spark.sql

import java.sql.DriverManager

object SparkSqlHiveDemo {
  
  def main(args: Array[String]): Unit = {
    Class.forName("org.apache.hive.jdbc.HiveDriver")
    
    val conn = DriverManager.getConnection("jdbc:hive2://10.10.22.3:10000", "hive", "")
    val pstmt = conn.prepareStatement("select * from ads_crm.cic_main_page")
    
    val rs = pstmt.executeQuery()
    while(rs.next()) {
      rs.getString("brand")
    }
    
    rs.close()
    pstmt.close()
    conn.close()
  }
}
