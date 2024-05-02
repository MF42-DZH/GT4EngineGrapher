Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / version      := "1.1.0"
ThisBuild / scalaVersion := "2.13.13"

lazy val sbtAssemblySettings = baseAssemblySettings ++ Seq(
  assembly / assemblyOutputPath    := baseDirectory.value / "GT4EngineGrapher.jar",
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", _*) => MergeStrategy.discard
    case _                        => MergeStrategy.first
  },
)

lazy val root = (project in file("."))
  .settings(
    name                 := "GT4EngineGrapher",
    sbtAssemblySettings,
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick"          % "3.5.0",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0",
      "org.slf4j"           % "slf4j-nop"      % "2.0.12",
      "com.h2database"      % "h2"             % "2.2.224",
      "org.xerial"          % "sqlite-jdbc"    % "3.45.2.0",
      "org.jfree"           % "jfreechart"     % "1.5.4",
    ),
  )
