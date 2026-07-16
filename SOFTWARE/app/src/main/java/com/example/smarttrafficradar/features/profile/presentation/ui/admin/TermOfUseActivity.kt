package com.example.smarttrafficradar.features.profile.presentation.ui.admin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermOfUseActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    TermOfUseScreen(onBackClick = { finish() })
                }
            }
        }
    }
}

@Composable
fun TermOfUseScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingXXL, bottom = Dimen.PaddingM, start = Dimen.PaddingM, end = Dimen.PaddingM)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = LightPrimary,
                modifier = Modifier
                    .size(Dimen.SizeL)
                    .clickable { onBackClick() }
                    .align(Alignment.CenterStart)
            )

            Text(
                text = stringResource(id = R.string.terms_of_use),
                color = LightPrimary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(thickness = Dimen.PaddingXXS, color = Color.LightGray.copy(alpha = 0.3f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.admin_terms_title),
                style = MaterialTheme.typography.titleMedium.semiBold(),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Text(
                text = stringResource(id = R.string.admin_terms_intro),
                style = MaterialTheme.typography.s14,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            TermSection(
                title = stringResource(id = R.string.admin_terms_section_1_title),
                content = stringResource(id = R.string.admin_terms_section_1_desc)
            )

            TermSection(
                title = stringResource(id = R.string.admin_terms_section_2_title),
                content = stringResource(id = R.string.admin_terms_section_2_desc)
            )

            TermSection(
                title = stringResource(id = R.string.admin_terms_section_3_title),
                content = stringResource(id = R.string.admin_terms_section_3_desc)
            )

            TermSection(
                title = stringResource(id = R.string.admin_terms_section_4_title),
                content = stringResource(id = R.string.admin_terms_section_4_desc)
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.XXL))
        }
    }
}

@Composable
fun TermSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = AppSpacing.L)) {
        Text(
            text = title,
            style = MaterialTheme.typography.s16.semiBold(),
            color = LightPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.XS))
        Text(
            text = content,
            style = MaterialTheme.typography.s14,
            color = Color.Black
        )
    }
}
