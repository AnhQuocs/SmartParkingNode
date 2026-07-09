package com.example.smarttrafficradar.features.payment.presentation.ui.pay_debt

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.payment.presentation.ui.PaymentWebViewActivity
import com.example.smarttrafficradar.features.payment.presentation.util.getInitials
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentState
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentViewModel
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.BabyBlue
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PayDebtState(
    profileState: UserProfileState,
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current
    val paymentState by paymentViewModel.paymentState.collectAsState()

    var isParkingServiceSelected by remember { mutableStateOf(false) }
    var isSelectAll by remember { mutableStateOf(false) }

    val currentDebt = (profileState as? UserProfileState.Success)?.profile?.currentDebt ?: 0

    LaunchedEffect(isSelectAll) {
        if (isSelectAll) {
            isParkingServiceSelected = true
        }
    }

    LaunchedEffect(isParkingServiceSelected) {
        if (!isParkingServiceSelected) {
            isSelectAll = false
        }
    }

    LaunchedEffect(paymentState) {
        if (paymentState is PaymentState.Success) {
            val payUrl = (paymentState as PaymentState.Success).payUrl
            val intent = Intent(context, PaymentWebViewActivity::class.java).apply {
                putExtra("payUrl", payUrl)
            }
            context.startActivity(intent)
            paymentViewModel.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (profileState) {
            is UserProfileState.Idle, UserProfileState.Loading -> {
                CircularProgressIndicator(
                    color = LightPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UserProfileState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimen.PaddingM)
                ) {
                    val profile = profileState.profile

                    PayerInfo(
                        fullName = profile.fullName,
                        identifier = profile.identifier
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isSelectAll,
                            onCheckedChange = { isSelectAll = it },
                            colors = CheckboxDefaults.colors(checkedColor = LightPrimary)
                        )
                        Text(
                            text = stringResource(id = R.string.select_all),
                            style = MaterialTheme.typography.s16,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    ParkingService(
                        totalDebt = profile.currentDebt,
                        isSelected = isParkingServiceSelected,
                        onSelectChanged = { isParkingServiceSelected = it }
                    )
                }
            }

            is UserProfileState.Error -> {
                Text(
                    text = profileState.uiText.asString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Dimen.PaddingM)
                .fillMaxWidth()
        ) {
            if (paymentState is PaymentState.Error) {
                Text(
                    text = (paymentState as PaymentState.Error).message,
                    color = Color.Red,
                    style = MaterialTheme.typography.s14,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val isEnabled = (isParkingServiceSelected || isSelectAll) && currentDebt > 0 && paymentState !is PaymentState.Loading

            Button(
                onClick = {
                    if (profileState is UserProfileState.Success) {
                        paymentViewModel.createPaymentUrl(
                            profileState.profile.uid,
                            profileState.profile.currentDebt
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightLarge),
                enabled = isEnabled,
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEnabled) LightPrimary else Color(0xFFD1D5DB),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFD1D5DB),
                    disabledContentColor = Color(0xFF6B7280)
                )
            ) {
                if (paymentState is PaymentState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(Dimen.SizeM),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.payment),
                        style = MaterialTheme.typography.s18.semiBold()
                    )
                }
            }
        }
    }
}

@Composable
fun PayerInfo(
    fullName: String,
    identifier: String
) {
    val initials = getInitials(fullName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(color = Color.White)
                .border(3.dp, color = BabyBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.s16,
                color = LightPrimary
            )
        }

        Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = fullName,
                style = MaterialTheme.typography.s16,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment),
                    contentDescription = null,
                    tint = RoyalBlue,
                    modifier = Modifier.size(Dimen.SizeSM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.XSPlus))

                Text(
                    text = identifier,
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ParkingService(
    totalDebt: Int,
    isSelected: Boolean,
    onSelectChanged: (Boolean) -> Unit
) {
    val formattedDebt = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(totalDebt)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(AppShape.ShapeL))
            .background(Color.White, RoundedCornerShape(AppShape.ShapeL))
            .padding(Dimen.PaddingM)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectChanged,
                colors = CheckboxDefaults.colors(checkedColor = LightPrimary)
            )

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.parking_service),
                    style = MaterialTheme.typography.s16.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = stringResource(id = R.string.outstanding_balance),
                    style = MaterialTheme.typography.s14,
                    color = Color(0xFF6B7280)
                )
            }

            Text(
                text = formattedDebt.replace("₫", "đ"),
                style = MaterialTheme.typography.s16.semiBold(),
                color = RoyalBlue
            )
        }
    }
}
