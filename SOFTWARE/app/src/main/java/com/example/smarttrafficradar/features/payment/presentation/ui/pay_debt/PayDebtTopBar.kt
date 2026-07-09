package com.example.smarttrafficradar.features.payment.presentation.ui.pay_debt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun PayDebtTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier.height(Dimen.HeightML),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(Dimen.SizeM)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Text(
                text = stringResource(id = R.string.payment),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.Black
            )
        }
    }
}