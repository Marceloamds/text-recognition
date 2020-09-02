package com.jera.vision.data.entity

import java.io.Serializable

data class ApiErrors(
    val errors: List<String>?,
    val errorMessage: String
) : Serializable