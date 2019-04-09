import us.hexcoder.gradle.twirl.plugin.TwirlExtension

plugins {
    scala
    application
}

application {
    mainClassName = "io.ikryvorotenko.tickets.web.AppMain"
}

val scalaVersion = "2.12"
dependencies {
    implementation("org.scala-lang:scala-library:2.12.8")

    implementation(project(":cli"))
    implementation("com.typesafe.akka" scala "akka-http" version "10.1.7")
    implementation("de.heikoseeberger" scala "akka-http-circe" version "1.25.2")
    implementation("com.typesafe.scala-logging" scala "scala-logging" version "3.9.2")
    implementation("com.typesafe.play" scala "twirl-api" version "1.4.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

infix fun String.version(that: String): String = "$this:$that"
infix fun String.scala(str: String): String = this.version("${str}_$scalaVersion")

/* ------------- TWIRL CONFIGURATION -------------*/
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("us.hexcoder:gradle-twirl:1.1.0")
    }
}
apply(plugin = "twirl")
configure<TwirlExtension> {
    imports = emptySet()
}
/*------------------------------------------------*/
