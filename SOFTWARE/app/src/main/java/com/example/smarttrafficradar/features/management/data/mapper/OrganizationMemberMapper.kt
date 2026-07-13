package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.OrganizationMemberDto
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember

fun OrganizationMemberDto.toDomain(): OrganizationMember {
    return OrganizationMember(
        identifier = identifier ?: "",
        fullName = fullName ?: "",
        email = email ?: "",
        department = department ?: "",
        memberType = memberType ?: "",
        linkedUid = linkedUid
    )
}

fun OrganizationMember.toDto(): OrganizationMemberDto {
    return OrganizationMemberDto(
        identifier = identifier,
        fullName = fullName,
        email = email,
        department = department,
        memberType = memberType,
        linkedUid = linkedUid
    )
}
