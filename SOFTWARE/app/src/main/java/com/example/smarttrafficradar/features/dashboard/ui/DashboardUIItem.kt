package com.example.smarttrafficradar.features.dashboard.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppHeader
import com.example.smarttrafficradar.components.AppSubText
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ErrorRed
import com.example.smarttrafficradar.ui.theme.NeonGreen
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20

@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            AppHeader(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.s20.bold()
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            AppSubText(
                text = stringResource(id = R.string.real_time_monitoring_active),
                style = MaterialTheme.typography.s16
            )
        }

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .height(Dimen.HeightSmall)
                .clip(RoundedCornerShape(AppShape.ShapeXL))
                .border(
                    0.5.dp, color = NeonGreen, shape = RoundedCornerShape(AppShape.ShapeXL)
                )
                .background(color = NeonGreen.copy(alpha = 0.1f))
                .padding(Dimen.PaddingXSPlus),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = Dimen.PaddingXSPlus)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dot),
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .blur(radius = 6.dp)
                            .alpha(0.6f)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_dot),
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(AppSpacing.S))

                AppSubText(
                    text = "LIVE", style = MaterialTheme.typography.s16.copy(
                        shadow = Shadow(
                            color = NeonGreen.copy(alpha = 0.6f), blurRadius = 20f
                        )
                    ), color = NeonGreen
                )
            }
        }
    }
}

@Composable
fun SpeedAnalysisChart(
    speedEntries: List<Pair<Double, Boolean>>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorRed = ErrorRed

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(speedEntries) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.speed_analysis_recent_10),
            color = primaryColor.copy(alpha = 0.7f),
            style = MaterialTheme.typography.s12.bold(),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightXL3)
        ) {
            if (speedEntries.isEmpty()) return@Canvas

            val spacing = size.width / (speedEntries.size - 1).coerceAtLeast(1)
            // Lấy max speed để scale tọa độ Y
            val maxSpeed =
                (speedEntries.maxOfOrNull { it.first } ?: 0.0).coerceAtLeast(100.0).toFloat()

            val points = speedEntries.mapIndexed { index, entry ->
                Offset(
                    x = index * spacing,
                    y = size.height - (entry.first.toFloat() / maxSpeed * size.height)
                )
            }

            // 1. Tạo Path cho đường kẻ (Line)
            val strokePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val progressThreshold = i.toFloat() / (points.size - 1)
                        if (animationProgress.value >= progressThreshold) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                }
            }

            // 2. Vẽ vùng Gradient phía dưới
            val fillPath = android.graphics.Path().apply {
                addPath(strokePath.asAndroidPath())
                val lastX = animationProgress.value * size.width
                lineTo(lastX, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawContext.canvas.nativeCanvas.drawPath(
                fillPath, Paint().apply {
                    shader = LinearGradientShader(
                        from = Offset(0f, 0f),
                        to = Offset(0f, size.height),
                        colors = listOf(primaryColor.copy(alpha = 0.2f), Color.Transparent)
                    )
                }.asFrameworkPaint()
            )

            // 3. Vẽ đường Line chính (Giữ màu Primary để tạo sự nhất quán)
            drawPath(
                path = strokePath,
                color = primaryColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Vẽ các điểm nút (Dots) - ĐÂY LÀ PHẦN THAY ĐỔI CHÍNH
            points.forEachIndexed { index, point ->
                val progressThreshold = index.toFloat() / (points.size - 1)

                if (animationProgress.value >= progressThreshold) {
                    // Kiểm tra xem tại vị trí này có vi phạm không
                    val isViolation = speedEntries[index].second
                    val pointColor = if (isViolation) errorRed else primaryColor

                    // Vẽ quầng sáng (Glow effect) cho điểm nút
                    drawCircle(
                        color = pointColor.copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = point
                    )

                    // Vẽ điểm nút chính
                    drawCircle(
                        color = pointColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )

                    // Nếu là vi phạm, vẽ thêm một vòng line nhỏ bao quanh để highlight mạnh hơn
                    if (isViolation) {
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }
    }
}