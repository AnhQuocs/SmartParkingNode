package com.example.smarttrafficradar.features.dashboard.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ErrorRed
import java.text.DecimalFormat

@Composable
fun RadarView(currentSpeed: Double, isViolation: Boolean) {
    // Lấy màu chủ đạo (Primary) và màu nền (Background) từ Theme hiện tại
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    // Tạo Animation xoay liên tục từ 0 -> 360 độ
    val infiniteTransition = rememberInfiniteTransition(label = "radar_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    // Tạo formatter: tối đa 1 chữ số thập phân, nếu là .0 thì biến mất
    val df = DecimalFormat("#.#")
    val speedText = df.format(currentSpeed) // 28.5 -> "28.5", 30.0 -> "30"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(backgroundColor), // Sử dụng màu nền của Theme
        contentAlignment = Alignment.Center
    ) {
        // Vẽ giao diện Radar bằng Canvas
        Canvas(
            modifier = Modifier
                .size(320.dp)
                .padding(16.dp)
        ) {
            val radius = size.minDimension / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Bước A: Vẽ 4 vòng tròn đồng tâm (Lưới Radar)
            for (i in 1..4) {
                drawCircle(
                    color = primaryColor.copy(alpha = 0.2f),
                    radius = radius * (i / 4f),
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Bước B: Vẽ tia trục chéo (Crosshairs)
            drawLine(
                color = primaryColor.copy(alpha = 0.1f),
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = primaryColor.copy(alpha = 0.1f),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 1.dp.toPx()
            )

            // Bước C: Vẽ tia quét và hiệu ứng đuôi mờ
            rotate(degrees = angle, pivot = center) {
                // Đuôi mờ (Comet tail)
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.8f to primaryColor.copy(alpha = 0.1f),
                        1.0f to primaryColor.copy(alpha = 0.5f),
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Đường kẻ kim (Tia quét chính)
                drawLine(
                    color = primaryColor,
                    start = center,
                    end = Offset(center.x + radius, center.y),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Đặt Text tốc độ đè lên trên chính giữa
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_radar_speed),
                contentDescription = null,
                tint = if(isViolation) ErrorRed else primaryColor,
                modifier = Modifier.size(Dimen.SizeL)
            )

            Text(
                text = speedText,
                color = if(isViolation) ErrorRed else primaryColor,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = if(isViolation) ErrorRed.copy(alpha = 0.8f) else primaryColor.copy(alpha = 0.8f),
                        blurRadius = 20f
                    )
                )
            )

            Text(
                text = "km/h",
                color = onBackgroundColor.copy(alpha = 0.6f), // Chữ phụ mờ đi một chút so với màu text chính
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )
        }
    }
}