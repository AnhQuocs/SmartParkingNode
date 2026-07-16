package com.example.smarttrafficradar.features.profile.presentation.ui.user

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.utils.s16
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportCenterActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SupportCenterScreen(onBackClick = { finish() })
                }
            }
        }
    }
}

@Composable
fun SupportCenterScreen(onBackClick: () -> Unit) {
    var message by remember { mutableStateOf("") }
    var includeDeviceInfo by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingXL)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                colorFilter = ColorFilter.tint(LightPrimary),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(top = Dimen.PaddingL, start = Dimen.PaddingSM)
                    .size(Dimen.SizeL)
                    .clickable { onBackClick() }
            )

            Text(
                stringResource(id = R.string.support_center),
                color = LightPrimary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = Dimen.PaddingL)
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.XL))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text(stringResource(id = R.string.message_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(AppShape.ShapeM),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LightPrimary)
        )

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { includeDeviceInfo = !includeDeviceInfo },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = includeDeviceInfo,
                onCheckedChange = { includeDeviceInfo = it },
                colors = CheckboxDefaults.colors(checkedColor = LightPrimary)
            )
            Text(
                text = stringResource(id = R.string.include_device_info),
                style = MaterialTheme.typography.s16,
                color = Color.Black
            )
        }

        if (includeDeviceInfo) {
            val deviceInfo = getDeviceInfo()
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(AppShape.ShapeS))
                    .padding(Dimen.PaddingS)
            ) {
                Text(
                    text = deviceInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.XXL))

        Button(
            onClick = {
                val finalBody = if (includeDeviceInfo) {
                    "$message\n\n--- ${context.getString(R.string.device_info_label)} ---\n${getDeviceInfo()}"
                } else {
                    message
                }

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("anhquocb435@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "[Smart Parking Support] Feedback")
                    putExtra(Intent.EXTRA_TEXT, finalBody)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    // Fallback to generic chooser if ACTION_SENDTO fails
                    val chooserIntent = Intent.createChooser(intent, "Send Email")
                    context.startActivity(chooserIntent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightDefault),
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
        ) {
            Text(stringResource(id = R.string.send_message), color = Color.White)
        }
    }
}

fun getDeviceInfo(): String {
    return """
        Model: ${Build.MODEL}
        Manufacturer: ${Build.MANUFACTURER}
        Android Version: ${Build.VERSION.RELEASE}
        SDK: ${Build.VERSION.SDK_INT}
        Brand: ${Build.BRAND}
        Device: ${Build.DEVICE}
    """.trimIndent()
}
