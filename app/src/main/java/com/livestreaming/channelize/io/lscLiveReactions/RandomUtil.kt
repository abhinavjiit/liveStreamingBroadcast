package com.livestreaming.channelize.io.lscLiveReactions

import java.util.*

class RandomUtil {
    /**
     * Generates the random between two given integers.
     */
    companion object {

     @JvmStatic
        fun generateRandomBetween(start: Int, end: Int): Int {
            val random = Random()
            var rand = random.nextInt(Int.MAX_VALUE - 1) % end
            if (rand < start) {
                rand = start
            }
            return rand
        }
    }

}