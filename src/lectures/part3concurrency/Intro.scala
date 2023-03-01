package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App{
/*
interface Runnable{
  public void run()
}
 */
  //JVM threads
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Runnning in parallel")
  })

  aThread.start //this only givs a signal to jvm to start a jvm thread
  // it will create a jvm thread which runs on the top of OS thread

  //we can also store runnable method in a val and then pass it to thread
  val runnable = new Runnable {
    override def run(): Unit = println("Runnning in parallel11111")
  }
  val aThread1 = new Thread(runnable)
  aThread1.start

  runnable.run()//dont use this, it does not do anything in parrell

  aThread.join() //blocks untill thread finishes running..
  // this is to make sure the thread has already run before u want to make any computation

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("helllloooooooooo")))
  //here we shortened the runnable method to lambda

  val threadHelloBye = new Thread(() => (1 to 5).foreach(_ => println("goodbyeeeee")))

  threadHello.start()
  threadHelloBye.start
  //different runs produce different result sequence

  //executors
  /*
  threads are very expensive to start and kill
  The solution is to reuse them

  Java standard library offers a api to reuse threads with executors and thread pools
  the way we can do that is by creating pool
   */
  val pool = Executors.newFixedThreadPool(10) //10 is number of executors which we want to reuse
  pool.execute(() => println("something in the thread pool"))
  //this executor will get executed in one of the 10 threads managed by thread pool

  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 sec")//------------1
  })

  pool.execute(() => {
    Thread.sleep(2000)
    println("almost done")//this will printed immidetly after 1...here 2000 wont work
    Thread.sleep(1000)
    println(" done after 2 sec")
  })

  pool.shutdown() //to shut down all the threads inside pool
  //pool.execute(() => println("should not appear"))//now it will throw exception because pool does not accept any more actions
  println(pool.isShutdown) //true

}
