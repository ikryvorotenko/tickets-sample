package io.ikryvorotenko.tickets

import java.time.{Instant, LocalDate, ZoneOffset}

import io.circe.{Encoder, Json}
import io.ikryvorotenko.tickets.Domain.{Genre, Show, SpecificShow}

object ShowsService {

  def findShowsToday(shows: List[Show], showDate: Instant): QueryResult =
    findShows(shows, LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), showDate)

  /**
    * Lookup for shows on provided showDate in provided shows list using the queryDate.
    */
  def findShows(shows: List[Show], queryDate: Instant, showDate: Instant): QueryResult = {
    val showsByGenre = shows
      .filter(_.hasStarted(showDate))
      .map(SpecificShow.forDate(_, queryDate, showDate))
      .groupBy { _.show.genre }

    QueryResult(showsByGenre.map((GenreResult.apply _).tupled).to[List])
  }

  case class QueryResult(inventory: List[GenreResult])
  case class GenreResult(genre: Genre, shows: List[SpecificShow])

  object encoders {
    import io.circe.generic.semiauto._
    implicit val queryResultEncoder: Encoder[QueryResult] = deriveEncoder
    implicit val genreResultEncoder: Encoder[GenreResult] = deriveEncoder
    implicit val genreEncoder: Encoder[Genre] = Encoder[String].contramap(_.toString.toLowerCase)
    implicit val specificShowEncoder: Encoder[SpecificShow] = (specificShow: SpecificShow) => {
      Json.obj(
        "title" -> Json.fromString(specificShow.show.title.toLowerCase),
        "tickets_left" -> Json.fromString(specificShow.state.left.toString),
        "tickets_available" -> Json.fromString(specificShow.state.available.toString),
        "status" -> Json.fromString(specificShow.state.status.toLowerCase())
      )
    }
  }
}
