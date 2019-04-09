package io.ikryvorotenko.tickets

import io.ikryvorotenko.tickets.Domain.{Comedy, Drama, Show}
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class ShowsParserSpec extends FlatSpec with Matchers {

  "ShowParser" should "parse provided csv string" in {
    val csv = """"1984",2018-10-14,"DRAMA"
          |"39 STEPS, THE ",2018-11-10,"COMEDY"""".stripMargin
    ShowsParser.parse(Source.fromString(csv)) shouldBe Right(
      List(
        Show("1984", ShowsParser.parseDate("2018-10-14"), Drama),
        Show("39 STEPS, THE ", ShowsParser.parseDate("2018-11-10"), Comedy)
      )
    )
  }

  it should "fail in case of incorrect format" in {
    val csv =
      """"1984",2018-10-14,"DRAMA"
        |"39 STEPS, THE ",2018-11-10,"COMEDY","extra field"
      """.stripMargin

    ShowsParser.parse(Source.fromString(csv)) shouldBe 'Left
  }

}
