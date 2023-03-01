package erercises

object StteamsPlayground extends App{

  abstract class MyStream[+A]{
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](elem:B): MyStream[B] //prepend
    def ++[B>:A](anotherStream: => MyStream[B]): MyStream[B] //concat two streams

    def foreach(f:A => Unit): Unit
    def map[B](f:A => B):  MyStream[B]
    def flatMap[B](f:A => MyStream[B]):MyStream[B]
    def filter(predicate: A=> Boolean): MyStream[A]

    def take(n: Int):MyStream[A] //takes the first n elements of the stream
   // def takeAsList(n:Int):List[A]
    final def toList[B>: A](acc: List[B] = Nil): List[B] =
      if (isEmpty) acc.reverse
      else tail.toList(head :: acc)
  }

  object EmptyStream extends MyStream[Nothing]{
    def isEmpty: Boolean = true

    def head: Nothing = throw new NoSuchElementException

    def tail: MyStream[Nothing] = throw new NoSuchElementException

    def #::[B >: Nothing](elem: B): MyStream[B] = new Cons[B](elem, this)

    def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

    def foreach(f: Nothing => Unit): Unit = ()

    def map[B](f: Nothing => B): MyStream[B] = this

    def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

    def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

    def take(n: Int): MyStream[Nothing] = this

  //  def takeAsList(n: Int): List[Nothing] = Nil
  }

  class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A]{
    def isEmpty: Boolean = false

    override val head: A = hd

    override lazy val tail: MyStream[A] = tl

    def #::[B >: A](elem: B): MyStream[B] = new Cons[B](elem, this)

    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream)

    def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }

    def map[B](f: A => B): MyStream[B] = new Cons[B](f(head),tail.map(f))

    def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

    def filter(predicate: A => Boolean): MyStream[A] =
      if (predicate(head)) new Cons(head, tail.filter(predicate))
      else tail.filter(predicate)

    def take(n: Int): MyStream[A] = {
      if (n == 0) EmptyStream
      else if (n == 1) new Cons(head, EmptyStream)
      else new Cons(head, tail.take(n-1))
    }

   // def takeAsList(n: Int): List[A]
  }

  object MyStream{
    def from[A](start: A)(generator:A => A): MyStream[A]=
      new Cons(start,MyStream.from(generator(start))(generator))
  }

  val naturals = MyStream.from(1)(_+1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.tail.head)
  val startFrom0 =0 #:: naturals
  println(startFrom0.head)

  println(naturals.take(12))
  naturals.take(12).foreach(println)
  startFrom0.map(_*2).take(5).foreach(println)

  //println(startFrom0.filter(_<10).toList()) // filter on infinite list can not gurante its execution becuse it has to check all elements of list which can be infinite
  //so while filtering use take
  println(startFrom0.filter(_<10).take(10).toList())

  /*
  EXERCISE ON STREAMS
  1...stream of fibonacci numbers
  2...stream of prime numbers with erathonius,s seive
  eratnenes,s seive works like below
  [2,3,4,5.....]
  filter out all numbers divisible by 2 --keep first element
  [2,3,5,7,9,11,...]
  filte out all numbers divisible by 3 ---keep first element
  [2,3,5,7,11,13,17,...]
  filter out all by 5 amd so on

   */
  def fibonacci(first: Int, second: Int): MyStream[Int]=
    new Cons(first, fibonacci(second,first+second))

  println(fibonacci(1,1).take(10).toList())

  //erathosenes,s sive
  def eratosthenes(numbers: MyStream[Int]): MyStream[Int]=
    if (numbers.isEmpty) numbers
    else new Cons(numbers.head, eratosthenes(numbers.tail.filter(_%numbers.head != 0)))

  println(eratosthenes(MyStream.from(2)(_+1)).take(10).toList())
}
