package lectures.part3concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}

object FuturesPromises extends App{

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }
  val aFuture = Future {

    calculateMeaningOfLife //calculates the meaning of life on another thread
  }// (global) which is passed by the compiler

  println(aFuture.value)//return Option

  println("waiting for future")
//onComplete means it will let u knopw when that future is executed
  aFuture.onComplete(t => t match {
    case Success(meaningOfLife) => println(s"The meaning of life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with $e")
  })

  Thread.sleep(3000)

  //design mini social network

  case class Profile(id: String, name:String){
    def poke(anotherProfile: Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
//databases
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    def fetchProfile(id:String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id,names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId,names(bfId))
    }
  }

  //client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(e) => e.printStackTrace()
  }
  Thread.sleep(1000)

  //functional composition of futures
  //map, filterMap, filter

  val nameOnTheWall = mark.map(profile => profile.name) //it will now chnage Future[Profile] to Future[String]

  val marksBestFriend =  mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  val zuksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("z"))

  //for comprehension
  //its the easiest and simple way
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  }mark.poke(bill)

  //fallbacks

  //if u want to retiurn custom result which is similar to actual one, upon failure then use recover

  val aProfileNoMatterWhat =  SocialNetwork.fetchProfile("unknown id").recover{
    case e: Throwable => Profile("fb.id.0-dummy","forevere alone")
  }
  //but in case if u dont want to return new profile (our class) itself but fetch another profile from the same social network
  // then use recoverWith
  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy") //parameter should be correct and should be present
  }

  val fallbackresult =  SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))
    //fallbackTo is better than recoverWith here if argument fails then it with take the excetion from success and return

  //online banking app
  case class User(name: String)
  case class  Transaction(sender: String, receiver:String, amount:Double, status:String)

  object BankingApp{
    val name = "Rock the jvm banking"
    def fetchUser(name:String): Future[User] = Future{
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user:User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Sucess")
    }
    def purchase(username:String, item:String, merchantName:String, cost: Double):String = {
      //fetch the user from db
      //create a traansaction from username to mechant name
      // wait for the ytransction to finish otgeerwise not return anytjing
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      }yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // it means that it will wait for 2 seconds only then throw exception
    }
  }

  println(BankingApp.purchase("daniel","iphone 12","rock the jvm store", 3000))

  //promises
  // in futures we to do any computaions we have to wait for future to compltete, so u we use methods like onComplete, recover, fallback, for comprehensions, map, faltmap etc
  //sometimes we have to specifically compltete a future at the time of our need, this intrduces concept of promise
  // promise is some sort of controller over furure
  val promise = Promise[Int]()
  val future = promise.future

  //thread 1 - "consumer"
  future.onComplete{
    case Success(r) => println("[consumer]--i have received" + r)
  }
  //thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producers]--crunching numbers")
    Thread.sleep(1000)
    // fulfilling the promise
    promise.success(42)
    println("[producer] done")
  })
  producer.start()
  Thread.sleep(1000)
}
