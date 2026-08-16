package io.github.kdroidfilter.seforimapp.framework.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OtzariaLinkSchemaCompatibilityTest {
    @Test
    fun exposesOtzariaBaseProvenanceAsTheExpectedBooleanColumn() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            driver.execute(
                identifier = null,
                sql =
                    """
                    CREATE TABLE link (
                        id INTEGER NOT NULL PRIMARY KEY,
                        sourceBookId INTEGER NOT NULL,
                        targetBookId INTEGER NOT NULL,
                        sourceLineId INTEGER NOT NULL,
                        targetLineId INTEGER NOT NULL,
                        targetLineIndex INTEGER NOT NULL,
                        targetBookOrderIndex INTEGER NOT NULL,
                        connectionTypeId INTEGER NOT NULL,
                        baseProvenance INTEGER NOT NULL
                    )
                    """.trimIndent(),
                parameters = 0,
            )
            driver.execute(
                identifier = null,
                sql =
                    """
                    INSERT INTO link VALUES
                        (1, 1, 2, 3, 4, 5, 6, 7, 0),
                        (2, 1, 2, 3, 4, 5, 6, 7, 1),
                        (3, 1, 2, 3, 4, 5, 6, 7, 2)
                    """.trimIndent(),
                parameters = 0,
            )

            installOtzariaLinkSchemaCompatibility(driver)

            assertEquals(listOf(0L, 1L, 0L), queryLongs(driver, "SELECT isDeclaredBase FROM link ORDER BY id"))
            val mainColumns = queryStrings(driver, "PRAGMA main.table_info(link)", columnIndex = 1)
            assertTrue("baseProvenance" in mainColumns)
            assertFalse("isDeclaredBase" in mainColumns)
        } finally {
            driver.close()
        }
    }

    @Test
    fun leavesTheNativeZayitSchemaUnchanged() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            driver.execute(
                identifier = null,
                sql =
                    """
                    CREATE TABLE link (
                        id INTEGER NOT NULL PRIMARY KEY,
                        sourceBookId INTEGER NOT NULL,
                        targetBookId INTEGER NOT NULL,
                        sourceLineId INTEGER NOT NULL,
                        targetLineId INTEGER NOT NULL,
                        targetLineIndex INTEGER NOT NULL,
                        targetBookOrderIndex INTEGER NOT NULL,
                        connectionTypeId INTEGER NOT NULL,
                        isDeclaredBase INTEGER NOT NULL
                    )
                    """.trimIndent(),
                parameters = 0,
            )

            installOtzariaLinkSchemaCompatibility(driver)

            assertEquals(emptyList(), queryStrings(driver, "SELECT name FROM sqlite_temp_master WHERE type = 'view'"))
        } finally {
            driver.close()
        }
    }

    private fun queryLongs(
        driver: JdbcSqliteDriver,
        sql: String,
    ): List<Long> {
        val queryResult =
            driver.executeQuery(
                identifier = null,
                sql = sql,
                mapper = { cursor ->
                    val values = mutableListOf<Long>()
                    while (cursor.next().value) {
                        cursor.getLong(0)?.let(values::add)
                    }
                    QueryResult.Value(values)
                },
                parameters = 0,
            )
        return queryResult.value
    }

    private fun queryStrings(
        driver: JdbcSqliteDriver,
        sql: String,
        columnIndex: Int = 0,
    ): List<String> {
        val queryResult =
            driver.executeQuery(
                identifier = null,
                sql = sql,
                mapper = { cursor ->
                    val values = mutableListOf<String>()
                    while (cursor.next().value) {
                        cursor.getString(columnIndex)?.let(values::add)
                    }
                    QueryResult.Value(values)
                },
                parameters = 0,
            )
        return queryResult.value
    }
}
