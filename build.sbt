name := "redis-ui"

organization := "jp.dwango"

version := "0.0.1"

scalaVersion := "2.10.2"

libraryDependencies += "net.debasishg" % "redisclient_2.10" % "2.11"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.3"

libraryDependencies += "com.miglayout" % "miglayout" % "3.7.3"

// Add dependency on ScalaFX library, for use with JavaFX 2.2/Java 7
libraryDependencies += "org.scalafx" %% "scalafx" % "1.0.0-R8"

// Add dependency on JavaFX library based on JAVA_HOME variable
//unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar"))

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true

