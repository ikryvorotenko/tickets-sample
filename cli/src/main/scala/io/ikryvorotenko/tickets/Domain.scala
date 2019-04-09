package io.ikryvorotenko.tickets

import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

object Domain {

  /**
    * Show genre information
    */
  sealed trait Genre
  object Genre {
    def apply(name: String): Genre = name match {
      case "DRAMA"   => Drama
      case "MUSICAL" => Musical
      case "COMEDY"  => Comedy
      case _         => sys.error(s"Unknown genre $name")
    }
  }
  case object Musical extends Genre
  case object Comedy extends Genre
  case object Drama extends Genre

  /**
    * Show state determines the details on how the tickers are sold for specific show
    */
  sealed abstract class ShowState(val available: Long, val left: Long, val status: String)
  case class OpenForSale(a: Long, l: Long) extends ShowState(a, l, "open for sale")
  case class SaleNotStarted(l: Long) extends ShowState(0, l, "sale not started")
  case object SoldOut extends ShowState(0, 0, "sold out")
  case object InThePast extends ShowState(0, 0, "in the past")

  /**
    * Place the show is taken place. Used to determine daily available tickets for sale and total seats capacity.
    */
  sealed abstract class Place(val dailyAvailable: Int, val capacity: Int)
  case object BigHall extends Place(10, 200)
  case object SmallHall extends Place(5, 100)

  /**
    * General show information
    */
  case class Show(title: String, openingDate: Instant, genre: Genre) {
    lazy val saleStartDate: Instant = openingDate.minus(25, DAYS)
    lazy val endDate: Instant = openingDate.plus(100, DAYS)

    def hasEnded(date: Instant): Boolean = date.compareTo(endDate) >= 0
    def hasStarted(date: Instant): Boolean = date.compareTo(openingDate) >= 0
  }

  /**
    * Show with price and other information specific for certain dates
    */
  case class SpecificShow(show: Show, price: Double, state: ShowState)
  object SpecificShow {

    /**
      * Create the SpecificShow for provided Show based on showDate and queryDate
      */
    def forDate(show: Show, queryDate: Instant, showDate: Instant) =
      SpecificShow(show, price(show, showDate), showState(show, queryDate, showDate))

    private def price(show: Show, showDate: Instant): Double = {
      val discount =
        if (daysBetween(show.openingDate, showDate) >= 80 && !show.hasEnded(showDate)) 0.2 else 0
      def discounted(price: Int): Double = price - (price * discount)

      show.genre match {
        case Musical => discounted(70)
        case Comedy  => discounted(50)
        case Drama   => discounted(40)
      }
    }

    private def showState(show: Show, queryDate: Instant, showDate: Instant): ShowState = {
      val place = if (daysBetween(show.openingDate, showDate) <= 60) BigHall else SmallHall
      def ticketsLeft(days: Long) = place.capacity - (place.dailyAvailable * (25 - days))

      daysBetween(queryDate, showDate) match {
        case d if d <= 0 || showDate.compareTo(show.endDate) >= 0 => InThePast
        case d if d <= 5                                          => SoldOut
        case d if d <= 25                                         => OpenForSale(place.dailyAvailable, ticketsLeft(d))
        case d if d > 25                                          => SaleNotStarted(place.capacity)
      }
    }

    private def daysBetween(fromInclusive: Instant, toInclusive: Instant): Long =
      DAYS.between(fromInclusive, toInclusive.plus(1, DAYS))
  }

}
