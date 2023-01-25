package lectures.part1as

object AdvancePatternMatching extends App{
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*we know below structures are accepted by pattern matching
   * constants,wildcards, case classes, tuples, some special magic like above
   * Now we will see how to create custom structure to be used in pattern matching
   */

  //lets say for some u can  not make the below class as case class and u still want this class to be compatible with pattern matching
  // we can do it using unapply method in object with return type Option of field parameter
  class Person(val name: String, val age: Int)
  object Person{
    def unapply(person:Person):Option[(String,Int)] = {
      if (person.age < 21) None
      else Some(person.name,person.age)
    }
    def unapply(age: Int): Option[String] = {
      Some(if (age < 20) "minor" else "major")
    }
  }
  val bob = new Person("BOB",25)
  val greeting = bob match {
    //it will match object Person not class, if u name object person1 which is different from class,
    // and pass that object in case..it will also work
    case Person(n,a) => s"hi my name is $n and i am $a years old"
  }
  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"my legal status is $status"
  }
  println(legalStatus)

  //Exercise
  val n:Int = 45
  val mathProperty = n match {
    case x if x < 10 =>"single digit"
    case x if x%2 == 0 => "an even number"
    case _ => "no property"
  }// writing that many `if` in cases will make code worse, we want something more elegant
  //so device a custom pattern matching solution

  object even{
    def unapply(arg: Int): Option[Boolean] =
      if (arg%2 == 0) Some(true)
      else None
  }
  object singleDigit{
    def unapply(arg: Int): Option[Boolean] =
      if (arg > -10 && arg < 10) Some(true)
      else None
  }

  val n1: Int = 8
  val mathProperty1 = n1 match {
    case even(_) => "single digit"
    case singleDigit(_) => "an even number"
    case _ => "no property"
  }
  println(mathProperty1)
  //another way to write this is as folloews

  //reomove `_` in pattern match case and remove Option type
  object even2 {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit2 {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n2: Int = 8
  val mathProperty2 = n1 match {
    case even2() => "single digit"
    case singleDigit2() => "an even number"
    case _ => "no property"
  }
  println(mathProperty2)

}
