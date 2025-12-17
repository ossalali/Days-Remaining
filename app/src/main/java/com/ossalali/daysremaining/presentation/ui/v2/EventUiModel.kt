package com.ossalali.daysremaining.presentation.ui.v2

import com.ossalali.daysremaining.model.EventItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

data class EventUiModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val imageUri: String? = null,
    val isArchived: Boolean = false,
)

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
