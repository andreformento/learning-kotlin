package com.andreformento.person

import kotlinx.serialization.Serializable

@Serializable
data class Person(val id: String, val fullName: String)