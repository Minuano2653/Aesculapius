package com.example.aesculapius.data

import com.example.aesculapius.R
import com.example.aesculapius.ui.diary.DiaryScreen
import com.example.aesculapius.ui.home.NavigationItemContent
import com.example.aesculapius.ui.profile.LearnItem
import com.example.aesculapius.ui.profile.ProfileScreen
import com.example.aesculapius.ui.statistics.GraphicTypeContent
import com.example.aesculapius.ui.statistics.GraphicTypes
import com.example.aesculapius.ui.statistics.StatisticsScreen
import com.example.aesculapius.ui.tests.Question
import com.example.aesculapius.ui.tests.Test
import com.example.aesculapius.ui.tests.TestsScreen
import com.example.aesculapius.ui.therapy.TherapyScreen

enum class Hours {
    Morning,
    Evening
}

enum class TestType {
    AST,
    Recommendations,
    Metrics
}

enum class CurrentMedicineType {
    Aerosol,
    Powder,
    Tablets
}

val navigationItemContentList = listOf(
    NavigationItemContent(
        pageType = StatisticsScreen.route,
        icon = R.drawable.statistics_icon
    ),
    NavigationItemContent(
        pageType = TherapyScreen.route,
        icon = R.drawable.icon_calendar_heart
    ),
    NavigationItemContent(
        pageType = DiaryScreen.route,
        icon = R.drawable.diary_icon
    ),
    NavigationItemContent(
        pageType = TestsScreen.route,
        icon = R.drawable.tests_icon
    ),
    NavigationItemContent(
        pageType = ProfileScreen.route,
        icon = R.drawable.profile_icon
    )
)

val doctorNavigationItemContentList = listOf(
    NavigationItemContent(
        pageType = com.example.aesculapius.ui.doctor.navigation.PatientsListScreen.route,
        icon = R.drawable.statistics_icon
    ),
    NavigationItemContent(
        pageType = com.example.aesculapius.ui.doctor.navigation.DoctorProfileScreen.route,
        icon = R.drawable.profile_icon
    )
)

val topBarHomeScreen = mapOf(
    StatisticsScreen.route to Pair(R.string.statistics_name, true),
    TherapyScreen.route to Pair(R.string.therapy_name, false),
    TestsScreen.route to Pair(R.string.tests_name, true),
    DiaryScreen.route to Pair(R.string.diary_screen_title, false),
    ProfileScreen.route to Pair(R.string.profile_name, false)
)

val graphicsNavigationItemContentList = listOf(
    GraphicTypeContent(graphicTypes = GraphicTypes.Week, nameOfType = R.string.week_chart),
    GraphicTypeContent(graphicTypes = GraphicTypes.Month, nameOfType = R.string.month_chart),
    GraphicTypeContent(graphicTypes = GraphicTypes.ThreeMonths, nameOfType = R.string.three_months_chart),
    GraphicTypeContent(graphicTypes = GraphicTypes.HalfYear, nameOfType = R.string.half_year_chart),
    GraphicTypeContent(graphicTypes = GraphicTypes.Year, nameOfType = R.string.year_chart),
)

val astTest = Test(
    listOfQuestion = mutableListOf(
        Question(
            questionText = R.string.first_ast_test_text,
            answersList = mutableListOf(R.string.all_the_time, R.string.very_often, R.string.sometimes, R.string.rarely, R.string.never)
        ),
        Question(
            questionText = R.string.second_ast_test_text,
            answersList = mutableListOf(R.string.more_one_a_week, R.string.one_time_a_week, R.string.three_six_a_week, R.string.one_two_a_week, R.string.no_one)
        ),
        Question(
            questionText = R.string.third_ast_test_text,
            answersList = mutableListOf(R.string.four_nights_a_week, R.string.two_three_nights_a_week, R.string.one_time_a_week, R.string.one_two_times_a_week, R.string.no_one)
        ),
        Question(
            questionText = R.string.fourth_ast_test_text,
            answersList = mutableListOf(R.string.three_times_a_day, R.string.one_or_two_a_day, R.string.two_or_three_a_week, R.string.one_time_a_week_rarely, R.string.no_one)
        ),
        Question(
            questionText = R.string.fifth_ast_test_text,
            answersList = mutableListOf(R.string.very_bad_control_asthma, R.string.bad_control_asthma, R.string.normal_control_asthma, R.string.good_control_asthma, R.string.awesome_control_asthma)
        )
    )
)

val recTest = Test(
    listOfQuestion = mutableListOf(
        Question(
            questionText = R.string.first_rec_test_text,
            answersList = mutableListOf(R.string.never, R.string.rarely, R.string.sometimes, R.string.often, R.string.always)
        ),
        Question(
            questionText = R.string.second_rec_test_text,
            answersList = mutableListOf(R.string.never, R.string.rarely, R.string.sometimes, R.string.often, R.string.always)
        ),
        Question(
            questionText = R.string.third_rec_test_text,
            answersList = mutableListOf(R.string.never, R.string.rarely, R.string.sometimes, R.string.often, R.string.always)
        ),
        Question(
            questionText = R.string.fourth_rec_test_text,
            answersList = mutableListOf(R.string.never, R.string.rarely, R.string.sometimes, R.string.often, R.string.always)
        ),
        Question(
            questionText = R.string.fifth_rec_test_text,
            answersList = mutableListOf(R.string.yes, R.string.no)
        ),
        Question(
            questionText = R.string.sixth_rec_test_text,
            answersList = mutableListOf(R.string.never, R.string.rarely, R.string.sometimes, R.string.often, R.string.always)
        ),
        Question(
            questionText = R.string.seventh_rec_test_text,
            answersList = mutableListOf(R.string.yes, R.string.no)
        ),
        Question(
            questionText = R.string.eighth_rec_test_text,
            answersList = mutableListOf(R.string.yes, R.string.no)
        ),
        Question(
            questionText = R.string.ninth_rec_test_text,
            answersList = mutableListOf(R.string.yes, R.string.no)
        )
    )
)

val daysUsual = mapOf(
    "янв" to 31,
    "фев" to 28,
    "мар" to 31,
    "апр" to 30,
    "май" to 31,
    "июн" to 30,
    "июл" to 31,
    "авг" to 31,
    "сен" to 30,
    "окт" to 31,
    "ноя" to 30,
    "дек" to 31
)

val daysSpecial = mapOf(
    "янв" to 31,
    "фев" to 29,
    "мар" to 31,
    "апр" to 30,
    "май" to 31,
    "июн" to 30,
    "июл" to 31,
    "авг" to 31,
    "сен" to 30,
    "окт" to 31,
    "ноя" to 30,
    "дек" to 31
)

val months = listOf("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек")

val learnList = listOf(
    LearnItem(
        name = R.string.what_is_asthma,
        text = R.string.what_is_asthma_text
    ),
    LearnItem(
        name = R.string.why_asthma_appears,
        text = R.string.why_asthma_appears_text
    ),
    LearnItem(
        name = R.string.asthma_symptoms,
        text = R.string.asthma_symptoms_text
    ),
    LearnItem(
        name = R.string.asthma_diagnosis,
        text = R.string.asthma_diagnosis_text
    ),
    LearnItem(
        name = R.string.how_heal_asthma,
        text = R.string.how_heal_asthma_text
    ),
    LearnItem(
        name = R.string.what_is_activity,
        text = R.string.what_is_activity_text
    )
)
