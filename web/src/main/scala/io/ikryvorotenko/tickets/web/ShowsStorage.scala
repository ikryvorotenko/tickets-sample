package io.ikryvorotenko.tickets.web

import io.ikryvorotenko.tickets.Domain.Show
import io.ikryvorotenko.tickets.ShowsParser

import scala.io.Source

/**
  * In memory shows storage. Use csv file as a data source.
  *
  * Fail fast if the file's incorrect.
  */
class ShowsStorage {
  private val shows: List[Show] = ShowsParser.parse(Source.fromResource("shows.csv")) match {
    case Right(r) => r
    case Left(l)  => sys.error(s"Could not load shows csv, caused $l")
  }

  def list: List[Show] = shows
}
