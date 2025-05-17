package utilities.datetime

import kotlinx.datetime.*

fun localDateTimeNow(): LocalDateTime = Clock.System.now().toLocalDateTime(
    TimeZone.currentSystemDefault(),
)

fun instantNow(): Instant = Clock.System.now()
