package io.ikryvorotenko.tickets

import java.io.File
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, ZoneOffset}

import io.ikryvorotenko.tickets.Domain.{Genre, Show}

import scala.io.Source
import scala.util.control.NonFatal

object ShowsParser {

  /**
    * Parses file as csv format
    *
    * The columns consist of the title, opening day (the day of the first performance of a show),
    * and genre of the show
    */
  def parse(file: File): Either[String, List[Show]] = parse(Source.fromFile(file))

  /**
    * Parses source as csv format
    *
    * The columns consist of the title, opening day (the day of the first performance of a show),
    * and genre of the show
    */
  def parse(source: Source): Either[String, List[Show]] = {
    import com.github.tototoshi.csv._

    def parseLine(fields: Seq[String]) =
      fields match {
        case Seq(t, d, g) => Show(t, parseDate(d), Genre(g))
        case other        => sys.error(s"Incorrect line format: $other")
      }

    val reader = CSVReader.open(source)
    try Right(reader.iterator.map(parseLine).to[List])
    catch {
      case NonFatal(e) => Left(s"Failed parsing csv file, caused: ${e.getMessage}")
    } finally reader.close()
  }

  def parseDate(date: String): Instant =
    DateTimeFormatter.ISO_DATE
      .parse[LocalDate](date, LocalDate.from _)
      .atStartOfDay()
      .toInstant(ZoneOffset.UTC)

}
