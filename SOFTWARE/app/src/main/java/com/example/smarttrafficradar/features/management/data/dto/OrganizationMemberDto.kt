package com.example.smarttrafficradar.features.management.data.dto

import com.google.firebase.firestore.PropertyName

data class OrganizationMemberDto(
    @get:PropertyName("identifier")
    @set:PropertyName("identifier")
    var identifier: String? = null,
    
    @get:PropertyName("fullName")
    @set:PropertyName("fullName")
    var fullName: String? = null,
    
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String? = null,
    
    @get:PropertyName("department")
    @set:PropertyName("department")
    var department: String? = null,
    
    @get:PropertyName("memberType")
    @set:PropertyName("memberType")
    var memberType: String? = null,
    
    @get:PropertyName("linkedUid")
    @set:PropertyName("linkedUid")
    var linkedUid: String? = null
)
