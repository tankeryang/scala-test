package com.yang.spark.etl

import net.sourceforge.argparse4j.inf.{ArgumentParser, Namespace}
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments

class SparkETL {

  private val USAGE = "java -jar xxx.jar com.yang.spark.etl.SpqrkETLExecutor -u"

  private var args: Namespace = _
  private var cmdArgs: Array[String] = _

  def this(cmdArgs: Array[String]) = {
    this()
    this.cmdArgs = cmdArgs
    this.args = setArgs
    checkArgs()
  }

  /**
    *
    * @return
    */
  private def setArgs: Namespace = {
    val parser: ArgumentParser = ArgumentParsers
      .newFor("spark-etl")
      .build()
      .description("SparkSQL-ETL-Tools")

    parser.addArgument("--usage", "-u")
      .action(Arguments.storeTrue())
      .dest("usage")
      .help("show usage")

    val args: Namespace = parser.parseArgs(cmdArgs)

    args
  }


  private def checkArgs(): Unit = {
    if(this.args.get("usage")) {
      showUsage()
      System.exit(1)
    }
  }

  def showUsage(): Unit = {
    printf("scala SparkETL usage")
  }

  def execute = {

  }
}
