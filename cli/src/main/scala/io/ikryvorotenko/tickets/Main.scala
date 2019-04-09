package io.ikryvorotenko.tickets

import io.circe.syntax._

object Main {

  def main(args: Array[String]): Unit = {
    val result = for {
      params <- Params.parse(args)
      shows  <- ShowsParser.parse(params.file)
      queryResult = ShowsService.findShows(shows, params.queryDate, params.showDate)
    } yield {
      import ShowsService.encoders._
      print(queryResult.asJson.spaces2)
    }

    result match {
      case Right(_)      => System.exit(0)
      case Left(message) => sys.error(s"Failed parsing parameters, caused by: $message")
    }

  }
}
