package com.example.aesculapius.database

import com.example.aesculapius.data.CurrentMedicineType
import com.example.aesculapius.data.tests.remote.RemoteTestDataSource
import com.example.aesculapius.ui.signup.SignUpUiState
import com.example.aesculapius.worker.User
import com.google.firebase.firestore.FirebaseFirestore
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
    private val itemDAO: ItemDAO,
    private val remoteTestDataSource: RemoteTestDataSource,
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

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
            recommendationTestDate = signUpUiState.recommendationTestDate
        )
        usersRef.document(signUpUiState.id!!).set(user)
    }

    /**
     * [pullUserData] извлечение информации о пользователе через id.
     * Тесты и метрики читаются из подколлекций, препараты — из subcollection medicines.
     */
    suspend fun pullUserData(userId: String): User {
        if (userId.isEmpty()) return User()
        var user = User()

        // 1. Читаем базовые поля профиля пользователя
        usersRef.document(userId).get()
            .addOnSuccessListener { doc ->
                user = User(
                    name = (doc["name"] as? String) ?: "",
                    surname = (doc["surname"] as? String) ?: "",
                    patronymic = (doc["patronymic"] as? String) ?: "",
                    height = (doc["height"] as? Double)?.toFloat() ?: 0f,
                    weight = (doc["weight"] as? Double)?.toFloat() ?: 0f,
                    birthDate = (doc["birthDate"] as? String) ?: "",
                    morningReminder = (doc["morningReminder"] as? String) ?: "",
                    eveningReminder = (doc["eveningReminder"] as? String) ?: "",
                    recommendationTestDate = (doc["recommendationTestDate"] as? String) ?: "",
                    astTestDate = (doc["astTestDate"] as? String) ?: ""
                )
            }.await()

        // 2. Восстанавливаем AST-тесты из подколлекции
        remoteTestDataSource.fetchAstTests(userId).forEach { item ->
            if (itemDAO.getAllAstResultsInRange(item.date, item.date).isEmpty())
                itemDAO.insertASTTestScore(item.date, item.score)
        }

        // 3. Восстанавливаем метрики пикфлоуметрии из подколлекции
        remoteTestDataSource.fetchPeakFlow(userId).forEach { item ->
            if (itemDAO.getAllMetricsWithDate(item.date).isEmpty())
                itemDAO.insertMetrics(item.metrics, item.date)
        }

        // 4. Восстанавливаем тесты приверженности из подколлекции
        remoteTestDataSource.fetchRecommendationTests(userId).forEach { item ->
            itemDAO.insertRecommendationScore(item.date, item.score)
        }

        // 5. Восстанавливаем препараты из subcollection medicines
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
     * [updateUser] резервная синхронизация тестов и метрик в Firestore через подколлекции.
     * Вызывается воркером раз в 30 минут как fallback.
     */
    suspend fun updateUser(userId: String) {
        if (userId.isEmpty()) return
        remoteTestDataSource.syncAllAstTests(userId, aesculapiusRepository.getAllAstResults())
        remoteTestDataSource.syncAllPeakFlow(userId, aesculapiusRepository.getAllMetrics())
        remoteTestDataSource.syncAllRecommendationTests(userId, itemDAO.getAllRecommendationResults())
    }
}
