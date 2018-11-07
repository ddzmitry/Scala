package com.dzmitry.spark
import org.apache.spark.sql.SparkSession
import org.apache.log4j._
object Raiting {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

//    val sc = new SparkContext("local[*]", "RatingsCounter")
    val spark = SparkSession.builder
      .appName("My Spark Application")  // optional and will be autogenerated if not specified
      .master("local[*]")               // only for demo and testing purposes, use spark-submit instead
      .config("spark.sql.warehouse.dir", "target/spark-warehouse")
      .getOrCreate()

    val lines = spark.sparkContext.textFile("../ml-100k/u.data")

    // Convert each line to a string, split it out by tabs, and extract the third field.
    // (The file format is userID, movieID, rating, timestamp)
    val ratings = lines.map(x => x.toString().split("\t")(2))
    for (i <- ratings.collect()){
      println(i)
    }
    // Count up how many times each value (rating) occurs
    val results = ratings.countByValue()
    println(results)
    // Sort the resulting map of (rating, count) tuples
    val sortedResults = results.toSeq.sortBy(_._1)
    // Print each result on its own line.
    sortedResults.foreach(println)
  }
}
