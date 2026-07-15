package com.example.smarttrafficradar.features.management.presentation.ui.register_card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.LightSecondary
import com.example.smarttrafficradar.ui.theme.TextSecondary
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16

@Composable
fun FeeInformationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
            .padding(Dimen.PaddingM)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = LightPrimary,
                modifier = Modifier.padding(end = Dimen.PaddingS)
            )
            Text(
                text = stringResource(R.string.parking_fee_info),
                style = MaterialTheme.typography.s16,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        FeeTable()
    }
}

@Composable
private fun FeeTable() {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.S)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth().padding(start = Dimen.PaddingSM)) {
            Spacer(modifier = Modifier.width(80.dp))
            Text(
                text = stringResource(R.string.motorcycle),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.s14,
                fontWeight = FontWeight.SemiBold,
                color = LightSecondary
            )
            Text(
                text = stringResource(R.string.car),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.s14,
                fontWeight = FontWeight.SemiBold,
                color = LightSecondary
            )
        }

        // Under 30 mins
        FeeRow(
            label = stringResource(R.string.under_30_mins),
            motorbikeFee = stringResource(R.string.free),
            carFee = stringResource(R.string.free)
        )

        // From 30 mins
        FeeRow(
            label = stringResource(R.string.from_30_mins),
            motorbikeFee = stringResource(R.string.price_per_turn, "5k"),
            carFee = stringResource(R.string.price_per_turn, "20k")
        )

        // Overnight
        FeeRow(
            label = stringResource(R.string.overnight_fee),
            motorbikeFee = stringResource(R.string.price_per_night, "15k"),
            carFee = stringResource(R.string.price_per_night, "50k")
        )
    }
}

@Composable
private fun FeeRow(label: String, motorbikeFee: String, carFee: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.s12,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.width(AppSpacing.M))

        Text(
            text = motorbikeFee,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.s14,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = carFee,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.s14,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
