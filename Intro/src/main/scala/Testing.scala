
object Testing {

  def fibo(int: Int): Int ={
    if(int <= 1) return  1
    fibo(int-1) + fibo(int - 2)
  }

  def returnList(int: Int): List[Int] = {
    return List.range(0,int)

  }
  def main(args: Array[String]): Unit = {
//    val answer : Int = fibo(20);
//    println(answer)

    var numOne: Int = 10;

    while(numOne >= 0){
      if(numOne <= 1){
         1
      }
      println((numOne -1) + (numOne -2))
      numOne = numOne -1
    }

    def transformstering(s:String,f: String => String): String ={
            f(s)
    }

    println {
      transformstering("apple", x => {
        val y = x.toUpperCase; y.reverse + x.reverse
      })
    };


    val By3 = returnList(20).filter((x : Int) => x % 3 == 0)
    println(By3)
    val By3TimesThree = By3.map((x:Int) => x *3)
    println(By3TimesThree)
    val Allsum = By3TimesThree.reduce((x:Int,y:Int) => x + y);
    println(Allsum)


  }

}
