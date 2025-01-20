Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / version      := "1.3.0"
ThisBuild / scalaVersion := "2.13.16"

lazy val sbtAssemblySettings = baseAssemblySettings ++ Seq(
  assembly / assemblyOutputPath    := baseDirectory.value / "GTEngineGrapher.jar",
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs @ _*) =>
      xs.map(_.toLowerCase) match {
        case "services" :: _ => MergeStrategy.filterDistinctLines
        case _               => MergeStrategy.discard
      }
    case _                             => MergeStrategy.first
  },
)

lazy val root = (project in file("."))
  .settings(
    name                 := "GTEngineGrapher",
    sbtAssemblySettings,
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick"          % "3.5.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.2",
      "org.slf4j"           % "slf4j-nop"      % "2.0.16",
      "org.xerial"          % "sqlite-jdbc"    % "3.48.0.0",
      "org.jfree"           % "jfreechart"     % "1.5.5",
    ),
  )
