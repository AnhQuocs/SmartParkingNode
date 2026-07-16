package com.example.smarttrafficradar.features.management.domain.usecase

import javax.inject.Inject

data class RegistrationUseCases @Inject constructor(
    val sendRegistrationRequest: SendRegistrationRequestUseCase,
    val getRegistrationRequests: GetRegistrationRequestsUseCase,
    val approveRegistration: ApproveRegistrationUseCase,
    val rejectRegistration: RejectRegistrationUseCase,
    val getRegisteredCards: GetRegisteredCardsUseCase,
    val updateCardStatus: UpdateCardStatusUseCase,
    val lockCard: LockCardUseCase,
    val sendVehicleChangeRequest: SendVehicleChangeRequestUseCase
)