val jcip      = "net.jcip"          % "jcip-annotations" % "1.0"
val ScalaTest = "org.scalatest"    %% "scalatest"        % "3.2.14"
val JUnit     = "org.junit.jupiter" % "junit-jupiter"    % "5.9.1" % Test

ThisBuild / version       := "1.0.0"
ThisBuild / scalaVersion  := "3.2.0"
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

ThisBuild / description := "A tiny, no (real) dependencies, Scala library, mostly used for teaching."
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / homepage := Some(url("https://github.com/charpov/TinyScalaUtils"))
ThisBuild / apiURL   := Some(url("https://charpov.github.io/TinyScalaUtils/"))

val GithubPackagePublish =
   "GitHub Package Registry" at "https://maven.pkg.github.com/charpov/TinyScalaUtils"
//ThisBuild / credentials += Credentials(Path.userHome / ".sbt" / "github-credentials")

ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := Some(MavenCache("local-maven", baseDirectory.value / "docs" / "maven-add"))

val scalaCompilerOptions = Compile / compile / scalacOptions := Seq(
  "-deprecation",   // Emit warning and location for usages of deprecated APIs.
  "-feature",       // Emit warning for usages of features that should be imported explicitly.
  "-unchecked",     // Enable detailed unchecked (erasure) warnings.
  "-new-syntax",    // Require `then` and `do` in control expressions.
  "-source:future", // source version.
  "-language:noAutoTupling",
)

val javaCompilerOptions = Compile / compile / javacOptions := Seq(
  "--release",
  "11",
  "-deprecation",
  "-Xlint",
)

val docOptions = Compile / doc / scalacOptions := Seq(
  "-project",
  "TinyScalaUtils",
  "-project-version",
  version.value,
  "-project-footer",
  "Copyright Michel Charpentier, 2022",
  "-siteroot",
  "./site",
  "-doc-root-content",
  "./api.md",
  "-author",
  "-groups",
  "-external-mappings:.*scala.*::scaladoc3::https://scala-lang.org/api/3.x/," +
     ".*java.*::javadoc::https://docs.oracle.com/en/java/javase/11/docs/api/java.base/",
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
     scalaCompilerOptions,
     javaCompilerOptions,
     docOptions,
     Compile / doc / target := file("docs"),
     libraryDependencies ++= Seq(jcip, ScalaTest % Test),
   )

lazy val T = project
   .settings(
     name := "tiny-scala-utils-test",
     scalaCompilerOptions,
     javaCompilerOptions,
     docOptions,
     libraryDependencies ++= Seq(jcip, ScalaTest),
   )
   .dependsOn(S)

lazy val J = project
   .settings(
     name := "tiny-scala-utils-java",
     scalaCompilerOptions,
     javaCompilerOptions,
     Compile / doc / javacOptions ++= Seq("--ignore-source-errors"),
     Compile / doc / sources ~= (files => files.filterNot(_.getName.endsWith(".scala"))),
     libraryDependencies ++= Seq(jcip, JUnit),
   )
   .dependsOn(S)
