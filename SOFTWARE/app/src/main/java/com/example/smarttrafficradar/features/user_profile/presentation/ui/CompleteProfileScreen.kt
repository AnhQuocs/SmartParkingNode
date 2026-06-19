package com.example.smarttrafficradar.features.user_profile.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppButton
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthState
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.presentation.util.CompleteProfileValidator
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightBackground
import com.example.smarttrafficradar.ui.theme.LightError
import com.example.smarttrafficradar.ui.theme.LightOnBackground
import com.example.smarttrafficradar.ui.theme.LightOnPrimary
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CompleteProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val identifierError by viewModel.identifierError.collectAsStateWithLifecycle()
    val memberInfo by viewModel.memberInfo.collectAsStateWithLifecycle()

    var step by remember { mutableIntStateOf(1) }
    var selectedType by remember { mutableStateOf(MemberType.STUDENT) }

    var identifier by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    LaunchedEffect(profileState) {
        if (profileState is UserProfileState.Success) {
            navController.navigate("user_root") {
                popUpTo("profile_completion_root") { inclusive = true }
            }
        } else if (profileState is UserProfileState.Error) {
            val message = (profileState as UserProfileState.Error).uiText.asString(context)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.SignedOut) {
            navController.navigate("auth") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    var phoneNumberTouched by remember { mutableStateOf(false) }
    val showPhoneNumberError =
        phoneNumberTouched && !CompleteProfileValidator.validatePhoneNumber(phoneNumber)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        IconButton(
            onClick = { authViewModel.signOut() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = Dimen.PaddingM, end = Dimen.PaddingS)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Logout",
                tint = LightError
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingL)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (step == 1) {
                Spacer(modifier = Modifier.height(AppSpacing.XXL))
                Text(
                    text = stringResource(R.string.are_you_a),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightOnBackground
                )
                Spacer(modifier = Modifier.height(AppSpacing.XL))

                RoleSelectionItem(
                    title = stringResource(R.string.student),
                    description = stringResource(R.string.student_desc),
                    icon = Icons.Default.Badge,
                    isSelected = selectedType == MemberType.STUDENT,
                    onClick = {
                        selectedType = MemberType.STUDENT
                        step = 2
                    }
                )
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
                RoleSelectionItem(
                    title = stringResource(R.string.employee),
                    description = stringResource(R.string.employee_desc),
                    icon = Icons.Default.Business,
                    isSelected = selectedType == MemberType.EMPLOYEE,
                    onClick = {
                        selectedType = MemberType.EMPLOYEE
                        step = 2
                    }
                )
            } else {
                Text(
                    text = stringResource(R.string.complete_your_profile),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightOnBackground
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))
                Text(
                    text = if (selectedType == MemberType.STUDENT) stringResource(R.string.student_account) else stringResource(
                        R.string.employee_account
                    ),
                    color = LightPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(AppSpacing.XL))

                ProfileOutlinedTextField(
                    value = currentUser?.email ?: "",
                    onValueChange = {},
                    label = stringResource(R.string.email_address_label),
                    leadingIcon = Icons.Default.Email,
                    readOnly = true,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                ProfileOutlinedTextField(
                    value = identifier,
                    onValueChange = {
                        identifier = it
                        if (it.length >= 3) {
                            viewModel.validateAndFetchMember(it, currentUser?.email ?: "")
                        }
                    },
                    label = if (selectedType == MemberType.STUDENT) stringResource(R.string.student_id_label) else stringResource(
                        R.string.employee_id_label
                    ),
                    leadingIcon = Icons.Default.Badge,
                    isError = identifierError != null,
                    supportingText = {
                        identifierError?.let {
                            Text(
                                it.asString(),
                                color = LightError
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                ProfileOutlinedTextField(
                    value = memberInfo?.fullName ?: "",
                    onValueChange = {},
                    label = stringResource(R.string.full_name_label),
                    leadingIcon = Icons.Default.Person,
                    placeholder = stringResource(R.string.validated_from_organization),
                    readOnly = true,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                ProfileOutlinedTextField(
                    value = memberInfo?.department ?: "",
                    onValueChange = {},
                    label = stringResource(R.string.department_faculty_label),
                    leadingIcon = Icons.Default.Business,
                    placeholder = stringResource(R.string.validated_from_organization),
                    readOnly = true,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                ProfileOutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneNumberTouched = true
                    },
                    label = stringResource(R.string.phone_number_label),
                    placeholder = stringResource(id = R.string.phone_required),
                    leadingIcon = Icons.Default.Phone,
                    isError = showPhoneNumberError
                )

                val errorMessage =
                    if (showPhoneNumberError) stringResource(id = R.string.phone_invalid) else ""

                if (showPhoneNumberError && errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    enabled = memberInfo != null && profileState !is UserProfileState.Loading && phoneNumberTouched && !showPhoneNumberError,
                    onClick = {
                        val info = memberInfo
                        if (info != null) {
                            val profile = UserProfile(
                                uid = currentUser?.uid ?: "",
                                email = currentUser?.email ?: "",
                                identifier = identifier,
                                fullName = info.fullName,
                                phoneNumber = phoneNumber,
                                memberType = selectedType,
                                department = info.department,
                                isActive = true
                            )
                            viewModel.saveProfile(profile)
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_enter_valid_id),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = AppShape.ShapeM,
                    content = {
                        if (profileState is UserProfileState.Loading) {
                            CircularProgressIndicator(
                                color = LightOnPrimary,
                                modifier = Modifier.size(Dimen.SizeM)
                            )
                        } else {
                            Text(
                                stringResource(R.string.confirm_finish),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                TextButton(onClick = {
                    selectedType =
                        if (selectedType == MemberType.STUDENT) MemberType.EMPLOYEE else MemberType.STUDENT
                    identifier = ""
                    viewModel.clearError()
                }) {
                    Text(
                        text = if (selectedType == MemberType.STUDENT)
                            stringResource(R.string.switch_to_employee)
                        else
                            stringResource(R.string.switch_to_student),
                        color = LightPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}