import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

// https://github.com/grpc/grpc-java/blob/v0.9.0/build.gradle#L130
// https://github.com/grpc/grpc-java/blob/v0.9.0/examples/build.gradle#L21
val json = libraryDependencies += "org.glassfish" % "javax.json" % "1.0.4"

val unusedWarnings = (
  "-Ywarn-unused" ::
  "-Ywarn-unused-import" ::
  Nil
)

lazy val root = project.in(file(".")).aggregate(
  grpcJavaSample, grpcScalaSample
)

lazy val grpcScalaSample = project.in(file("grpc-scala")).settings(
  PB.protobufSettings,
  PB.runProtoc in PB.protobufConfig := { args =>
    com.github.os72.protocjar.Protoc.runProtoc("-v300" +: args.toArray)
  },
  version in PB.protobufConfig := "3.0.0-beta-1",
  watchSources ++= (((sourceDirectory in Compile).value / "protobuf") ** "*.proto").get,
  libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % "0.5.18",
  json,
  unmanagedResourceDirectories in Compile += (baseDirectory in LocalRootProject).value / "grpc-java/examples/src/main/resources",
  sourceDirectory in PB.protobufConfig := (baseDirectory in LocalRootProject).value / "grpc-java/examples/src/main/proto",
  scalaVersion := "2.11.7",
  scalacOptions ++= (
    "-deprecation" ::
    "-unchecked" ::
    "-Xlint" ::
    "-language:existentials" ::
    "-language:higherKinds" ::
    "-language:implicitConversions" ::
    "-Yno-adapted-args" ::
    Nil
  ) ::: unusedWarnings,
  Seq(Compile, Test).flatMap(c =>
    scalacOptions in (c, console) ~= {_.filterNot(unusedWarnings.toSet)}
  )
)

lazy val grpcJavaSample = project.in(file("grpc-java/examples")).settings(
  json,
  libraryDependencies += "io.grpc" % "grpc-all" % "0.9.0",
  autoScalaLibrary := false,
  unmanagedSourceDirectories in Compile += baseDirectory.value / "src/generated/main/"
)
