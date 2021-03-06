/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2020 Qoosky
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

lazy val root = (project in file(".")).
  settings(
    name := "websocket-relay-server",
    version := "1.1",
    scalaVersion := "2.13.1",
    mainClass in assembly := Some("qoosky.websocketrelayserver.Main"),
    retrieveManaged := true,
    parallelExecution := false,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.1" % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "com.typesafe.akka" %% "akka-testkit" % "2.5.31",
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.31",
      "com.typesafe.akka" %% "akka-actor" % "2.5.31",
      "com.typesafe.akka" %% "akka-stream" % "2.5.31",
      "com.typesafe.akka" %% "akka-http" % "10.1.11",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
      "com.typesafe.akka" %% "akka-http-xml" % "10.1.11",
      "commons-daemon" % "commons-daemon" % "1.2.2"
    )
  )
