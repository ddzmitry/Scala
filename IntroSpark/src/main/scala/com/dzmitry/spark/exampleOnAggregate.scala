package com.dzmitry.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{avg, sum}

object exampleOnAggregate {
  def main(args: Array[String]): Unit = {


    Logger.getLogger("org").setLevel(Level.ERROR)

    // Use new SparkSession interface in Spark 2.0
    val spark = SparkSession
      .builder
      .appName("PopularMovies")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "file:///C:/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows.
      .getOrCreate()

    import spark.implicits._
    import org.apache.spark.sql.functions._

    val df = spark.sparkContext.parallelize(Seq(
      (1.0, 0.3, 1.0), (1.0, 0.5, 0.0),
      (-1.0, 0.6, 0.5), (-1.0, 5.6, 0.2))
    ).toDF("col1", "col2", "col3")

    val A = df.groupBy($"col1").min()
    A.show()
    //    +----+---------+---------+---------+
    //|col1|min(col1)|min(col2)|min(col3)|
    //+----+---------+---------+---------+
    //|-1.0|     -1.0|      0.6|      0.2|
    //| 1.0|      1.0|      0.3|      0.0|
    //+----+---------+---------+---------+
    //
    val B = df.groupBy("col1").sum("col2", "col3")
    B.show()
    //+----+-----------------+---------+
    //|col1|        sum(col2)|sum(col3)|
    //+----+-----------------+---------+
    //|-1.0|6.199999999999999|      0.7|
    //| 1.0|              0.8|      1.0|
    //+----+-----------------+---------+
    //
    val C = df.groupBy("col1").agg( avg(df("col2")), avg(df("col3")))
    C.show()

    //+----+------------------+---------+
    //|col1|         avg(col2)|avg(col3)|
    //+----+------------------+---------+
    //|-1.0|3.0999999999999996|     0.35|
    //| 1.0|               0.4|      0.5|
    //+----+------------------+---------+
    val D = df.groupBy("col1").agg(sum("col2").alias("col2"), avg("col3").alias("col3"))
    D.show()
    //
    //+----+-----------------+----+
    //|col1|             col2|col3|
    //+----+-----------------+----+
    //|-1.0|6.199999999999999|0.35|
    //| 1.0|              0.8| 0.5|
    //+----+-----------------+----+
    val E = df.groupBy("col1").agg(sum("col2").alias("col2"), avg("col3").alias("col3"))
    E.show()



  }
}
