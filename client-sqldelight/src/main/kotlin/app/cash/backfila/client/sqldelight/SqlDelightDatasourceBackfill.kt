package app.cash.backfila.client.sqldelight

import app.cash.backfila.client.Backfill
import app.cash.backfila.client.BackfillConfig
import app.cash.backfila.client.PrepareBackfillConfig
import app.cash.sqldelight.Transacter

abstract class SqlDelightDatasourceBackfill<D: Transacter, R : Any, P : Any> : Backfill {

  /**
   * Do Validation and return a map of partition names to databases.
   *
   * Override this and throw an exception to prevent the backfill from being created.
   * This is also a good place to do any prep work before batches are run as well.
   */
  abstract fun prepareAndValidateBackfill(config: PrepareBackfillConfig<P>): Map<String, D>

  /**
   * Called for each batch of records.
   * Override in a backfill to process all records in a batch.
   */
  open fun runBatch(records: List<@JvmSuppressWildcards R>, config: BackfillConfig<P>) {
    records.forEach { runOne(it, config) }
  }

  /**
   * Called for each record.
   * Override in a backfill to process one record at a time.
   */
  open fun runOne(record: R, config: BackfillConfig<P>) {
  }
}
