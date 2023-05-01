plugins {
  kotlin("jvm")
  id("app.cash.sqldelight") version Versions.sqldelight
}

// TODO May have to make the other module dependent on this test module although we would have to avoid cycles.
sqldelight {
  databases {
    create("HockeyDataDatabase") {
      packageName.set("app.cash.backfila.client.sqldelight.hockeydata")
      dialect(Dependencies.sqldelightMysqlDialect)
      srcDirs.setFrom(listOf("src/main/sqldelight", "src/main/resources/migrations"))
      deriveSchemaFromMigrations.set(true)
      migrationOutputDirectory.set(file("$buildDir/resources/main/migrations"))
      verifyMigrations.set(true)
    }
  }
}

val compileKotlin by tasks.getting {
  dependsOn("generateMainHockeyDataDatabaseMigrations")
}

dependencies {
  implementation(Dependencies.guava)
  implementation(Dependencies.moshiCore)
  implementation(Dependencies.moshiKotlin)
  implementation(Dependencies.wireRuntime)
  implementation(Dependencies.guice)
  implementation(Dependencies.kotlinStdLib)
  implementation(Dependencies.sqldelightJdbcDriver)
  implementation(Dependencies.sqldelightMysqlDialect)
  implementation(Dependencies.okHttp)
  implementation(Dependencies.okio)
  implementation(Dependencies.retrofit)
  implementation(Dependencies.retrofitMock)
  implementation(Dependencies.retrofitMoshi)
  implementation(Dependencies.retrofitWire)
  implementation(Dependencies.wireMoshiAdapter)

  implementation(project(":client"))
  implementation(project(":client-sqldelight"))

  testImplementation(Dependencies.assertj)
  testImplementation(Dependencies.junitEngine)
  testImplementation(Dependencies.kotlinTest)

  testImplementation(project(":backfila-embedded"))
  testImplementation(project(":client-testing"))

  // ****************************************
  // For TESTING purposes only. We only want Misk for easy testing.
  // DO NOT turn these into regular dependencies.
  // ****************************************
  testImplementation(Dependencies.misk)
  testImplementation(Dependencies.miskActions)
  testImplementation(Dependencies.miskInject)
  testImplementation(Dependencies.miskJdbc)
  testImplementation(Dependencies.miskJdbcTesting)
  testImplementation(Dependencies.miskTesting)
  testImplementation(project(":client-misk"))
}
