package com.example.smarttrafficradar.utils

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Các Extension Properties để gọi nhanh theo Size
 */
val androidx.compose.material3.Typography.s12: TextStyle get() = labelLarge.copy(fontSize = 12.sp, lineHeight = 16.sp)
val androidx.compose.material3.Typography.s13: TextStyle get() = bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp)
val androidx.compose.material3.Typography.s14: TextStyle get() = bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp)
val androidx.compose.material3.Typography.s15: TextStyle get() = bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp)
val androidx.compose.material3.Typography.s16: TextStyle get() = titleMedium.copy(fontSize = 16.sp, lineHeight = 24.sp)
val androidx.compose.material3.Typography.s18: TextStyle get() = titleLarge.copy(fontSize = 18.sp, lineHeight = 26.sp)
val androidx.compose.material3.Typography.s20: TextStyle get() = headlineMedium.copy(fontSize = 20.sp, lineHeight = 28.sp)
val androidx.compose.material3.Typography.s22: TextStyle get() = headlineLarge.copy(fontSize = 22.sp, lineHeight = 30.sp)
val androidx.compose.material3.Typography.s24: TextStyle get() = displaySmall.copy(fontSize = 24.sp, lineHeight = 32.sp)
val androidx.compose.material3.Typography.s28: TextStyle get() = displayMedium.copy(fontSize = 28.sp, lineHeight = 36.sp)
val Typography.s32: TextStyle get() = displayLarge.copy(fontSize = 32.sp, lineHeight = 40.sp)

/**
 * Các hàm Extension để ghi đè (Override) thuộc tính
 */

// --- Font Weight ---
fun TextStyle.bold() = this.copy(fontWeight = FontWeight.Bold)
fun TextStyle.semiBold() = this.copy(fontWeight = FontWeight.SemiBold)
fun TextStyle.medium() = this.copy(fontWeight = FontWeight.Medium)
fun TextStyle.normal() = this.copy(fontWeight = FontWeight.Normal)
fun TextStyle.light() = this.copy(fontWeight = FontWeight.Light)

// --- Color ---
fun TextStyle.withColor(color: Color) = this.copy(color = color)

// --- Size & Height ---
fun TextStyle.withSize(size: TextUnit) = this.copy(fontSize = size)
fun TextStyle.withLineHeight(height: TextUnit) = this.copy(lineHeight = height)

// --- Style & Decoration ---
fun TextStyle.italic() = this.copy(fontStyle = FontStyle.Italic)
fun TextStyle.underline() = this.copy(textDecoration = TextDecoration.Underline)
fun TextStyle.lineThrough() = this.copy(textDecoration = TextDecoration.LineThrough)

// --- Spacing ---
fun TextStyle.spacing(spacing: TextUnit) = this.copy(letterSpacing = spacing)