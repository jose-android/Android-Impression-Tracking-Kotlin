package com.example.impressiontutorialstepthrough.analytic

data class TrackingData(
    var viewDuration: Long? = 0,
    var viewId: String? = null,
    var percentageHeightVisible: Double? = 0.0
)