package io.github.kdroidfilter.seforimapp.pagination

import io.github.kdroidfilter.seforimlibrary.core.models.ConnectionType

/**
 * Connection types rendered in the commentaries pane.
 *
 * Otzaria classifies dependent texts more precisely than COMMENTARY alone.
 * Keep targum separate because Zayit renders it in its own pane.
 */
val COMMENTARY_CONNECTION_TYPES: Set<ConnectionType> =
    setOf(
        ConnectionType.COMMENTARY,
        ConnectionType.SUPER_COMMENTARY,
        ConnectionType.MIDRASH,
        ConnectionType.PARSHANUT,
        ConnectionType.DIBUR_HAMATCHIL,
    )
