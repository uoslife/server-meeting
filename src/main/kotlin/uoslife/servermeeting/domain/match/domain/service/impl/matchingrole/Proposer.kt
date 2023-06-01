package uoslife.servermeeting.domain.match.domain.service.impl.matchingrole

class Proposer(val id: Long) {

    var preferences: MutableList<Receiver> = mutableListOf()

    var match: Receiver? = null
}
