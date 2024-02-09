package uoslife.servermeeting.domain.hr

data class Hospital(
    val name: String,
    val capacity: Int,
    val acceptedResidents: MutableList<Resident> = ArrayList(),
    val preferredResidents: MutableList<Resident> = ArrayList()
) {

  fun acceptResident(res: Resident): Resident? {
    var removedResident: Resident? = null
    if (atCapacity()) {
      removedResident = removeLastAcceptedResident()
    }
    if (acceptedResidents.isEmpty() || isRankedBetterThan(lastAcceptedResident(), res)) {
      acceptedResidents.add(res)
    } else {
      for (i in acceptedResidents.indices) {
        if (isRankedBetterThan(res, acceptedResidents[i])) {
          acceptedResidents.add(i, res)
          break
        }
      }
    }
    return removedResident
  }

  fun willAcceptResident(res: Resident): Boolean {
    if (preferredResidents.contains(res)) {
      if (!atCapacity()) {
        return true
      } else {
        if (isRankedBetterThan(res, lastAcceptedResident())) {
          return true
        }
      }
    }
    return false
  }

  fun atCapacity(): Boolean {
    return acceptedResidents.size >= capacity
  }

  fun isRankedBetterThan(res1: Resident, res2: Resident): Boolean {
    return preferredResidents.indexOf(res1) < preferredResidents.indexOf(res2)
  }

  fun lastAcceptedResident(): Resident {
    return acceptedResidents.last()
  }

  fun removeLastAcceptedResident(): Resident {
    return acceptedResidents.removeAt(acceptedResidents.size - 1)
  }
}
