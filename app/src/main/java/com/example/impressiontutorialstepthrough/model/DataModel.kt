package com.example.impressiontutorialstepthrough.model

data class DataModel(
    var nombre: String? = null
) : DataTracking {
    override var tracked: Boolean = false
}