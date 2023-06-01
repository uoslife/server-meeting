package uoslife.servermeeting.domain.match.domain.service.impl

import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.match.domain.service.impl.matchingrole.Proposer
import uoslife.servermeeting.domain.match.domain.service.impl.matchingrole.Receiver

@Service
class MatchingAlgorithmService() {
    fun assignProposersToReceivers(
        receivers: List<Receiver>,
        proposers: List<Proposer>,
    ): List<Pair<Receiver, Proposer>> {
        val matches = mutableListOf<Pair<Receiver, Proposer>>()

        do {
            var receiver = getReceiverOnCondition(receivers)
            var proposer = getProposerOncondition(receiver)
            if (receiver == null || proposer == null) {
                break
            }

            deletePairFromMatch(receiver, proposer)

            makeNewPair(proposer, receiver)

            // For each successor, `s` to `receiver` in the preference list of `proposer`,
            // delete the pair `(p, s)` from the game.
            proposer.preferences = deletePreferenceOfReceiverBySuccessors(proposer, receiver)
        } while (true)

        receivers.forEach { receiver ->
            receiver.match.forEach { proposer ->
                matches.add(Pair(receiver, proposer))
            }
        }
        return matches
    }
    private fun getProposerOncondition(receiver: Receiver?) =
        receiver?.preferences?.firstOrNull()

    private fun getReceiverOnCondition(receivers: List<Receiver>) =
        receivers.firstOrNull {
            it.capacity > it.match.size && it.preferences.any { proposer -> proposer.match == null }
        }
    private fun makeNewPair(
        proposer: Proposer,
        receiver: Receiver,
    ) {
        proposer.match = receiver
        receiver.match.add(proposer)
    }

    fun deletePairFromMatch(receiver: Receiver, proposer: Proposer) {
        proposer.match?.match?.remove(proposer)
        proposer.preferences.remove(receiver)
    }

    fun deletePreferenceOfReceiverBySuccessors(proposer: Proposer, successor: Receiver): MutableList<Receiver> {
        return proposer.preferences.subList(
            proposer.preferences.indexOf(successor) + 1,
            proposer.preferences.size,
        )
    }

    fun allocateRoleOnMatching() {
        // count MeetingTeam on gender and allocate role.
        // gender that has more count will be allocated to proposer
    }

    fun makeMeetingTeamToPlayer() {
        // make MeetingTeam to Player
    }
}
