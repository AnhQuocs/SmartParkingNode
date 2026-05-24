package com.example.smarttrafficradar.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    // Dùng cho chuỗi cứng (ít dùng cho lỗi hệ thống)
    data class DynamicString(val value: String) : UiText()

    // Dùng cho ID từ strings.xml (Chuẩn để đa ngôn ngữ)
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}