package app.cash.backfila.client.misk.testing

import app.cash.backfila.client.misk.internal.PartitionCursor
import org.assertj.core.api.AbstractAssert

class PartitionCursorAssert(
  val partitionCursor: PartitionCursor
) : AbstractAssert<PartitionCursorAssert, PartitionCursor>(partitionCursor, PartitionCursorAssert::class.java) {
  fun isDone(): PartitionCursorAssert {
    if (!partitionCursor.done) {
      failWithMessage("Expected the backfill partition ${partitionCursor.partitionName} " +
          "to be done but it isn't. Cursor $partitionCursor")
    }
    return this // Return the current assertion for method chaining.
  }

  fun isNotDone(): PartitionCursorAssert {
    if (partitionCursor.done) {
      failWithMessage("Expected the backfill partition ${partitionCursor.partitionName} " +
          "to be not done but it is. Cursor $partitionCursor")
    }
    return this // Return the current assertion for method chaining.
  }
}

fun assertThat(partitionCursor: PartitionCursor): PartitionCursorAssert {
  return PartitionCursorAssert(partitionCursor)
}
