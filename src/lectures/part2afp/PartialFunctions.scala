package lectures.part2afp

object PartialFunctions extends App{

  val aFunction = (x:Int) => x+1

  val aFussyFunction = (x:Int) =>
    if (x == 1) 42
    else if (x==2) 56
    else if (x==5) 999
    else throw new FunctionNotApplicableEception

  class FunctionNotApplicableEception extends RuntimeException

  //below is the simpler way to write the same

  val aNicerFuzzyFunction = (x:Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  //below is the even more better and simplified way, called as partial function
  val aPartialFunction: PartialFunction[Int,Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  println(aPartialFunction(2)) //it invoked its apply method
  //println(aPartialFunction(2232))

  println(aPartialFunction.isDefinedAt(67))

  //if u want to convert return type to Option then use lift method
   val lifted = aPartialFunction.lift
  println(lifted(2))
  println(lifted(222))
  println(aPartialFunction.lift(5))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 54 => 67
  }
  println(pfChain(2))
  println(pfChain(54))

  //pf extends normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  //HOF accepts pf, which is a sort of side effect

  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)
  //but side efect is that if change the case which is not matching list value, then map will try to find it out and crashes
  //see below side effect
/*
below will fail due to case5

 val aMappedList1 = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 5 => 1000
  }
  println(aMappedList1)*/

  //--PARTIAL FUNCTION HAS ONE PARAMETER TYPE ONLY ------NOTE-------
  /*
   *EXERCISE
   * CONSTRUCT A PARTIAL function instance yourself by instantiating a anonymous class
   * implement a small dumb chat bot as a partial function
   */
  val aManualFuzzyFunction = new PartialFunction[Int , Int] {
    override def apply(x:Int):Int = x match {
      case 1 => 42
      case 2 => 65
      case 5 => 999
    }
    override def isDefinedAt(x:Int) =
      x ==1 || x==2 || x==5
  }

  val chatBox:PartialFunction[String, String]= {
    case "hello" => "Hi, my name is sacgibjkbkjh"
    case "bbyee" => "once u start talking to me there is no return"
    case "call mom" => "unablke to fing ur phone without ur credit card"
  }
  scala.io.Source.stdin.getLines().foreach(x => println(s"chatbot  said " + chatBox(x))) //it will work as scanf in c

  //below is simplified way of writing
  scala.io.Source.stdin.getLines().map(chatBox).foreach(println)
}
