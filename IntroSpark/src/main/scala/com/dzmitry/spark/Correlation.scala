package com.dzmitry.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.linalg.{Matrix, Vectors}
import org.apache.spark.ml.stat.Correlation.corr
import org.apache.spark.sql.{Row, SparkSession}

object Correlation {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession.builder
      .appName("Popular Heroe")  // optional and will be autogenerated if not specified
      .master("local[*]")               // only for demo and testing purposes, use spark-submit instead
      .config("spark.sql.warehouse.dir", "target/spark-warehouse")
      .getOrCreate()

    val data = Seq(
      Vectors.sparse(4, Seq((0, 1.0), (3, -2.0))),
      Vectors.dense(4.0, 5.0, 0.0, 3.0),
      Vectors.dense(6.0, 7.0, 0.0, 8.0),
      Vectors.sparse(4, Seq((0, 9.0), (3, 1.0)))
    )
    import spark.implicits._
    val df = data.map(Tuple1.apply).toDF("features")
    val Row(coeff1: Matrix) = org.apache.spark.ml.stat.Correlation.corr(df, "features").head
    println("Pearson correlation matrix:\n" + coeff1.toString)

    val Row(coeff2: Matrix) = org.apache.spark.ml.stat.Correlation.corr(df, "features", "spearman").head
    println("Spearman correlation matrix:\n" + coeff2.toString)
  }
}