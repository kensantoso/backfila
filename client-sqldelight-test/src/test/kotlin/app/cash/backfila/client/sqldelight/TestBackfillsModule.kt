package app.cash.backfila.client.sqldelight

import app.cash.backfila.client.BackfilaHttpClientConfig
import app.cash.backfila.client.misk.MiskBackfillModule
import app.cash.backfila.client.sqldelight.SqlDelightDatasourceBackfillModule.Companion.create
import app.cash.backfila.client.sqldelight.hockeydata.HockeyDataDatabase
import app.cash.backfila.client.sqldelight.persistence.HockeyDataDb
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.Query
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import com.google.inject.Provides
import java.sql.Connection
import javax.inject.Provider
import javax.inject.Singleton
import javax.sql.DataSource
import misk.inject.KAbstractModule
import misk.jdbc.DataSourceConfig
import misk.jdbc.DataSourceType
import misk.jdbc.JdbcModule

/**
 * Simulates a Backfills module where all the relevant backfills are registered.
 */
class TestBackfillsModule : KAbstractModule() {
  override fun configure() {
    install(
      JdbcModule(
        HockeyDataDb::class,
        DataSourceConfig(
          type = DataSourceType.MYSQL,
          username = "root",
          password = "",
          database = "hockeydata_testing",
          migrations_resource = "classpath:/migrations",
        ),
      ),
    )

    install(
      MiskBackfillModule(
        BackfilaHttpClientConfig(
          url = "test.url", slack_channel = "#test",
        ),
      ),
    )
    install(create<PlayerOriginBackfill>())
  }

  @Provides
  @Singleton
  fun provideHockeyDatabase(
    @HockeyDataDb dataSource: Provider<DataSource>,
  ): HockeyDataDatabase {
    val driver = object : JdbcDriver() {
      override fun getConnection(): Connection {
        val connection = dataSource.get().connection
        connection.autoCommit = true
        return connection
      }

      override fun closeConnection(connection: Connection) {
        connection.close()
      }

      override fun addListener(listener: Query.Listener, queryKeys: Array<String>) {
        // No-op. JDBC Driver is not set up for observing queries by default.
      }

      override fun removeListener(listener: Query.Listener, queryKeys: Array<String>) {
        // No-op. JDBC Driver is not set up for observing queries by default.
      }

      override fun notifyListeners(queryKeys: Array<String>) {
        // No-op. JDBC Driver is not set up for observing queries by default.
      }
    }

    return HockeyDataDatabase(
      driver = driver,
      hockeyPlayerAdapter = HockeyPlayer.Adapter(
        positionAdapter = EnumColumnAdapter(),
        shootsAdapter = EnumColumnAdapter(),
      ),
    )
  }
}
