package com.example.aesculapius.ui.doctor.navigation

import com.example.aesculapius.ui.navigation.NavigationDestination

object PatientsListScreen : NavigationDestination {
    override val route = "PatientsListScreen"
}

object DoctorProfileScreen : NavigationDestination {
    override val route = "DoctorProfileScreen"
}

object PatientDetailScreen : NavigationDestination {
    override val route = "PatientDetailScreen"
    const val patientIdArg = "patientId"
    val routeWithArgs = "$route/{$patientIdArg}"
}
