package io.github.kdroidfilter.seforimapp.framework.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver

/**
 * Makes Otzaria's link schema readable by SeforimLibrary without modifying the shared database.
 *
 * Otzaria replaced isDeclaredBase with baseProvenance. A temporary view shadows the main link
 * table on this connection and exposes the column shape expected by generated SQLDelight queries.
 */
internal fun installOtzariaLinkSchemaCompatibility(driver: SqlDriver) {
    val columns =
        driver.executeQuery(
            identifier = null,
            sql = "PRAGMA main.table_info(link)",
            mapper = { cursor ->
                val names = mutableSetOf<String>()
                while (cursor.next().value) {
                    cursor.getString(1)?.let(names::add)
                }
                QueryResult.Value(names)
            },
            parameters = 0,
        ).value

    if ("isDeclaredBase" in columns || "baseProvenance" !in columns) return

    val requiredColumns =
        setOf(
            "id",
            "sourceBookId",
            "targetBookId",
            "sourceLineId",
            "targetLineId",
            "targetLineIndex",
            "targetBookOrderIndex",
            "connectionTypeId",
        )
    if (!columns.containsAll(requiredColumns)) return

    driver.execute(
        identifier = null,
        sql =
            """
            CREATE TEMP VIEW link AS
            SELECT
                id,
                sourceBookId,
                targetBookId,
                sourceLineId,
                targetLineId,
                targetLineIndex,
                targetBookOrderIndex,
                connectionTypeId,
                CASE WHEN baseProvenance = 1 THEN 1 ELSE 0 END AS isDeclaredBase
            FROM main.link
            """.trimIndent(),
        parameters = 0,
    ).value
}
