package org.elnix.notes.data.helpers

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Title
import org.elnix.notes.R

enum class SortType {
    DATE, TITLE, COMPLETED, CUSTOM
}

enum class SortMode {
    ASC, DESC
}

fun sortTypeIcon(type: SortType) = when (type) {
    SortType.DATE -> Icons.Default.DateRange
    SortType.TITLE -> Icons.Default.Title
    SortType.COMPLETED -> Icons.Default.CheckBox
    SortType.CUSTOM -> Icons.Default.DashboardCustomize
}


fun sortTypeName(ctx: Context, type: SortType) = when (type) {
    SortType.DATE -> ctx.getString(R.string.date)
    SortType.TITLE -> ctx.getString(R.string.title)
    SortType.COMPLETED -> ctx.getString(R.string.completed)
    SortType.CUSTOM -> ctx.getString(R.string.custom_sort_order_text)
}

fun sortModeName(ctx: Context, mode: SortMode) = when (mode) {
    SortMode.ASC -> ctx.getString(R.string.asc)
    SortMode.DESC -> ctx.getString(R.string.desc)
}

fun sortModeIcon( mode: SortMode) = when (mode) {
    SortMode.ASC -> Icons.Default.ArrowDropDown
    SortMode.DESC -> Icons.Default.ArrowDropUp
}