val ScalaTest = "org.scalatest"    %% "scalatest-funsuite" % "3.2.19"
val JUnit     = "org.junit.jupiter" % "junit-jupiter"      % "5.12.2"

ThisBuild / version       := "1.7.0"
ThisBuild / scalaVersion  := "3.3.6"
ThisBuild / versionScheme := Some("semver-spec")

ThisBuild / Test / fork                 := true
ThisBuild / Test / parallelExecution    := false
ThisBuild / Test / run / outputStrategy := Some(StdoutOutput)

ThisBuild / organization         := "com.github.charpov"
ThisBuild / organizationName     := "charpov"
ThisBuild / organizationHomepage := Some(url("https://github.com/charpov"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/charpov/TinyScalaUtils"),
    "scm:git@github.com:charpov/TinyScalaUtils.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "charpov",
    name = "Michel Charpentier",
    email = "Michel.Charpentier@unh.edu",
    url = url("https://github.com/charpov")
  )
)

ThisBuild / description     := "A tiny, no dependencies, Scala library, mostly used for teaching."
ThisBuild / licenses        := Seq(License.Apache2)
ThisBuild / homepage        := Some(url("https://github.com/charpov/TinyScalaUtils"))
ThisBuild / apiURL          := Some(url("https://charpov.github.io/TinyScalaUtils/"))
ThisBuild / releaseNotesURL := Some(url("https://github.com/charpov/TinyScalaUtils/releases"))

ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := Some(MavenCache("local-maven", baseDirectory.value / "docs" / "maven-add"))

ThisBuild / javacOptions := Seq("--release", "17")

ThisBuild / scalacOptions := Seq(
  "-deprecation",   // Emit warning and location for usages of deprecated APIs.
  "-feature",       // Emit warning for usages of features that should be imported explicitly.
  "-unchecked",     // Enable detailed unchecked (erasure) warnings.
  "-new-syntax",    // Require `then` and `do` in control expressions.
  "-source:future", // source version.
  "-language:noAutoTupling", // no auto-tupling
  "-Wunused:linted",         // unused stuff
  "-java-output-version:17", // Target Java 17, which is needed anyway
)

val docOptions = Compile / doc / scalacOptions := Seq(
  "-project:TinyScalaUtils",
  s"-project-version:${version.value}",
  "-project-footer:Copyright Michel Charpentier, 2025",
  "-siteroot:./site",
  "-doc-root-content:./api.md",
  "-author",
  "-groups",
  "-external-mappings:.*scala/.*::scaladoc3::https://scala-lang.org/api/3.x/," +
     ".*java/.*::javadoc::https://docs.oracle.com/en/java/javase/17/docs/api/java.base/",
  "-source-links:github://charpov/TinyScalaUtils/main",
)

lazy val tinyscalautils = (project in file("."))
   .aggregate(S, J, T)
   .settings(
     publish / skip := true,
   )

lazy val S = project
   .settings(
     name := "tiny-scala-utils",
     docOptions,
     Compile / doc / target := file("docs"),
     libraryDependencies ++= Seq(ScalaTest % Test),
   )

lazy val T = project
   .settings(
     name := "tiny-scala-utils-test",
     docOptions,
     libraryDependencies ++= Seq(ScalaTest),
   )
   .dependsOn(S)

lazy val J = project
   .settings(
     name := "tiny-scala-utils-java",
     Compile / compile / javacOptions ++= Seq("-deprecation", "-Xlint"),
     Compile / doc / javacOptions ++= Seq("--ignore-source-errors", "-Xdoclint:none"),
     Compile / doc / sources ~= (files => files.filterNot(_.getName.endsWith(".scala"))),
     libraryDependencies ++= Seq(JUnit % Test),
   )
   .dependsOn(S)
