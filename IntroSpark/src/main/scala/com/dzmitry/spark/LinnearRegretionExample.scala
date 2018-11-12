package com.dzmitry.spark
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.feature.Normalizer
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.sql.SparkSession
//ml.regression.LinearRegression
object LinnearRegretionExample {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    // Use new SparkSession interface in Spark 2.0
    val spark = SparkSession
      .builder
      .appName("PopularMovies")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "file:///C:/temp") // Necessary to work around a Windows bug in Spark 2.0.0; omit if you're not on Windows.
      .getOrCreate()

    // Read in each rating line and extract the movie ID; construct an RDD of Movie objects.
    val data  = spark.sparkContext.textFile("../lpsa.data.txt")

    val parsedData = data.map { line =>
      val x : Array[String] = line.replace(",", " ").split(" ")
      val y = x.map{ (a => a.toDouble)}
      val d = y.size - 1
      val c = Vectors.dense(y(0),y(d))
      LabeledPoint(y(0), c)
    }.cache()
    import spark.implicits._
    import org.apache.spark.sql.functions._
    parsedData.toDF().show()
//    +----------+--------------------+
    //|     label|            features|
    //+----------+--------------------+
    //|-0.4307829|[-0.4307829,-0.86...|
    //|-0.1625189|[-0.1625189,-0.86...|
    //|-0.1625189|[-0.1625189,-0.15...|
    //|-0.1625189|[-0.1625189,-0.86...|
    //| 0.3715636|[0.3715636,-0.864...|
    //| 0.7654678|[0.7654678,-0.864...|
    //| 0.8544153|[0.8544153,-0.864...|

    val numIterations = 10000000
    val stepSize = 0.00000001
    val model = LinearRegressionWithSGD.train(parsedData, numIterations, stepSize)

    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    valuesAndPreds.foreach((result) => println(s"predicted label: ${result._1}, actual label: ${result._2}"))

    val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
    println("training Mean Squared Error = " + MSE)
  }
}
//Create LabelPoint object
//First we use the textFile method to read the text file lpsa.dat, whose first lines is shown below. In this data set, y is value that we want to calculate (the dependant variable). It is the first field. The other fields are the independent variables. So instead of having just y = mx + b, where x is an input variable. Here we have many x’s.
//-0.4307829,-1.63735562648104 -2.00621178480549 -1.86242597251066 -1.02470580167082 -0.522940888712441 -0.863171185425945 -1.04215728919298 -0.864466507337306
//
//That creates data as an RDD[String].
//val data = sc.textFile("/home/walker/lpsa.dat")
//
//A string is an iterable object so we loop over it twice to split it by spaces. We do it twice because you can see in the data that the first two elements are separated by a comma so we have to get rid of that first.
//val parsedData = data.map { line =>
//val x : Array[String] = line.replace(",", " ").split(" ")
//
//We then convert all these strings to Doubles, as machine learning requires numbers and this particular algorithm requires a Vector of Doubles.
//val y = x.map{ (a => a.toDouble)}
//
//The LabeledPoint is an object required by the linear regression algorithm. It is in this format
//(labels, features)
//
//Where features is a Vector. Here we use a dense vector which is a type that does not handle blank values, of which we have none.
//val parsedData = data.map { line =>
//val x : Array[String] = line.replace(",", " ").split(" ")
//val y = x.map{ (a => a.toDouble)}
//val d = y.size - 1
//val c = Vectors.dense(y(0),y(d))
//LabeledPoint(y(0), c)
//}.cache()
//Note that we use the word cache because Spark is a distributed system. To calculate this we need to retrieve data from each node. Cache, like collect, gathers all the elements across the nodes.
//
//Training with testing data
//In this example, we use the training set as the testing data as well. That is OK for purposes of illustration as both should be nearly the same if they represent true samples of actual data. In real life, you train on the training data, like the lpsa.dat file. Then you make predictions on new incoming data, which is called testing data even though a better name would be live data.
//
//There are two parameters: numIterations and stepSize, in addition to the LabeledPoint. You would have to understand the Stochastic Gradient Descent logic and math to know what those mean, which we do not explain here as it is complex. Basically it means how many times can the algorithm keep looping to adjust its estimation, meaning hone in on the point where the error goes to as close to zero as possible. That is called converging.
//val numIterations = 100
//val stepSize = 0.00000001
//
//val model = LinearRegressionWithSGD.train(parsedData, numIterations, stepSize)
//
//Test the model
//Now, to see how well it works, loop through the LabelPoint and then run the predict() method over each creating a new RDD(double, double) of the label (y or the independent variable, meaning the observed end result y) and the prediction (the estimation based on the regression formula determined by the SDG algorithm).
//val valuesAndPreds = parsedData.map { point =>
//val prediction = model.predict(point.features)
//(point.label, prediction)
//}
//
//Calculate error
//An error of 0 would mean we have a perfect model. As you can see below, the model does not do a good job of predicting values until the label is high. For the whole model, the error is:
//println("training Mean Squared Error = " + MSE)
//training Mean Squared Error = 7.451030996001437