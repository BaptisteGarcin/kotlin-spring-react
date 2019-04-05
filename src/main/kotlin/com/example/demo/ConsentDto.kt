package com.example.demo

data class ConsentDto(
        val id: String,
        val name: String,
        val mailConsent: Boolean,
        val phoneConsent: Boolean
)
