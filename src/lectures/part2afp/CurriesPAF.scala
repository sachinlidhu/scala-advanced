package lectures.part2afp

object CurriesPAF extends App{

  //curried function
  val superAdder: Int => Int => Int =
    x => y => x+y

  val add3 = superAdder(3) //Int => Int which is y = 3+y
  println(add3(5))

  //or we can directly write it as below
  println(superAdder(3)(5))

  //METHOD
  def curriedAdder(x:Int)(y:Int):Int = x+y //curried method

  //val add4 = curriedAdder(4) //it will not work
  //to make it work u need to provide return type to add4
  // because curriedAdder is a method and it takes 2 parameters, but we passed only one,
  // so to make that know what to pass and return we provide return type to val
  val add4: Int => Int =curriedAdder(4)

  //but if still want to use curried function with only few parameters and dont want to provide return type
  // then use _ ...it will work as eta expansion
  val add5 =curriedAdder(4) _


  //important concept
  def inc(x:Int):Int = x+1
  List(1,2,3).map(inc)
  //internally compiler will convert it into below
  List(1,2,3).map(x => inc(x)) //its called ETA expansion

  //Exercise
  val simpleAddFunction = (x:Int, y:Int) => x+y
  def simpleAddMethod(x:Int, y:Int) = x+y
  def curriedAddMethod(x:Int)(y:Int)= x+y

  //add7: Int => Int = y => 7+y
  //different implementations of add7 using the above

  val add7 = (x:Int) => simpleAddFunction(7,x)
  val add7_2 = simpleAddFunction.curried(7)
  val add7_3 = curriedAddMethod(7) _ //PAF
  val add7_4 = curriedAddMethod(7)(_)
  val add7_5 = simpleAddMethod(7, _:Int)
  val add7_6 = simpleAddFunction(7,_:Int)

  //underscores are powerful
  def concatinator(a:String, b:String, c:String) = a+b+c
  val insertName = concatinator("Hello, I am", _:String, ", how are you?")
  //internally it will do eta expansion
  //x:String => concatinator(hello, x, how are u)
  println(insertName("Sachin Lidhu"))

  val fillInTheBlanks = concatinator("hello", _:String, _:String) //(x,y):String => concatinator(hello, x, y)
  println(fillInTheBlanks("sachin","scala is good"))

  //EXERCISES
  /*
  1. Process a list of numbers and return their string representation with different formats
      use the %4.2f, %8.6f and 14.12f with a curried formatted function
   */
  /*
  2. difference between
      - function vs methods
      - parameters: by-name vs 0-lamda
   */
  println("%8.6f".format(Math.PI))
  def curriedFormatter(s:String)(number:Double):String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f")_
  val seriousFormat = curriedFormatter("%8.6f")_
  val preciseFormat = curriedFormatter("%14.12f")_

  println(numbers.map(simpleFormat))
  //println(simpleFormat(numbers))---this will not work as numbers is list but parameter needed is double (single element not list)

  def byName(n: =>Int) = n+1
  def byFunction(f: ()=> Int) = f()+1

  def method: Int = 42
  def parenMethod(): Int = 42
  /*
  explore calling byName and byFunction with the following expression
    -Int
    -method
    -parenMethod
    -lambda
    -PAF

   Figure out which cases compile and which not
   */

  println(byName(23))
  println(byName(method))
  println(byName(parenMethod()))
  println(byName(parenMethod))
  //println(byName(()=>42))//Not Ok as byName accepts value not function type
 // println(byName(()=>42()))// ok because we are calling a function also inside a parameyter itself which results in val type

//println(byFunction(45))//NOT OK
//println(byFunction(method))//NOT OK because this function in paramete is getting converted to value int
  // if u want above method to run as a function inside a parameter then use () in its defination like paranMethod()
  println(byFunction(parenMethod)) //OK here we have () in its defination so it did eta  expansion
 // println(byFunction(parenMethod())) //Not Ok
  println(byFunction(() => 45)) //ok

}
