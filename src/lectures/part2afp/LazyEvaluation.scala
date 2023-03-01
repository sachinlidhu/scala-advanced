package lectures.part2afp

object LazyEvaluation extends App{
  //val x = throw new RuntimeException ---when u run this it will throw exception
  //LAZY delays the evaluation of values
  lazy val x = throw new RuntimeException // here nothing will happen beacuse its not evaluated
  //now x will only be eveluated when u use it somewhere
  //println(x)//here x will be evaluated now

  //NOTE--- LAZY will be evaluated once only when used first time
  lazy val y = {
    println("helloooo")
    57
    78
  }
  println(y) //here it is evluated first time so entire block will be printed
  println(y) //here only last line in block is printed
  //examples
  //1..side effect
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition: Boolean = false

  lazy val lazyCondition  = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")
  //here lazyCondition is true and we know that when lazy is evaluated first time it will print all
  // but in our case the moment compile see that simpleCondition is false then it will not check second condition (lazyCondition)
  //so it will only print `no`

  //2..in conjuction with call by name
  def byNameMethod(n: => Int): Int = n+n+n+1
  def retrieMagicValue ={
    //the side effect  or a long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }
  println(byNameMethod(retrieMagicValue))
  // here we will see waiting is printing 3 times

  //but we can optimise it using lazy and u will see waiting will be printing only once then
  def byNameMethod1(n: => Int): Int = {
    lazy val t = n
    t + t + t + 1
  }

  def retrieMagicValue1 = {
    //the side effect  or a long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod1(retrieMagicValue1))

  //filter
  def lessThan30(i:Int):Boolean ={
    println(s"$i is less than 30?")
    i<30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }
  val numbers = List(1,25,40,5,23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  //now chwck the below output and compare the difference
  //withFilter uses lazy evaluation
  val lt30_lazy = numbers.withFilter(lessThan30)
  val gt20_lazy = lt30_lazy.withFilter(greaterThan20)
  println(gt20_lazy) // this will not work with `withfilter`
  gt20_lazy.foreach(println)

  // for comprehension uses withFilter  which are lazy with guards
  for{
    a <- List(1,2,3) if a % 2 == 0
  }yield a+1

  //same will converted to below internally
  List(1,2,3).withFilter(_%2 == 0).map(_+1)
  /*
  Exercise: impliment a lazily evaluated, singly list stream of elements
   */

}
