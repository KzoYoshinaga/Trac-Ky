

package main

object FirstTest {
  import scala.concurrent._
  import akka._
  import akka.actor._
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.util._

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val source = Source(1 to 3)
  val sink = Sink.foreach[Int](println)
  val invert = Flow[Int].map(elem => elem * -1)
  val doubler = Flow[Int].map(elem => elem * 2)
  source via invert via doubler to sink run
}

object QuickStart {
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.util.ByteString
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.nio.file.Paths

  val source: Source[Int, NotUsed] = Source(1 to 100)

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()

  val done: Future[Done] = source.runForeach(println)(materializer)

  implicit val ec = system.dispatcher
  done.onComplete(_ => system.terminate())
}

object QuickStart2 {
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.util.ByteString
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.nio.file.Paths

  val source: Source[Int, NotUsed] = Source(1 to 50)

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] = factorials
                          .map(num => ByteString(s"$num\n"))
                          .runWith(FileIO.toPath(Paths.get("factorials.txt")))

  implicit val ec = system.dispatcher
  result.onComplete(_ => system.terminate())
}

object QuickStart3 {
  import scala.concurrent._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl._

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] = body.split(" ").collect {
      case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]",""))
    }.toSet
  }

  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
    Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
    Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
    Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
    Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
    Nil)

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val done: Future[Done] = tweets.map(_.hashtags)
    .reduce(_ ++ _)
    .mapConcat(identity)
    .map(_.name.toUpperCase)
    .runWith(Sink.foreach(println))

  implicit val ec = system.dispatcher
  done.onComplete(_ => system.terminate())
}

object QuickStart4 {
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.util.ByteString
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.nio.file.Paths

  val source: Source[Int, NotUsed] = Source(1 to 50)

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] = factorials
                          .map(num => ByteString(s"$num\n"))
                          .runWith(FileIO.toPath(Paths.get("factorials.txt")))

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  val result2: Future[IOResult] = factorials
                          .map(_.toString)
                          .runWith(lineSink("factorial2.txt"))

  implicit val ec = system.dispatcher
  result.onComplete(_ => system.terminate())
  result2.onComplete(_ => system.terminate())
}

object QuickStart5 extends App {
  import akka.stream._
  import akka.stream.scaladsl._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.util.ByteString
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.nio.file.Paths

  val source: Source[Int, NotUsed] = Source(1 to 50)

  implicit val system = ActorSystem("TestSystem")
  implicit val materializer = ActorMaterializer()

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[Done] = factorials
    .zipWith(Source(0 to 50))((num, idx) => s"$idx! = $num")
    .throttle(1, 50.millisecond, 1, ThrottleMode.shaping)
    .runForeach(println)

  implicit val ec = system.dispatcher
  result.onComplete(_ => system.terminate())

}

object QuickStart6 extends App {
  import akka.stream.scaladsl.Source
  import akka.NotUsed
  val source: Source[Int, NotUsed] = Source(1 to 100)

  import akka.actor.ActorSystem
  implicit val system = ActorSystem("QuickStart6")

  import akka.stream.ActorMaterializer
  implicit val materializer = ActorMaterializer()

  import scala.concurrent.duration._
  import akka.stream.ThrottleMode

  implicit val ec = system.dispatcher
  source.throttle(1, 100.millisecond, 1, ThrottleMode.shaping)
    .runForeach(println)
    .onComplete(_ => system.terminate())
}

object QuickStart7 extends App {

  println("QuickStart7")

  import akka.stream.scaladsl.Source
  import akka.NotUsed
  val source: Source[Set[String], NotUsed] =
    Source(Set("A","B")::Set("B","C")::Set("C","D"):: Nil)

  import akka.actor.ActorSystem
  implicit val system = ActorSystem("QuickStart7")

  import akka.stream.ActorMaterializer
  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher
  source.mapConcat(_.toList).runForeach(println).onComplete(_ => system.terminate())
  source.mapConcat(_.toList).runForeach(println).onComplete(_ => system.terminate())

}

object QuickStart8 extends App {
  import scala.concurrent._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl._

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] = body.split(" ").collect {
      case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]",""))
    }.toSet
  }

  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
    Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
    Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
    Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
    Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
    Nil)

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val wirteAuthors: Sink[Author, NotUsed] = ???
  val writeHashTags: Sink[Hashtag, NotUsed] = ???

  implicit val ec = system.dispatcher
  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    import akka.stream.ClosedShape
    val bcast = b.add(Broadcast[Tweet](2))
    tweets ~> bcast.in
    bcast.out(0) ~> Flow[Tweet].map(_.author) ~> wirteAuthors
    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashTags
    ClosedShape
  })
  g.run()
}

object QuickStart9 extends App {
  import scala.concurrent._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl._

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] = body.split(" ").collect {
      case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]",""))
    }.toSet
  }

  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
    Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
    Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
    Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
    Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
    Nil)

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  import akka.stream.OverflowStrategy
  val done: Future[Done] = tweets
    .buffer(10, OverflowStrategy.dropHead)
    .map(_.hashtags)
    .reduce(_ ++ _)
    .mapConcat(identity)
    .map(_.name.toUpperCase)
    .runWith(Sink.foreach(println))

  implicit val ec = system.dispatcher
  done.onComplete(_ => system.terminate())
}

object QuickStart10 extends App {
   import scala.concurrent._
  import akka.{NotUsed, Done}
  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.stream.scaladsl._

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] = body.split(" ").collect {
      case t if t.startsWith("#") => Hashtag(t.replaceAll("[^#\\w]",""))
    }.toSet
  }

  val akkaTag = Hashtag("#akka")

  val tweets: Source[Tweet, NotUsed] = Source(
    Tweet(Author("rolandkuhn"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("bantonsson"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("drewhk"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("ktosopl"), System.currentTimeMillis, "#akka on the rocks!") ::
    Tweet(Author("mmartynas"), System.currentTimeMillis, "wow #akka !") ::
    Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("bananaman"), System.currentTimeMillis, "#bananas rock!") ::
    Tweet(Author("akkateam"), System.currentTimeMillis, "#akka rocks!") ::
    Tweet(Author("appleman"), System.currentTimeMillis, "#apples rock!") ::
    Tweet(Author("patriknw"), System.currentTimeMillis, "#akka !") ::
    Tweet(Author("drama"), System.currentTimeMillis, "we compared #apples to #oranges!") ::
    Nil)

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_ => 1)
  val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

  val counterGraph: RunnableGraph[Future[Int]] = tweets.via(count).toMat(sumSink)(Keep.right)
  val sum: Future[Int] = counterGraph.run()
  implicit val ec = system.dispatcher
  sum.foreach(c => println("Total tweets processed: " + c))

  val authors: Flow[Tweet, String, NotUsed]
    = Flow[Tweet].map(_.author.handle)
  val sumAuthors: Flow[String, String, NotUsed] =
    Flow[String].fold(Map[String, Int]())((acc, a) => acc + (a -> (acc.getOrElse(a, 0) + 1)))
    .mapConcat(_.map(m => m._1 + " " + m._2))
  val authorsCount: Sink[String, Future[String]] = Sink.fold("")(_ + _ + "\n")
  val countAuthors: RunnableGraph[Future[String]] =
    tweets.via(authors).via(sumAuthors).toMat(authorsCount)(Keep.right)
  val countRun = countAuthors.run()
  countRun.foreach(println)
 }


















