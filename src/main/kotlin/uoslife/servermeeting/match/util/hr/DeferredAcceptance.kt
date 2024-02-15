package uoslife.servermeeting.match.util.hr

/**
 */
object DeferredAcceptance {
    fun optimizeForResidents(
        residentsToMatch: MutableList<Resident>?,
        hospitalsToMatch: MutableList<Hospital>?
    ): MutableList<Hospital> {
        val residents: MutableList<Resident> = residentsToMatch ?: ArrayList()
        val hospitals: MutableList<Hospital> = hospitalsToMatch ?: ArrayList()
        while (residents.isNotEmpty()) {
            val resident: Resident = residents.removeAt(0)
            val preferredHospitals: MutableList<Hospital> = ArrayList(resident.preferredHospitals)
            while (preferredHospitals.isNotEmpty()) {
                val candidateHospital: Hospital = preferredHospitals.removeAt(0)
                if (candidateHospital.willAcceptResident(resident)) {
                    val removedResident = candidateHospital.acceptResident(resident)
                    if (removedResident != null) {
                        // handle the removed resident next in line.
                        residents.add(0, removedResident)
                    }
                    resident.preferredHospitals = preferredHospitals
                    break
                }
            }
        }
        return hospitals
    }
}
