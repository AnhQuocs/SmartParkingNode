package com.example.smarttrafficradar.features.violation.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.semiBold

enum class ViolationInfoType {
    SPEED, LIMIT, EXCESS
}

@Composable
fun ViolationCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1117)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingSM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.height(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeL))
                            .border(
                                1.dp,
                                color = Color(0xFFD97706),
                                shape = RoundedCornerShape(AppShape.ShapeL)
                            )
                            .background(color = Color(0xFFD97706).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning),
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(Dimen.SizeL)
                        )
                    }

                    Spacer(modifier = Modifier.width(AppSpacing.M))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = "CAR_49",
                            style = MaterialTheme.typography.s16.bold(),
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clock),
                                contentDescription = null,
                                tint = SlateMist,
                                modifier = Modifier.size(Dimen.SizeSM)
                            )

                            Spacer(modifier = Modifier.width(AppSpacing.S))

                            Text(
                                text = "25/04/2026 - 10:18:43", color = SlateMist
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(AppShape.ShapeM))
                            .background(color = Color(0xFF24171E))
                            .border(
                                1.dp,
                                color = Color(0xFF492431),
                                shape = RoundedCornerShape(AppShape.ShapeM)
                            ), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CRITICAL",
                            style = MaterialTheme.typography.s14.semiBold(),
                            color = Color(0xFFFF3E5D),
                            modifier = Modifier.padding(horizontal = Dimen.PaddingS)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ViolationInfoCard(
                    title = "Recorded Speed", value = 74, type = ViolationInfoType.SPEED
                )

                ViolationInfoCard(
                    title = "Speed Limit", value = 60, type = ViolationInfoType.LIMIT
                )

                ViolationInfoCard(
                    title = "Excess", value = 14, type = ViolationInfoType.EXCESS
                )
            }
        }
    }
}

data class ViolationInfoColor(
    val contentClr: Color, val borderClr: Color, val backgroundClr: Color
)

@Composable
fun ViolationInfoCard(
    title: String, value: Int, type: ViolationInfoType, modifier: Modifier = Modifier
) {
    val color = when (type) {
        ViolationInfoType.SPEED -> ViolationInfoColor(
            contentClr = Color(0xFFFF3E5D),
            borderClr = Color(0xFF492431),
            backgroundClr = Color(0xFF24171E)
        )

        ViolationInfoType.LIMIT -> ViolationInfoColor(
            contentClr = Color.White,
            borderClr = Color(0xFF2E3544),
            backgroundClr = Color(0xFF1A1F29)
        )

        ViolationInfoType.EXCESS -> ViolationInfoColor(
            contentClr = Color(0xFFFFB200),
            borderClr = Color(0xFF42341C),
            backgroundClr = Color(0xFF24201A)
        )
    }

    Box(
        modifier = modifier
            .height(Dimen.HeightXL)
            .width(105.dp)
            .clip(RoundedCornerShape(AppShape.ShapeL))
            .background(color = color.backgroundClr)
            .border(1.dp, color = color.borderClr, RoundedCornerShape(AppShape.ShapeL)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingSM),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title, style = MaterialTheme.typography.s14, color = Color(0xFF8690A0)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (type == ViolationInfoType.EXCESS) "+$value" else value.toString(),
                    style = MaterialTheme.typography.s24.bold(),
                    color = color.contentClr
                )

                Spacer(modifier = Modifier.width(AppSpacing.XXS))

                Text(
                    text = "km/h",
                    style = MaterialTheme.typography.s14.semiBold(),
                    color = Color(0xFF8690A0),
                )
            }
        }
    }
}