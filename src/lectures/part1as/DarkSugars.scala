package lectures.part1as

import scala.util.Try

object DarkSugars extends App{
  // syntax sugar 1--- methods with single parameter
  def singleArgsMethod(arg: Int):String = s"$arg little ducks..."

  //instead of calling it like singleArgsMethod(42), we can call using singleArgsMethod {...}
  //example is as follows
  val description = singleArgsMethod {
    //write some complex code
    42
  }
  //generally we also use Try like that

  val aTryIntance = Try { //here its actually a apply method from Try which can be called like this
    //some code
    throw new RuntimeException()
  }

  //map also takes one parameter so can be called like this
  List(1,2,3).map{ x =>
    x+1
  }

  //syntax number 2...........single abstract method pattern
  //instances of a trait with single method can actually be reduced to lamdas
  trait Action{
    def act(x:Int):Int
  }

  /*normal way to instance a trait is as follows
  val anInstance:Action = new Action{
    override def act(x:Int):Int = x+1
  }
   * the above code can be written as follows if we have single method
   */
  val aFunkyInstance:Action = (x:Int) => x+1

  // another examples lets see how we can simplify runnable
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello, Scala")
  })
  //the above code can be simplified as follows
  val aThread1 = new Thread(()=> println("sweet, Scala"))

  //this pattern also works for class that have methods implemented but only one method unimplemented

  abstract class AnAbstractType{
    def implemented: Int =23
    def f(a:Int):Unit
  }
  val anAbstractInstance: AnAbstractType = (a:Int) => println("meaww")

 // syntax sugar 3----- the :: and #:: methods are special
  val prependedList = 2 ::List(3,4)

  /*internally it will do following
  2.::(List(3,4))
  List(3,4).::(2)
   */
  val a = 1 :: 2:: 3:: List(4,5)
  //internally its equivalent to following
  //List(4,5).::(3).::(2).::(1)
  //NOTE--any method which ends on : is right associative, that why in :: and #:: we can notice ends on :

  //syntax sugar 4 ----- multi word method naming
    class TeenGirl(name:String) {
    def `and then said`(gossip:String) = println(s"$name said $gossip")
  }
  //`` this sign helps to create custom string
  val lilly = new TeenGirl("LILLY")
  lilly `and then said` "Scala is sweet"

  // syntax sugar 5----infix types in generics
  class Composite[A,B]
  val composite: Composite[Int, String] = ???
  //we can also write above one as fallows
  val composite1: Int Composite String = ???

  class -->[A,B]
  val towards: Int --> String = ???

  //syntax sugar 6 ---------- update() is very special, much like apply
  val anArray = Array(1,2,3)
  anArray(2) = 7 //rewritten to anArray.update(2,7)
  //update is widely used in mutable collections


  //syntax sugar 7 ------setters for mutable containers




}
