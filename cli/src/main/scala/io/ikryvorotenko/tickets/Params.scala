package io.ikryvorotenko.tickets

import java.io.File
import java.time.Instant

import scala.util.Try

case class Params(file: File, queryDate: Instant, showDate: Instant)

object Params {

  /**
    * Parses incoming parameters according to spec:
    * 1. an input file consisting of the show inventory. Where the input file is in the comma
    * separated value (CSV) format, and the columns consist of the title, opening day (the day
    * of the first performance of a show), and genre of the show.
    * 2. query-date, the reference date that determines the inventory state.
    * 3. show-date, the date for which you want to know how many tickets are left.
    */
  def parse(args: Array[String]): Either[String, Params] = args match {
    case Array(file, queryDate, showDate) =>
      for {
        f  <- parseFile(file)
        qd <- parseDate(queryDate)
        sd <- parseDate(showDate)
      } yield Params(f, qd, sd)
    case _ => Left("Incorrect number of arguments, expecting 3: file, queryDate and showDate")
  }

  private def parseDate(date: String): Either[String, Instant] =
    Try(ShowsParser.parseDate(date)).toEither.left
      .map(e => s"Failed parsing date: $date, caused by ${e.getMessage}")

  private def parseFile(path: String): Either[String, File] =
    Try(new File(path)).toEither.left
      .map(e => s"Could not read file $path, caused ${e.getMessage}")
      .right
      .filter(f => f.exists())
      .getOrElse(Left(s"File $path does not exist"))

}
