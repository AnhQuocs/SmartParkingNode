package com.example.smarttrafficradar.features.system_monitor.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun NetworkSetupTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(Dimen.SizeML)
                    .clickable { onBackClick() }
            )

            Text(
                stringResource(R.string.setup_network),
                color = Color.White,
                style = MaterialTheme.typography.s20.semiBold()
            )

            Spacer(modifier = Modifier.size(Dimen.SizeM))
        }
    }
}