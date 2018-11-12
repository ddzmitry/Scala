import sbt.util

name := "IntroSpark"
logLevel := util.Level.Error
version := "0.1"

scalaVersion := "2.11.8"
organization := "com.dzmitrydubarau"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.3.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.3.0" % "provided",
   "org.apache.spark" %% "spark-mllib" % "2.3.0" % "provided",
  "com.databricks" %% "spark-csv" % "1.5.0"

)
