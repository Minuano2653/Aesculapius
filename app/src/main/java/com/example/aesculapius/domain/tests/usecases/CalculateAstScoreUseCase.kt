package com.example.aesculapius.domain.tests.usecases

import javax.inject.Inject

class CalculateAstScoreUseCase @Inject constructor() {
    operator fun invoke(answers: List<Int>): Int =
        answers.sum() + answers.size
}
