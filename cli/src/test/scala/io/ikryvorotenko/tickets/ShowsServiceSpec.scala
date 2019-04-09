package io.ikryvorotenko.tickets

import java.time.temporal.ChronoUnit.DAYS

import io.ikryvorotenko.tickets.Domain._
import io.ikryvorotenko.tickets.ShowsParser._
import io.ikryvorotenko.tickets.ShowsService._
import org.scalatest.{FlatSpec, Matchers}

class ShowsServiceSpec extends FlatSpec with Matchers {

  private val cats = Show("Cats", parseDate("2018-06-01"), Domain.Musical)
  private val comedyOfError = Show("Comedy of Errors", parseDate("2018-07-01"), Domain.Comedy)
  private val everyMan = Show("Everyman", parseDate("2018-08-01"), Domain.Drama)

  val shows = List(cats, comedyOfError, everyMan)

  "ShowsService" should "find not started shows only" in {
    val actualShows =
      ShowsService.findShows(shows, parseDate("2018-01-01"), parseDate("2018-07-01"))

    actualShows shouldBe QueryResult(
      List(
        GenreResult(Musical, List(SpecificShow(cats, 70, SaleNotStarted(200)))),
        GenreResult(Comedy, List(SpecificShow(comedyOfError, 50, SaleNotStarted(200)))),
      )
    )
  }

  it should "find open for sale shows (first day in small hall)" in {
    val smallHallShows = ShowsService.findShows(
      List(cats),
      showDate = cats.openingDate.plus(60, DAYS),
      queryDate = cats.openingDate.plus(41, DAYS)
    )

    val bigHallShows = ShowsService.findShows(
      List(cats),
      showDate = cats.openingDate.plus(59, DAYS),
      queryDate = cats.openingDate.plus(41, DAYS)
    )

    smallHallShows shouldBe QueryResult(
      List(GenreResult(Musical, List(SpecificShow(cats, 70, OpenForSale(5, 75)))))
    )
    bigHallShows shouldBe QueryResult(
      List(GenreResult(Musical, List(SpecificShow(cats, 70, OpenForSale(10, 140)))))
    )
  }

  it should "find open for sale shows with small hall" in {
    val actualShows =
      ShowsService.findShows(shows, parseDate("2018-08-01"), parseDate("2018-08-15"))

    actualShows shouldBe QueryResult(
      List(
        GenreResult(Musical, List(SpecificShow(cats, 70, OpenForSale(5, 50)))),
        GenreResult(Drama, List(SpecificShow(everyMan, 40, OpenForSale(10, 100)))),
        GenreResult(Comedy, List(SpecificShow(comedyOfError, 50, OpenForSale(10, 100)))),
      )
    )
  }

  it should "find sold out and in the past shows" in {
    val actualShows =
      ShowsService.findShows(shows, parseDate("2018-11-01"), parseDate("2018-11-01"))

    actualShows shouldBe QueryResult(
      List(
        GenreResult(Musical, List(SpecificShow(cats, 70, InThePast))),
        GenreResult(Drama, List(SpecificShow(everyMan, 32, SoldOut))),
        GenreResult(Comedy, List(SpecificShow(comedyOfError, 50, InThePast)))
      )
    )
  }

  it should "find in the past shows and not future shows" in {
    val showDate = parseDate("2017-06-01")
    val bestShow = Show("2017 best show", showDate, Domain.Musical)
    val actualShows =
      ShowsService.findShows(
        bestShow :: shows,
        showDate = showDate.plus(100, DAYS),
        queryDate = showDate.plus(10, DAYS)
      )

    actualShows shouldBe QueryResult(
      List(GenreResult(Musical, List(SpecificShow(bestShow, 70, InThePast))))
    )
  }

}
