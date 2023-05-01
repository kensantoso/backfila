package app.cash.backfila.client.sqldelight.internal

import app.cash.backfila.client.spi.BackfilaParametersOperator
import app.cash.backfila.client.spi.BackfillOperator
import app.cash.backfila.client.sqldelight.SqlDelightDatasourceBackfill
import app.cash.backfila.protos.clientservice.GetNextBatchRangeRequest
import app.cash.backfila.protos.clientservice.GetNextBatchRangeResponse
import app.cash.backfila.protos.clientservice.KeyRange
import app.cash.backfila.protos.clientservice.PrepareBackfillRequest
import app.cash.backfila.protos.clientservice.PrepareBackfillResponse
import app.cash.backfila.protos.clientservice.RunBatchRequest
import app.cash.backfila.protos.clientservice.RunBatchResponse
import app.cash.sqldelight.Transacter
import okio.ByteString.Companion.encodeUtf8

class SqlDelightDatasourceBackfillOperator<D: Transacter, R : Any, P : Any>(
  override val backfill: SqlDelightDatasourceBackfill<D, R, P>,
  private val parametersOperator: BackfilaParametersOperator<P>,
) : BackfillOperator {

  override fun name(): String = backfill.javaClass.toString()

  override fun prepareBackfill(request: PrepareBackfillRequest): PrepareBackfillResponse {
    val config = parametersOperator.constructBackfillConfig(request)
    val partitionMap = backfill.prepareAndValidateBackfill(config)

    val partitions = partitionMap.map {
      PrepareBackfillResponse.Partition.Builder()
        .partition_name(it.key)
        .backfill_range(
          computeOverallRange()
        ).build()
    }
    return PrepareBackfillResponse.Builder()
      .partitions(partitions)
      .build()
  }

  override fun getNextBatchRange(request: GetNextBatchRangeRequest): GetNextBatchRangeResponse {
    val config = parametersOperator.constructBackfillConfig(request)
      /*
    val pathPrefix = backfill.getPrefix(config)

    val batchSize = request.batch_size.toInt()
    val previousEndKey = request.previous_end_key?.utf8()?.toLong() ?: 0L
    val fileSize = s3Service.getFileSize(
      backfill.getBucket(config),
      pathPrefix + request.partition_name,
    )

    if (previousEndKey == fileSize) {
      // Either the file is empty or we have reached the end of the file.
      return GetNextBatchRangeResponse.Builder().batches(
        listOf(),
      ).build()
    }

    // When precomputing all we are trying to do is figure out how big the file is.
    if (request.precomputing == true) {
      require(previousEndKey == 0L) {
        "The file size changed between batch calculations."
      }
      return GetNextBatchRangeResponse.Builder().batches(
        listOf(
          GetNextBatchRangeResponse.Batch.Builder()
            .batch_range(
                KeyRange(
                    (0L).toString().encodeUtf8(),
                    fileSize.toString().encodeUtf8(),
                ),
            )
            .matching_record_count(fileSize)
            .scanned_record_count(fileSize)
            .build(),
        ),
      ).build()
    }

    val fileStream = s3Service.getFileStreamStartingAt(
      backfill.getBucket(config),
      pathPrefix + request.partition_name,
      previousEndKey,
    )

    val recordBytes = mutableListOf<Long>()
    val stopwatch = Stopwatch.createStarted()
    while (!fileStream.exhausted() && // There is file to stream.
      recordBytes.size.floorDiv(batchSize) < request.compute_count_limit && // We want more records.
      (
        request.compute_time_limit_ms == null || // Either there is no limit or we are withing our timeframe.
          stopwatch.elapsed(TimeUnit.MILLISECONDS) <= request.compute_time_limit_ms
        )
    ) {
      val peekSource = fileStream.peek()
      val bytes = backfill.recordStrategy.calculateNextRecordBytes(peekSource)
      require(bytes > 0) { "Failed to consume any streamed bytes for ${request.partition_name}" }
      recordBytes += bytes
      fileStream.skip(bytes)
    }

    var offset = previousEndKey
    val batches = mutableListOf<GetNextBatchRangeResponse.Batch>()
    recordBytes.chunked(batchSize).map { it.sum() }.forEach { size ->
      batches += GetNextBatchRangeResponse.Batch.Builder()
        .batch_range(
            KeyRange(
                (offset).toString().encodeUtf8(),
                (offset + size).toString().encodeUtf8(),
            ),
        )
        .matching_record_count(size)
        .scanned_record_count(size)
        .build()
      offset += size
    }
*/
    return GetNextBatchRangeResponse.Builder()
      .batches(listOf())
      .build()
  }

  override fun runBatch(request: RunBatchRequest): RunBatchResponse {
    val config = parametersOperator.constructBackfillConfig(request)
    /*
    val pathPrefix = backfill.getPrefix(config.prepareConfig())
    val batchRange = request.batch_range.decode()
    requireNotNull(batchRange.end) { "Batch was created without a range end." }

    val byteString = s3Service.getWithSeek(
      backfill.getBucket(config.prepareConfig()),
      pathPrefix + request.partition_name,
      batchRange.start,
      batchRange.end,
    )

    val batch = backfill.recordStrategy.bytesToRecords(byteString)

    backfill.runBatch(batch, config)
*/
    return RunBatchResponse.Builder()
      .build()
  }

  data class DecodedRange(
    val start: Long,
    val end: Long,
  )
  private fun KeyRange.decode(): DecodedRange {
    val start = this.start.utf8().toLong()
    val end = this.end.utf8().toLong()
    return DecodedRange(start, end)
  }
}

/**
 * TODO something smart
 */
  private fun computeOverallRange(): KeyRange? = KeyRange.Builder()
    .start((0L).toString().encodeUtf8())
    .build()
