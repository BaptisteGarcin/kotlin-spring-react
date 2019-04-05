package com.example.demo

data class DoneBy(
        val userId: String,
        val role: String
)

data class Groups(
        val key: String,
        val label: String,
        val consents: List<Consents>
)

data class Consents(
        val key: String,
        val label: String,
        val checked: Boolean
)

data class ConsentNio(
        val userId: String,
        val name: String,
        val doneBy: DoneBy,
        val version: Int,
        val groups: List<Groups> = emptyList()
)
