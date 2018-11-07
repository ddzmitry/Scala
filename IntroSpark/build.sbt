import sbt.util

name := "IntroSpark"
logLevel := util.Level.Error
version := "0.1"

scalaVersion := "2.11.8"
organization := "com.dzmitrydubarau"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.2.1" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.2.1" % "provided"

)
