package lectures.part3concurrency

object JVMConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x =1
    })

    val thread2 = new Thread(() => {
      x+2
    })

    thread1.start()
    thread2.start()
    println(x)//it will x of last executed thread in compiler..its not neccesary that it will thread2 in our case
    //this is called race condition
    //so to avoid race conditions try not to use mutable variables

    //there are ways to control how threads can sequence the flow of execution and that is through syncronisation
  }

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit ={
    /*
    involves 3 steps
    - read old value
    - compute result
    - write new value
     */
    bankAccount.amount -= price
  }
  /*
  Example race condition:
  below is one senaro possible-----------------
  thread1 (shoes)
  - reads amount 50000
  - compute result 50000 - 3000 = 47000
  thread2 (iphone)
  - reads amount 50000
  - compute result 50000 - 4000 = 46000
  thread1 (shoes)
  - write amount 47000
  thread2 (iphone)
  - write amount 46000 // this will be final result
   */

  //to avoid race condition use below methd
  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.synchronized{
      bankAccount.amount -= price // synchronosed method will not any thread to enter here until first one is proccessed.....called a critical section
    }
  }
  def demoBankingProblem(): Unit ={
    (1 to 10000).foreach{_ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buy(account, "shoes", 3000))
      val thread2 = new Thread(() => buy(account, "iphone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000) println(s"ahaaa, i have just broken the bank: ${account.amount}")
    }
  }

  def demoBankingSafe():Unit ={
    (1 to 10000).foreach{_ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buySafe(account,"shoes",3000))
      val thread2 = new Thread(() => buySafe(account, "iphone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000) println(s"again broken:::: ${account.amount}")
    }
  }

  /**
   * Exercise
   * 1 - Create "inception threads"
   *  thread1
   *    -> thread2
   *      ->thread3
   *      ...
   *  each thread prints "hello from thread(i)"
   *  now u have to print all messages in reverse order
   * Thread must start one after another
   *
   * 2 - What is min/max value for x
   * 3 - "sleep fallacy": what is the value of message
   */

    //1..inception threads
    def inceptionThreads(maxThreads: Int, i: Int = 1): Thread =
      new Thread(() =>{
        if (i < maxThreads){
          val newThread = inceptionThreads(maxThreads, i+1)
          newThread.start()
          newThread.join()
        }
        println(s"hello from thread: $i")
      })

    //2...min/max
    /*
    max value = 100 - each thread increases thread by 1
    min value = 1
        all threads read x= 0 at the same time
        all threads will compute 0+1 = 1
     */
    def minMax(): Unit = {
      var x =0
      val threads = (1 to 100).map(_ => new Thread(() => x += 1))
      threads.foreach(_.start())
    }
 //3..sleep fallacy
  /*
  almost always, message = "Scala is awsome"
  is it guareented? No
  below senareo is also possible:

  main thread:
    message = "scala sucks"
    awsomeThread.start()
    sleep("1001") --yeilds execution
    meanwhile OS gives CPU some important threads
  awsomeThread:
      sleep("1000") --yeilds execution
       meanwhile OS gives CPU some important threads
   mainThread:
      println(meaasge) // scala sucks ---it will be printed
   awsomethread:
      message = "scala is awsome
   */
  // solution for above condition is use join()

  def demoSleepFallacy():Unit = {
    var message = ""
    val awsomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is awsome"
    })
    message = "Scala sucks"
    awsomeThread.start()
    Thread.sleep(1001)
    //solution that guarenteed our result
    awsomeThread.join()
    println(message)
  }

  def main(args: Array[String]): Unit = {
    runInParallel()
    demoBankingProblem()
    demoBankingSafe()
    inceptionThreads(10).start()
    demoSleepFallacy()
  }
}
