package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App{
  //what we learned in previous lesson is that we cannot enfore the certain level of execution on threads
  //but in this lecture we are going to manage that
  /*
  PRODUCER CONSUMER PROBLEM
  we are start with a small container which wraps a single value and in parallel we have two threads running
  one is called a producer which has propuse of setting a value inside a container
  other is called consumer which has purpose of extract a value out of container

  Now problem is that producer and consumer are working in parrallel so they dont know who has finished running.

  Now to solve that we somehow have to force consumer to wait for producer to finish
   */
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    //below method will work as producer
    def set(newValue: Int) = value = newValue

    //below method will work as consumer
    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

    def naiveProdCons():Unit = {
      val container = new SimpleContainer

      val consumer = new Thread(() => {
        println("[consumer] waiting....")
        while (container.isEmpty){
          println("[consumer] actively waiting....")
        }
        println(s"[consumer].. i have consumed -- ${container.get}")
      })
      val producer = new Thread(() => {
        println("[producer]..computing")
        Thread.sleep(1000)
        val value = 42
        println(s"[producer]..i have produced--$value")
        container.set(value)
      })
      consumer.start()
      producer.start()
    }
  naiveProdCons()

  //wait and notify
  //lets write a smarter producer consumer code
  def smartProdCons():Unit = {
    val conatiner = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer]...waiting")
      conatiner.synchronized{
        conatiner.wait
      }
      println(s"[Consumer]...i consumed--${conatiner.get}")
    })
    val producer = new Thread(() => {
      println("[producer]..working hard-----")
      Thread.sleep(1000)
      val value = 53

      conatiner.synchronized{
        println(s"[producer]...prodcing value = $value")
        conatiner.set(value)
        conatiner.notify() //notify will awaken the waited teread
      }
    })
    consumer.start()
    producer.start()
  }
  smartProdCons()

  /*
  producer -> [???] -> consumer
   */

  def prodConsLargeBuffer():Unit  = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {

      val random = new Random()

      while(true) {
        buffer.synchronized{
          if(buffer.isEmpty){
            println("[consumer]--buffer empty, waiting")
            buffer.wait()
          }
          //there must be at least one value in buffer
          val x = buffer.dequeue()
          println("[consumer] consumed" + x)

          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while(true){
        buffer.synchronized{
          if(buffer.size == capacity){
            println("[producer] buffer is full, waiting")
            buffer.wait()
          }
          // there must be at least one empty space in the buffer
          println("[producer]--producing" + i)
          buffer.enqueue(i)
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })
    consumer.start()
    producer.start()
  }
  prodConsLargeBuffer()

  /*
  prod - cons level 3

  here we have multiple producers and multiple consumers working on the same buffer
   */

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"[consumer $id]--buffer empty, waiting")
            buffer.wait()
          }
          //there must be at least one value in buffer
          val x = buffer.dequeue()
          println(s"[consumer $id] consumed" + x)

          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }
  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run():Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting")
            buffer.wait()
          }
          // there must be at least one empty space in the buffer
          println(s"[producer $id]--producing" + i)
          buffer.enqueue(i)
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }
  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 30

    (1 to nConsumers).foreach(i => new Consumer(i,buffer).start())
    (1 to nProducers).foreach(i => new Producer(i,buffer,capacity).start())
  }
  //multiProdCons(4,5)
  /*
  Exercise
  1) think of an example where notifyAll acts in a different way than notify
  2) create a deadlock ---threads are blocked.
  3) create a live lock -- threads are active not blocked but cant continue.

  ----using notifyAll prevents a possible deadlock .
  example 10 producers, 2 consumers, buffer size 3
  --one producer fills the buffer quickly. The other 9 go to sleep.
  --one consumer consumes all then go to sleep,. others go to sleep when they see the buffer empty.
  --every poor producer sees buffer full and goes to slepp.....deadlock
   */

  def testNotifyAll():Unit ={
    val bell = new Object
    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized{
        println(s"[thread $i]--waiting")
        bell.wait()
        println(s"[thread $i]--woken up")
      }
    }).start())
    new Thread(() => {
      Thread.sleep(2000)
      println("annouser--rock and roll")
      bell.synchronized{
        notifyAll() //if u use notify u will see the difference--only one thread will be awake
      }
    }).start()
  }
  //testNotifyAll()

  //2..deadlock
  case class Friend(name:String){
    def bow(other:Friend) = {
      this.synchronized{
        println(s"$this : I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }
    def rise(other:Friend) = {
      this.synchronized{
        println(s"$this: I am rising to my friend $other")
      }
    }
  }
  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

  new Thread(() => sam.bow(pierre)).start()
  new Thread(() => pierre.bow(sam)).start()

  //3..livelock

  }
