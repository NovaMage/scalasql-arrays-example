val scala3Version = "3.6.2"

lazy val root = project
  .in(file("."))
  .settings(name := "scalasql-testing", version := "0.1.0-SNAPSHOT", scalaVersion := scala3Version)

libraryDependencies += "com.lihaoyi"   %% "scalasql"   % "0.1.15"
libraryDependencies += "org.postgresql" % "postgresql" % "42.7.5"
libraryDependencies += "org.testcontainers" % "postgresql" % "1.20.4"
