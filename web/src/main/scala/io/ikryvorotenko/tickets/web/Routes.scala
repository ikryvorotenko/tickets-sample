package io.ikryvorotenko.tickets.web

import akka.http.scaladsl.marshalling.{PredefinedToEntityMarshallers, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.ikryvorotenko.tickets.{ShowsParser, ShowsService}
import play.twirl.api.Html

import scala.language.postfixOps

trait Routes {

  val showStorage: ShowsStorage

  implicit val marshaller: ToEntityMarshaller[Html] =
    PredefinedToEntityMarshallers.stringMarshaller(`text/html`).compose(_.toString)

  lazy val routes: Route = path("") { redirect("/index.html", StatusCodes.PermanentRedirect) } ~
    path("index.html") {
      get {
        parameter('showDate.as[String] ?) {
          case Some(sd) => complete(html.index.render(Some(sd), findShows(sd)))
          case None     => complete(html.index.render(None, List.empty))
        }
      }
    }

  private def findShows(showDate: String) =
    ShowsService.findShowsToday(showStorage.list, ShowsParser.parseDate(showDate)).inventory

}
