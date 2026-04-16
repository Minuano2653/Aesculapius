package com.example.aesculapius.data.tests.remote

import com.example.aesculapius.database.USERS_COLLECTION_REF
import com.example.aesculapius.ui.tests.MetricsItem
import com.example.aesculapius.ui.tests.RecommendationItem
import com.example.aesculapius.ui.tests.ScoreItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteTestDataSource @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val usersRef = firestore.collection(USERS_COLLECTION_REF)

    private fun astTestsRef(userId: String) =
        usersRef.document(userId).collection("astTests")

    private fun peakFlowRef(userId: String) =
        usersRef.document(userId).collection("peakFlowTests")

    private fun recommendationRef(userId: String) =
        usersRef.document(userId).collection("recommendationTests")

    /** Немедленное сохранение результата AST-теста (fire-and-forget) */
    fun saveAstTest(userId: String, date: LocalDate, score: Int) {
        astTestsRef(userId).document(date.toString()).set(
            hashMapOf("score" to score, "date" to date.toString())
        )
    }

    /** Немедленное сохранение результата пикфлоуметрии (upsert по дате, fire-and-forget) */
    fun savePeakFlow(userId: String, date: LocalDate, metrics: Float) {
        peakFlowRef(userId).document(date.toString()).set(
            hashMapOf("metrics" to metrics, "date" to date.toString())
        )
    }

    /** Немедленное сохранение результата теста приверженности (fire-and-forget) */
    fun saveRecommendationTest(userId: String, date: LocalDate, score: Int) {
        recommendationRef(userId).document(date.toString()).set(
            hashMapOf("score" to score, "date" to date.toString())
        )
    }

    /** Пакетная синхронизация AST-тестов (для воркера-fallback) */
    suspend fun syncAllAstTests(userId: String, items: List<ScoreItem>) {
        if (items.isEmpty()) return
        val batch = Firebase.firestore.batch()
        items.forEach { item ->
            batch.set(
                astTestsRef(userId).document(item.date.toString()),
                hashMapOf("score" to item.score, "date" to item.date.toString())
            )
        }
        batch.commit().await()
    }

    /** Пакетная синхронизация метрик пикфлоуметрии (для воркера-fallback) */
    suspend fun syncAllPeakFlow(userId: String, items: List<MetricsItem>) {
        if (items.isEmpty()) return
        val batch = Firebase.firestore.batch()
        items.forEach { item ->
            batch.set(
                peakFlowRef(userId).document(item.date.toString()),
                hashMapOf("metrics" to item.metrics, "date" to item.date.toString())
            )
        }
        batch.commit().await()
    }

    /** Пакетная синхронизация тестов приверженности (для воркера-fallback) */
    suspend fun syncAllRecommendationTests(userId: String, items: List<RecommendationItem>) {
        if (items.isEmpty()) return
        val batch = Firebase.firestore.batch()
        items.forEach { item ->
            batch.set(
                recommendationRef(userId).document(item.date.toString()),
                hashMapOf("score" to item.score, "date" to item.date.toString())
            )
        }
        batch.commit().await()
    }

    /** Загрузка AST-тестов из Firestore (для pullUserData) */
    suspend fun fetchAstTests(userId: String): List<ScoreItem> {
        return usersRef.document(userId).collection("astTests").get().await()
            .documents.mapIndexed { index, doc ->
                ScoreItem(
                    id = index,
                    score = (doc["score"] as? Long)?.toInt() ?: 0,
                    date = LocalDate.parse(doc["date"] as? String ?: LocalDate.now().toString())
                )
            }
    }

    /** Загрузка метрик пикфлоуметрии из Firestore (для pullUserData) */
    suspend fun fetchPeakFlow(userId: String): List<MetricsItem> {
        return usersRef.document(userId).collection("peakFlowTests").get().await()
            .documents.mapIndexed { index, doc ->
                MetricsItem(
                    id = index,
                    metrics = (doc["metrics"] as? Double)?.toFloat() ?: 0f,
                    date = LocalDate.parse(doc["date"] as? String ?: LocalDate.now().toString())
                )
            }
    }

    /** Загрузка тестов приверженности из Firestore (для pullUserData) */
    suspend fun fetchRecommendationTests(userId: String): List<RecommendationItem> {
        return usersRef.document(userId).collection("recommendationTests").get().await()
            .documents.mapIndexed { index, doc ->
                RecommendationItem(
                    id = index,
                    score = (doc["score"] as? Long)?.toInt() ?: 0,
                    date = LocalDate.parse(doc["date"] as? String ?: LocalDate.now().toString())
                )
            }
    }
}
