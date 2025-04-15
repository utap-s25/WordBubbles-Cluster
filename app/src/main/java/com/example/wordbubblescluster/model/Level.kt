package com.example.wordbubblescluster.model

import java.io.Serializable

data class Level(
    val level: Int,
    val letters: String,
    val solution: List<String>,
    val width: Int,
    val height: Int
): Comparable<Level>, Serializable {
    fun isWordInSolution(word: String): Boolean {
        return solution.contains(word)
    }

    override fun compareTo(other: Level) = compareValuesBy(this, other, { it.level }, { it.level })
}

class Repository {
    private var levels = listOf(
        Level(
            level = 1,
            letters = "WORD",
            solution = listOf("WORD"),
            width = 2,
            height = 2
        ),
        Level(
            level = 2,
            letters = "ALSE",
            solution = listOf("SALE"),
            width = 2,
            height = 2
        ),
        Level(
            level = 3,
            letters = "EAKR",
            solution = listOf("RAKE"),
            width = 2,
            height = 2
        ),
        Level(
            level = 4,
            letters = "JKCA",
            solution = listOf("JACK"),
            width = 2,
            height = 2
        ),
        Level(
            level = 5,
            letters = "RIDB",
            solution = listOf("BIRD"),
            width = 2,
            height = 2
        ),
        Level(
            level = 6,
            letters = "RFEI",
            solution = listOf("FIRE"),
            width = 2,
            height = 2
        ),
        Level(
            level = 7,
            letters = "IPCK",
            solution = listOf("PICK"),
            width = 2,
            height = 2
        ),
        Level(
            level = 8,
            letters = "MKEA",
            solution = listOf("MAKE"),
            width = 2,
            height = 2
        ),
        Level(
            level = 9,
            letters = "IFSH",
            solution = listOf("FISH"),
            width = 2,
            height = 2
        ),
        Level(
            level = 10,
            letters = "FRTIUNCKY",
            solution = listOf("TRICKY", "FUN"),
            width = 3,
            height = 3
        ),
        Level(
            level = 11,
            letters = "ELTHDTTRO",
            solution = listOf("THROTTLED"),
            width = 3,
            height = 3
        ),
        Level(
            level = 12,
            letters = "LLAAEUTGH",
            solution = listOf("LAUGH", "LATE"),
            width = 3,
            height = 3
        ),
        Level(
            level = 13,
            letters = "MESRATFON",
            solution = listOf("FRONT", "SAME"),
            width = 3,
            height = 3
        ),
        Level(
            level = 14,
            letters = "DLBPUAAES",
            solution = listOf("PAUSE", "BALD"),
            width = 3,
            height = 3
        ),
        Level(
            level = 15,
            letters = "CRBSINEVE",
            solution = listOf("SEVEN", "CRIB"),
            width = 3,
            height = 3
        ),
        Level(
            level = 16,
            letters = "PMEARTDLA",
            solution = listOf("LATER", "DAMP"),
            width = 3,
            height = 3
        ),
        Level(
            level = 17,
            letters = "TRBKHASIG",
            solution = listOf("SIGHT", "BARK"),
            width = 3,
            height = 3
        ),
        Level(
            level = 18,
            letters = "TABTEONWP",
            solution = listOf("PET", "TAB", "OWN"),
            width = 3,
            height = 3
        ),
        Level(
            level = 19,
            letters = "NIPMXSIBO",
            solution = listOf("MIX", "SOB", "PIN"),
            width = 3,
            height = 3
        ),
        Level(
            level = 20,
            letters = "GETMIGAJB",
            solution = listOf("GET", "BIG", "JAM"),
            width = 3,
            height = 3
        ),
        Level(
            level = 21,
            letters = "LLOGETBUH",
            solution = listOf("LEG", "LOT", "HUB"),
            width = 3,
            height = 3
        ),
        Level(
            level = 22,
            letters = "DNFEERSIIZAWYROO",
            solution = listOf("WOOZY", "RAISE", "FRIEND"),
            width = 4,
            height = 4
        ),
        Level(
            level = 23,
            letters = "CTHCVIMNOPEUUCHD",
            solution = listOf("VOUCH", "MUNCH", "DEPICT"),
            width = 4,
            height = 4
        ),
        Level(
            level = 24,
            letters = "ERPUMSFROAYINMLC",
            solution = listOf("PURIFY", "SERMON", "CLAM"),
            width = 4,
            height = 4
        ),
        Level(
            level = 25,
            letters = "KCUDHFLOETRIWALD",
            solution = listOf("FLORID", "WEALTH", "DUCK"),
            width = 4,
            height = 4
        ),
        Level(
            level = 26,
            letters = "RWTPYOUEESRGARGE",
            solution = listOf("WROTE", "PURGE", "GREASY"),
            width = 4,
            height = 4
        ),
        Level(
            level = 27,
            letters = "OURERFTEAFHGIXNA",
            solution = listOf("AFFIX", "ROUTE", "HANGER"),
            width = 4,
            height = 4
        ),
        Level(
            level = 28,
            letters = "IVANFDELIERPOYNOEIEOVRATH",
            solution = listOf("PROVE", "IRATE", "VIDEO", "FINAL", "HONEY"),
            width = 5,
            height = 5
        ),
        Level(
            level = 29,
            letters = "EILOOVIPRTKSACIOESLMLRACE",
            solution = listOf("OLIVE", "SPIKE", "SLIME", "CAROL", "ACTOR"),
            width = 5,
            height = 5
        ),
        Level(
            level = 30,
            letters = "PPLEHOHHCNLUAREYSDNMMOTIP",
            solution = listOf("LOUSY", "DRENCH", "PHANTOM", "HELP", "IMP"),
            width = 5,
            height = 5
        ),

    )

    fun getNumLevels(): Int {
        return levels.size
    }

    fun getLevelRange(): IntRange {
        return levels.min().level..levels.max().level
    }

    fun fetchLevel(levelNumber: Int): Level {
        return levels.first { it.level == levelNumber }
    }
}
