package com.example.aesculapius.ui.medicines

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aesculapius.R
import com.example.aesculapius.ui.TopBar
import com.example.aesculapius.ui.navigation.NavigationDestination
import com.example.aesculapius.ui.therapy.MedicineCard
import com.example.aesculapius.ui.therapy.MedicineItem

object MedicinesListScreen : NavigationDestination {
    override val route = "MedicinesListScreen"
}

object EditMedicineFromProfile : NavigationDestination {
    override val route = "EditMedicineFromProfileScreen"
}

object NewMedicineFromProfile : NavigationDestination {
    override val route = "NewMedicineFromProfileScreen"
}

@Composable
fun MedicinesListScreen(
    modifier: Modifier = Modifier,
    viewModel: MedicinesListViewModel = hiltViewModel<MedicinesListViewModel>(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (MedicineCard) -> Unit,
    onNavigateToNew: () -> Unit,
) {
    val medicines by viewModel.medicines.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = onNavigateBack,
                text = stringResource(R.string.my_medicines)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNew,
                modifier = modifier
                    .padding(16.dp)
                    .size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_medicine),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->
        if (medicines.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.medicines_list_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(medicines) { medicineItem ->
                    MedicineManagementCard(
                        medicineItem = medicineItem,
                        onClick = {
                            onNavigateToEdit(medicineItem.toMedicineCard())
                        },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

/** Карточка препарата без цветного индикатора слева (для экрана управления). */
@Composable
fun MedicineManagementCard(
    medicineItem: MedicineItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 16.dp
    var parentHeightPx by remember { mutableIntStateOf(0) }
    val parentHeightDp: Dp = with(LocalDensity.current) { parentHeightPx.toDp() }

    Card(
        elevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 112.dp, max = 136.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
                .onGloballyPositioned { parentHeightPx = it.size.height }
        ) {
            Image(
                painter = painterResource(id = R.drawable.medicines_example),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.30f)
                    .height(parentHeightDp)
            )
            Column(
                modifier = Modifier
                    .weight(0.70f)
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    text = medicineItem.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Text(
                    text = medicineItem.undername,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = medicineItem.dose,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = medicineItem.frequency,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

/** Конвертирует MedicineItem в MedicineCard для передачи на экран редактирования. */
private fun MedicineItem.toMedicineCard() = MedicineCard(
    id = idMedicine,
    name = name,
    undername = undername,
    dose = dose,
    frequency = "",
    isSkipped = false,
    isAccepted = false,
    medicineType = medicineType,
    startDate = startDate,
    endDate = endDate,
    doseId = 0,
    fullFrequency = frequency
)
