plugins {
    scala
    application
    id("com.github.maiflai.scalatest") version "0.25"
}

application {
    mainClassName = "io.ikryvorotenko.tickets.Main"
}

val scalaVersion = "2.12"
dependencies {
    implementation("org.scala-lang:scala-library:2.12.8")
    implementation("io.circe" scala "circe-core" version "0.11.1")
    implementation("io.circe" scala "circe-generic" version "0.11.1")
    implementation("com.github.tototoshi" scala "scala-csv" version "1.3.5")

    testImplementation("org.scalatest" scala "scalatest" version "3.0.5")
    testRuntime("org.pegdown:pegdown:1.4.2")
}

infix fun String.version(that: String): String = "$this:$that"
infix fun String.scala(str: String): String = this.version("${str}_$scalaVersion")