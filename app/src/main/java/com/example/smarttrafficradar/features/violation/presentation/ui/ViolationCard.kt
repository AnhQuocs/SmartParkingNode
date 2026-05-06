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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.presentation.util.getViolationLevel
import com.example.smarttrafficradar.features.violation.presentation.util.toFormattedDateTime
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AmberDark
import com.example.smarttrafficradar.ui.theme.AmberPrimary
import com.example.smarttrafficradar.ui.theme.NeutralBackground
import com.example.smarttrafficradar.ui.theme.NeutralBorder
import com.example.smarttrafficradar.ui.theme.NeutralPrimary
import com.example.smarttrafficradar.ui.theme.Orange644919
import com.example.smarttrafficradar.ui.theme.OrangeBackground
import com.example.smarttrafficradar.ui.theme.OrangePrimary
import com.example.smarttrafficradar.ui.theme.RedBackground
import com.example.smarttrafficradar.ui.theme.RedBorder
import com.example.smarttrafficradar.ui.theme.RedPrimary
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.ui.theme.SurfaceDark
import com.example.smarttrafficradar.ui.theme.TextSecondary
import com.example.smarttrafficradar.ui.theme.YellowBackground
import com.example.smarttrafficradar.ui.theme.YellowBorder
import com.example.smarttrafficradar.ui.theme.YellowPrimary
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.semiBold

enum class ViolationInfoType {
    SPEED, LIMIT, EXCESS
}

enum class ViolationLevel {
    CRITICAL, HIGH, MODERATE
}

data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
fun ViolationCard(
    modifier: Modifier = Modifier,
    violation: Violation
) {
    Card(
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
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
                                color = AmberDark,
                                shape = RoundedCornerShape(AppShape.ShapeL)
                            )
                            .background(color = AmberDark.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning),
                            contentDescription = null,
                            tint = AmberPrimary,
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
                            text = violation.vehicleId,
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
                                text = violation.timestamp.toFormattedDateTime(), color = SlateMist
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    val level = getViolationLevel(violation.speedKmh, 60)

                    val (textRes, contentColor, borderColor, backgroundColor) = when (level) {
                        ViolationLevel.CRITICAL -> Quad(
                            R.string.violation_level_critical,
                            RedPrimary,
                            RedBorder,
                            RedBackground
                        )

                        ViolationLevel.HIGH -> Quad(
                            R.string.violation_level_high,
                            OrangePrimary,
                            Orange644919,
                            OrangeBackground
                        )

                        ViolationLevel.MODERATE -> Quad(
                            R.string.violation_level_moderate,
                            YellowPrimary,
                            YellowBorder,
                            YellowBackground
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(AppShape.ShapeM))
                            .background(color = backgroundColor)
                            .border(
                                1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(AppShape.ShapeM)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = textRes),
                            style = MaterialTheme.typography.s14.semiBold(),
                            color = contentColor,
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
                    title = stringResource(id = R.string.violation_recorded_speed),
                    value = violation.speedKmh.toInt(),
                    type = ViolationInfoType.SPEED
                )

                ViolationInfoCard(
                    title = stringResource(id = R.string.violation_speed_limit),
                    value = 60,
                    type = ViolationInfoType.LIMIT
                )

                ViolationInfoCard(
                    title = stringResource(id = R.string.violation_excess),
                    value = violation.speedKmh.toInt() - 60,
                    type = ViolationInfoType.EXCESS
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
            contentClr = RedPrimary,
            borderClr = RedBorder,
            backgroundClr = RedBackground
        )

        ViolationInfoType.LIMIT -> ViolationInfoColor(
            contentClr = NeutralPrimary,
            borderClr = NeutralBorder,
            backgroundClr = NeutralBackground
        )

        ViolationInfoType.EXCESS -> ViolationInfoColor(
            contentClr = YellowPrimary,
            borderClr = YellowBorder,
            backgroundClr = YellowBackground
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
                text = title, style = MaterialTheme.typography.s14, color = TextSecondary
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
                    color = TextSecondary,
                )
            }
        }
    }
}