package uoslife.servermeeting.domain.hr

import java.io.Serializable

data class Resident(
    val name: String,
    var preferredHospitals: MutableList<Hospital> = mutableListOf()
) : Serializable {
    class ResidentBuilder {
        var preferredHospitals: MutableList<Hospital> = mutableListOf()
    }
}
