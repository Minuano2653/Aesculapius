package com.example.aesculapius.database

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.example.aesculapius.worker.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

const val USERS_COLLECTION_REF = "users"

/** [UserRemoteDataRepository] репозиторий для Firestore Database */
@Singleton
class UserRemoteDataRepository @Inject constructor(
    private val aesculapiusRepository: AesculapiusRepository,
    private val itemDAO: ItemDAO
) {
    companion object {
        val usersRef: CollectionReference = Firebase.firestore.collection(USERS_COLLECTION_REF)
        fun getUserId() = usersRef.document().id
    }

    /**
     * [addUserAtFirst] добавление пользователя в Firestore Database впервые
     */
    fun addUserAtFirst(signUpUiState: SignUpUiState) {
        val user = User(
            name = signUpUiState.name,
            surname = signUpUiState.surname,
            patronymic = signUpUiState.patronymic,
            height = signUpUiState.height.toFloat(),
            weight = signUpUiState.weight.toFloat(),
            birthDate = signUpUiState.birthday.toString(),
            morningReminder = signUpUiState.morningReminder.toString(),
            eveningReminder = signUpUiState.eveningReminder.toString(),
            astTestDate = signUpUiState.astTestDate,
            recommendationTestDate = signUpUiState.recommendationTestDate,
            astTests = listOf(),
            metrics = listOf()
        )
        usersRef.document(signUpUiState.id!!).set(user)
    }

    /**
     * [pullUserData] извлечение информации о пользователе через id.
     * Препараты читаются из subcollection medicines/{medicineId}/doses/{doseDocId}.
     */
    suspend fun pullUserData(userId: String): User {
        if (userId.isEmpty()) return User()
        var user = User()
        usersRef.document(userId).get()
            .addOnSuccessListener {
                val metrics = mutableListOf<MetricsItem>()
                val metricsList = it["metrics"] as? List<HashMap<String, Any>>
                metricsList?.forEach { metricsItem ->
                    metrics.add(
                        MetricsItem(
                            id = (metricsItem["id"] as? Long)!!.toInt(),
                            metrics = (metricsItem["metrics"] as? Double)!!.toFloat(),
                            date = LocalDate.parse(metricsItem["date"] as? String)
                        )
                    )
                }

                val astTests = mutableListOf<ScoreItem>()
                val astTestsList = it["astTests"] as? List<HashMap<String, Any>>
                astTestsList?.forEach { scoreItem ->
                    astTests.add(
                        ScoreItem(
                            id = (scoreItem["id"] as? Long)!!.toInt(),
                            score = (scoreItem["score"] as? Long)!!.toInt(),
                            date = LocalDate.parse(scoreItem["date"] as? String)
                        )
                    )
                }

                user = User(
                    name = (it["name"] as? String)!!,
                    surname = (it["surname"] as? String)!!,
                    patronymic = (it["patronymic"] as? String)!!,
                    height = (it["height"] as? Double)!!.toFloat(),
                    weight = (it["weight"] as? Double)!!.toFloat(),
                    birthDate = (it["birthDate"] as? String)!!,
                    morningReminder = (it["morningReminder"] as? String)!!,
                    eveningReminder = (it["eveningReminder"] as? String)!!,
                    recommendationTestDate = (it["recommendationTestDate"] as? String)!!,
                    astTestDate = (it["astTestDate"] as? String)!!,
                    metrics = metrics,
                    astTests = astTests
                )
            }.await()

        // Восстанавливаем препараты из subcollection
        val medicinesSnapshot = usersRef.document(userId).collection("medicines").get().await()
        for (medicineDoc in medicinesSnapshot.documents) {
            val medicineId = medicineDoc.id.toIntOrNull() ?: continue
            aesculapiusRepository.insertMedicineItem(
                medicineType = CurrentMedicineType.valueOf(medicineDoc["medicineType"] as? String ?: "Aerosol"),
                name = (medicineDoc["name"] as? String) ?: "",
                undername = (medicineDoc["undername"] as? String) ?: "",
                dose = (medicineDoc["dose"] as? String) ?: "",
                frequency = (medicineDoc["frequency"] as? String) ?: "",
                startDate = LocalDate.parse(medicineDoc["startDate"] as? String ?: LocalDate.now().toString()),
                endDate = LocalDate.parse(medicineDoc["endDate"] as? String ?: LocalDate.now().plusMonths(1).toString())
            )

            // Восстанавливаем статусы доз
            val dosesSnapshot = medicineDoc.reference.collection("doses").get().await()
            for (doseDoc in dosesSnapshot.documents) {
                val dateStr = doseDoc["date"] as? String ?: continue
                val isMorning = doseDoc["isMorning"] as? Boolean ?: continue
                val date = LocalDate.parse(dateStr)
                val isAccepted = doseDoc["isAccepted"] as? Boolean ?: false
                val isSkipped = doseDoc["isSkipped"] as? Boolean ?: false
                val dose = itemDAO.getDoseByMedicineIdDateAndMorning(medicineId, date, isMorning)
                if (dose != null) {
                    if (isAccepted) itemDAO.acceptMedicine(dose.idDose)
                    else if (isSkipped) itemDAO.skipMedicine(dose.idDose)
                }
            }
        }

        return user
    }

    fun updateAstDate(userId: String, astDate: LocalDate) {
        usersRef.document(userId).update(
            "astTestDate", Converters.dateToStringWithFormat(astDate)
        )
    }

    fun updateRecDate(userId: String, recDate: LocalDate) {
        usersRef.document(userId).update(
            "recommendationTestDate", Converters.dateToStringWithFormat(recDate)
        )
    }

    fun updateMorningDate(userId: String, morning: LocalDateTime) {
        usersRef.document(userId).update(
            "morningReminder", morning.toString()
        )
    }

    fun updateEveningDate(userId: String, evening: LocalDateTime) {
        usersRef.document(userId).update(
            "eveningReminder", evening.toString()
        )
    }

    /**
     * [updateUserProfile] обновление данных пользователя в Firestore Database
     */
    fun updateUserProfile(signUpUiState: SignUpUiState) {
        usersRef.document(signUpUiState.id!!).update(
            "birthDate", signUpUiState.birthday.toString(),
            "name", signUpUiState.name,
            "surname", signUpUiState.surname,
            "patronymic", signUpUiState.patronymic,
            "height", signUpUiState.height.toFloat(),
            "weight", signUpUiState.weight.toFloat(),
            "astTestDate", signUpUiState.astTestDate,
            "recommendationTestDate", signUpUiState.recommendationTestDate
        )
    }

    /**
     * [updateUser] синхронизирует метрики и результаты тестов в Firestore.
     * Препараты синхронизируются в реальном времени через RemoteMedicineDataSource.
     */
    suspend fun updateUser(userId: String) {
        val metricsList = aesculapiusRepository.getAllMetrics().map { metricsItem ->
            hashMapOf(
                "id" to metricsItem.id,
                "metrics" to metricsItem.metrics,
                "date" to metricsItem.date.toString()
            )
        }
        val astTestsList = aesculapiusRepository.getAllAstResults().map { scoreItem ->
            hashMapOf(
                "id" to scoreItem.id,
                "score" to scoreItem.score,
                "date" to scoreItem.date.toString()
            )
        }

        usersRef.document(userId)
            .update("metrics", metricsList, "astTests", astTestsList)
    }
}
