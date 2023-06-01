package uoslife.servermeeting.domain.match.domain.service.impl.matchingrole

class Receiver(val id: Long, var capacity: Int = 0) {

    var preferences: MutableList<Proposer> = mutableListOf()

    var match: MutableList<Proposer> = mutableListOf()
}