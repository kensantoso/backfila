package app.cash.backfila.client.sqldelight

import app.cash.backfila.client.BackfillConfig
import app.cash.backfila.client.PrepareBackfillConfig
import app.cash.backfila.client.sqldelight.hockeydata.HockeyDataDatabase
import com.squareup.wire.internal.newMutableList
import javax.inject.Inject

class PlayerOriginBackfill @Inject constructor(
  val hockeyDataDatabase: HockeyDataDatabase
) : SqlDelightDatasourceBackfill<HockeyDataDatabase, String, PlayerOriginBackfill.PlayerOriginParameters>() {
  val backfilledPlayers = newMutableList<Pair<String, String>>()

  override fun prepareAndValidateBackfill(config: PrepareBackfillConfig<PlayerOriginParameters>): Map<String, HockeyDataDatabase> {
    check(config.parameters.validate) { "Validate failed" }
    return mapOf("only" to hockeyDataDatabase)
  }

  override fun runOne(record: String, config: BackfillConfig<PlayerOriginParameters>) {
    if (record.contains(config.parameters.originRegex)) {
      backfilledPlayers.add(config.partitionName to record)
    }
  }

  data class PlayerOriginParameters(
    val originRegex: String = "CAN",
    val validate: Boolean = true,
  )
}
