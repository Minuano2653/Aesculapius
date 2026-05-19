package com.example.aesculapius.domain.profile.usecases

import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserRegisterDateUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    operator fun invoke(): LocalDate {
        val millis = firebaseAuth.currentUser?.metadata?.creationTimestamp
            ?: return LocalDate.now()
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
