package com.dzmitry.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object avarageByName {
  def parseLine (line: String) : (String, Int) ={
    val fields = line.split(",")
    val name = fields(1)
    val numFriends = fields(3).toInt
    return (name,numFriends)

  }
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession.builder
      .appName("My Spark Application")  // optional and will be autogenerated if not specified
      .master("local[*]")               // only for demo and testing purposes, use spark-submit instead
      .config("spark.sql.warehouse.dir", "target/spark-warehouse")
      .getOrCreate()

    val lines = spark.sparkContext.textFile("../fakefriends.csv")
    val rdd = lines.map(parseLine)
    //    map values (33,385) => (33,(385,1))
    val mappedValues = rdd.mapValues(x => (x,1))
//        mappedValues.foreach(println)

    //    (Tom,(1473,6))
    val SumofCounts = mappedValues.reduceByKey((x,y) => (x._1 + y._1, x._2 + y._2))
    val avgbyName = SumofCounts.mapValues(x => (x._1 / x._2).round);
    val avgbyAgeOrdered  = avgbyName
      .map(x => (x._2,x._1)).sortByKey(false).map(x => (x._2,x._1))

    //    Create schema and dataframe
    //    Define schema function
    val newNames = Seq("Name","avgCountofFriends")
    val dataFrame = spark.createDataFrame(avgbyAgeOrdered)
    //    Rename all fields accordingly
    val namedDf = dataFrame.toDF(newNames: _*)
    namedDf.show()
  }
}
