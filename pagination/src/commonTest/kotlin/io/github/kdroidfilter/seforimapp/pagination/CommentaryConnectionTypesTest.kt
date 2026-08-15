package io.github.kdroidfilter.seforimapp.pagination

import io.github.kdroidfilter.seforimlibrary.core.models.ConnectionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CommentaryConnectionTypesTest {
    @Test
    fun includesDependentTextTypesUsedByOtzaria() {
        assertEquals(
            setOf(
                ConnectionType.COMMENTARY,
                ConnectionType.SUPER_COMMENTARY,
                ConnectionType.MIDRASH,
                ConnectionType.PARSHANUT,
                ConnectionType.DIBUR_HAMATCHIL,
            ),
            COMMENTARY_CONNECTION_TYPES,
        )
        assertFalse(ConnectionType.TARGUM in COMMENTARY_CONNECTION_TYPES)
    }
}
