package com.example.smarttrafficradar.features.auth.domain.usecase

import javax.inject.Inject

class AuthUseCases @Inject constructor(
    val signUpUseCase: SignUpUseCase,
    val signInUseCase: SignInUseCase,
    val signUpAdminUseCase: SignUpAdminUseCase,
    val signOutUseCase: SignOutUseCase,
    val deleteCurrentAccountUseCase: DeleteCurrentAccountUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val getUserByIdUseCase: GetUserByIdUseCase
)