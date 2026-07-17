package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.usecase.cards.GetPendingCardsUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.cards.GetRegisteredCardsUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.cards.LockCardUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.cards.UpdateCardStatusUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.cards.UpdatePendingCardStatusUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.members.GetOrganizationMembersUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.registration.ApproveRegistrationUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.registration.GetRegistrationRequestsUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.registration.RejectRegistrationUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.registration.SendRegistrationRequestUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.vehicle.GetVehicleChangeRequestsUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.vehicle.HandleVehicleChangeDecisionUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.vehicle.SendVehicleChangeRequestUseCase
import javax.inject.Inject

data class RegistrationUseCases @Inject constructor(
    // Registration
    val sendRegistrationRequest: SendRegistrationRequestUseCase,
    val getRegistrationRequests: GetRegistrationRequestsUseCase,
    val approveRegistration: ApproveRegistrationUseCase,
    val rejectRegistration: RejectRegistrationUseCase,

    // Cards
    val getRegisteredCards: GetRegisteredCardsUseCase,
    val updateCardStatus: UpdateCardStatusUseCase,
    val lockCard: LockCardUseCase,
    val getPendingCards: GetPendingCardsUseCase,
    val updatePendingCardStatus: UpdatePendingCardStatusUseCase,

    // Members
    val getOrganizationMembers: GetOrganizationMembersUseCase,

    // Vehicle Change
    val sendVehicleChangeRequest: SendVehicleChangeRequestUseCase,
    val getVehicleChangeRequests: GetVehicleChangeRequestsUseCase,
    val handleVehicleChangeDecision: HandleVehicleChangeDecisionUseCase
)
