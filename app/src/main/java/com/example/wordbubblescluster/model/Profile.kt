package com.example.wordbubblescluster.model

import java.io.Serializable

data class Profile(
    var uid: String = "",
    var name: String = "",
    var bio: String = "",
    var profilePictureUuid: String = "",
    var levelsCompleted: Int = 0
): Comparable<Profile>, Serializable {
    override fun compareTo(other: Profile): Int {
        val compareByLevels = compareValuesBy(this, other, { it.levelsCompleted }, { it.levelsCompleted })
        if (compareByLevels == 0)
                return -compareValuesBy(this, other, { it.name }, { it.name })
        return compareByLevels
    }
}
