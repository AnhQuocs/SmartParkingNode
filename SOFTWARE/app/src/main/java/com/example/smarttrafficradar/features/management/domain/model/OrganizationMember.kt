package com.example.smarttrafficradar.features.management.domain.model

data class OrganizationMember(
    val identifier: String,
    val fullName: String,
    val email: String,
    val department: String,
    val memberType: String,
    val linkedUid: String? = null
)
