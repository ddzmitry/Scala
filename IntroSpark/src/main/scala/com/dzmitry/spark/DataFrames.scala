package com.dzmitry.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object DataFrames {

  case class Person(ID:Int, name:String, age:Int, numFriends:Int)

  def mapper(line:String): Person = {
    val fields = line.split(',')

    val person:Person = Person(fields(0).toInt, fields(1), fields(2).toInt, fields(3).toInt)
    return person
  }



  def main(args: Array[String]): Unit = {


    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Use new SparkSession interface in Spark 2.0
    val spark = SparkSession
      .builder
      .appName("Dataframes")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "file:///C:/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows.
      .getOrCreate()
    import spark.implicits._
    import org.apache.spark.sql.functions._
    val lines = spark.sparkContext.textFile("../fakefriends.csv")
    val people = lines.map(mapper).toDS().cache()
    println("Here is our inferred schema:")
    people.printSchema()

    people.show(20)
    println("Let's select the name column:")
    people.select("name").show()

    println("Filter out anyone over 21:")
    people.filter(people("age") < 21).show()

    println("Group by age:")
    people.groupBy("age").count().show()

    println("Make everyone 10 years older:")
    people.select(people("name"), people("age") + 10).show()
    val newds = people.groupBy(people("name"))
      .agg( avg(people("age")), avg(people("numFriends")))
    newds.show()
    spark.stop()

  }

}


