package io.ikryvorotenko.tickets.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class AppMain(routes: Route, interface: String, port: Int)(
    implicit val system: ActorSystem,
    implicit val materializer: ActorMaterializer,
    implicit val executor: ExecutionContextExecutor
) extends LazyLogging {
  val serverBinding: Future[ServerBinding] = Http().bindAndHandle(routes, interface, port)

  serverBinding.onComplete {
    case Success(binding) => logger.info("Server started on port: " + binding.localAddress.getPort)
    case Failure(e)       => logger.error("Unable to start server: " + e.getMessage)
  }

  def shutdown(): Unit =
    serverBinding.onComplete {
      case Success(binding) =>
        logger.info("Shutting down server on port: " + binding.localAddress.getPort)
        binding.unbind()
      case Failure(t) => logger.error("Unable to stop server: " + t.getMessage)
    }
}

object AppMain extends LazyLogging {

  object AppConfig {
    private val config = ConfigFactory.load()
    val HttpInterface: String = config.getString("http.interface")
    val HttpPort: Int = config.getInt("http.port")
  }

  def main(args: Array[String]): Unit = {
    val csvPath = args match {
      case Array(path) => path
      case _           => sys.error("The path to csv file with shows info should be provided")
    }

    val routes = new Routes {
      override val showStorage: ShowsStorage = new ShowsStorage(csvPath)
    }

    implicit val system: ActorSystem = ActorSystem("AppMain")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executorContext: ExecutionContextExecutor = system.dispatcher
    import AppConfig._
    val uiTestingTool: AppMain = new AppMain(routes.routes, HttpInterface, HttpPort)

    scala.sys.addShutdownHook {
      uiTestingTool.shutdown()
      system.terminate()
    }
  }
}
