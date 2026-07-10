package com.example.smarttrafficradar.features.user_profile.presentation.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditProfileScreen(onBackClick = { finish() })
                }
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authUser by authViewModel.currentUser.collectAsState()
    val profileState by userProfileViewModel.profileState.collectAsState()
    
    var phoneNumber by remember { mutableStateOf("") }
    var currentProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(authUser) {
        authUser?.uid?.let { uid ->
            userProfileViewModel.loadUserProfile(uid)
        }
    }

    LaunchedEffect(profileState) {
        when (profileState) {
            is UserProfileState.Success -> {
                val profile = (profileState as UserProfileState.Success).profile
                // Nếu đang trong quá trình lưu và thành công thì mới đóng màn hình
                if (isSaving) {
                    Toast.makeText(context, context.getString(R.string.apply), Toast.LENGTH_SHORT).show()
                    onBackClick()
                } else {
                    currentProfile = profile
                    phoneNumber = profile.phoneNumber
                }
            }
            is UserProfileState.Error -> {
                isSaving = false
                Toast.makeText(context, (profileState as UserProfileState.Error).uiText.asString(context), Toast.LENGTH_SHORT).show()
            }
            is UserProfileState.Loading -> {
                // Đang xử lý
            }
            else -> {}
        }
    }

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
                stringResource(id = R.string.edit_profile),
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

        currentProfile?.let { profile ->
            ReadOnlyProfileField(label = stringResource(id = R.string.full_name_label), value = profile.fullName)
            Spacer(modifier = Modifier.height(AppSpacing.M))
            
            ReadOnlyProfileField(label = stringResource(id = R.string.email_address_label), value = profile.email)
            Spacer(modifier = Modifier.height(AppSpacing.M))

            ReadOnlyProfileField(
                label = if (profile.memberType == com.example.smarttrafficradar.features.user_profile.domain.model.MemberType.STUDENT) 
                    stringResource(id = R.string.student_id_label) else stringResource(id = R.string.employee_id_label),
                value = profile.identifier
            )
            Spacer(modifier = Modifier.height(AppSpacing.M))

            ReadOnlyProfileField(label = stringResource(id = R.string.department_faculty_label), value = profile.department)
            Spacer(modifier = Modifier.height(AppSpacing.M))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(stringResource(id = R.string.phone_number_label)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightPrimary,
                    unfocusedBorderColor = Color.Gray
                ),
                enabled = profileState !is UserProfileState.Loading
            )

            Spacer(modifier = Modifier.height(AppSpacing.XXL))

            Button(
                onClick = {
                    if (phoneNumber.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.phone_required), Toast.LENGTH_SHORT).show()
                    } else {
                        isSaving = true
                        userProfileViewModel.saveProfile(profile.copy(phoneNumber = phoneNumber))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightDefault),
                shape = RoundedCornerShape(AppShape.ShapeL),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                enabled = profileState !is UserProfileState.Loading
            ) {
                if (profileState is UserProfileState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(id = R.string.save_changes), color = Color.White)
                }
            }
        } ?: run {
            CircularProgressIndicator(color = LightPrimary)
        }
    }
}

@Composable
fun ReadOnlyProfileField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = false,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledBorderColor = Color.LightGray,
            disabledLabelColor = Color.Gray,
            disabledContainerColor = Color(0xFFF5F5F5)
        )
    )
}
