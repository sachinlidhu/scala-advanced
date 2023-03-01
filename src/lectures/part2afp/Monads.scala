package lectures.part2afp

object Monads extends App{

  //our own try monad
  trait Attempt[+A]{
    def flatMap[B](f:A => Attempt[B]):Attempt[B]
  }
  object Attempt{
    def apply[A](a: => A): Attempt[A] =
      try{
          Sucess(a)
      }catch{
        case e: Throwable => Fail(e)
      }
  }
  case class Sucess[+A](value: A) extends Attempt[A] {
    def flatMap[B](f:A => Attempt[B]):Attempt[B] =
      try{
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f:Nothing => Attempt[B]):Attempt[B] = this
  }

  /*
  left-identity

  unit.flatMap(f) = f(x)
  Attemp(x).flatMap(f) = f(x) //success case
  Sucess(x).flatMap(f) = f(x) //proved

  right-identity

  attemp.flatMap(unit) =attemp
  Sucess(x).flatMap(x =>Attemp(x)) =Attemp(x) = Sucess

  associativity

  attempt.flapMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
  Fail(e).flatMap(f).flatMap(g) = Fail(e)
   */
}
