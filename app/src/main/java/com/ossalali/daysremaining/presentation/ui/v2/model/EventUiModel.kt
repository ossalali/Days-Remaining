package com.ossalali.daysremaining.presentation.ui.v2.model

import com.ossalali.daysremaining.model.EventItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Serializable
data class EventUiModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = LocalDate.now().toString(),
    val imageUri: String? = null,
    val isArchived: Boolean = false,
)

fun String.toNumberOfDays(): String {
    return if (this.isNotBlank()) {
        LocalDate.now().until(LocalDate.parse(this), ChronoUnit.DAYS)
    } else {
        0
    }
        .toString()
}

fun EventUiModel.toEvent(): EventItem {
    return EventItem(
        id = this.id,
        title = this.title,
        date = LocalDate.parse(this.date),
        description = this.description,
        imageUri = this.imageUri,
        isArchived = this.isArchived,
    )
}

fun EventItem.toEventUiModel(): EventUiModel {
    return EventUiModel(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date.toString(),
        imageUri = this.imageUri,
        isArchived = this.isArchived,
    )
}

fun ImmutableList<EventItem>.toEventUiModels(): ImmutableList<EventUiModel> {
    return this.map { it.toEventUiModel() }.toPersistentList()
}
