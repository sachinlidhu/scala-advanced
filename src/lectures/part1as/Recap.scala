package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {
  val aCondition:Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65

  //read--instruction vs expression

  //in a code block last line is result
  val aCodebBlock = {
    println("-------------------------------------------")
    if (aCondition) 54
    56
  }
  println(aCodebBlock)

  //unit = void-----------unit means function is not returning something meaningful
  val theUnit = println("hello, Scala")

  //functions
  def aFunction(x:Int):Int = x+1

  //recursion:  stack and tail
  //@tailrec ---t forces the compiler to check whether the function is tailrec or not
  @tailrec
  def factorial(n:Int, acc:Int):Int =
    if(n <= 0) acc
    else factorial(n-1, n*acc)

  //object orientattion

  class Animal
  class Dog extends Animal

  val dog: Animal = new Dog //subtype polymorphysim

}
