package com.example.aesculapius.domain.tests.usecases

import javax.inject.Inject

class CalculateRecommendationScoreUseCase @Inject constructor() {
    operator fun invoke(answers: List<Int>): Int {
        var score = 0
        for (i in 0..3) score += answers[i]
        score += if (answers[4] == 0) 0 else 4
        score += answers[5]
        score += if (answers[6] == 0) 0 else 4
        score += if (answers[7] == 0) 0 else 4
        score += if (answers[8] == 0) 4 else 0
        return score
    }
}
