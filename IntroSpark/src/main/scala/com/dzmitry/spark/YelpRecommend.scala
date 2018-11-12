package com.dzmitry.spark

import java.nio.charset.CodingErrorAction

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession

import scala.io.{Codec, Source}

object YelpRecommend {
  /** Load up a Map of movie IDs to movie names. */
  def loadMovieNames() : Map[Int, String] = {

    // Handle character encoding issues:
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    // Create a Map of Ints to Strings, and populate it from u.item.
    var movieNames:Map[Int, String] = Map()

    val lines = Source.fromFile("../ml-100k/u.item").getLines()
    for (line <- lines) {
      var fields = line.split('|')
      if (fields.length > 1) {
        movieNames += (fields(0).toInt -> fields(1))
      }
    }

    return movieNames
  }

  /** Our main function where the action happens */
  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("Dataframes")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "file:///C:/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows.
      .getOrCreate()
    import spark.implicits._
    import org.apache.spark.sql.functions._


    val data = spark.read.json("C:\\Users\\ddzmi\\Downloads\\yelp_academic_dataset_review.json").cache()
//    data.printSchema()
//    data.toDF().show(10)
    var columns = Seq("user_id","business_id","stars")

    var testDF = data.select(columns.map(c => col(c)): _*).cache()
    testDF.show(10)
    testDF.repartition(100).write.format("csv").save("data.csv")


  }
}
