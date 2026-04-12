package com.example.aesculapius.ui.medicines

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aesculapius.R
import com.example.aesculapius.data.CurrentMedicineType

@Composable
fun MedicineTypeSelector(
    currentType: CurrentMedicineType,
    onTypeSelected: (CurrentMedicineType) -> Unit
) {
    Row(modifier = Modifier.padding(top = 24.dp)) {

        val items = listOf(
            Triple(CurrentMedicineType.Aerosol, R.drawable.aerosol_icon, R.string.aerosol),
            Triple(CurrentMedicineType.Powder, R.drawable.powder_icon, R.string.powder),
            Triple(CurrentMedicineType.Tablets, R.drawable.tablets_icon, R.string.tablets)
        )

        items.forEachIndexed { index, (type, icon, text) ->
            CurrentMedicineTypeCard(
                isEnabled = currentType == type,
                medicineDrawableRes = icon,
                medicineStringRes = text,
                onClick = { onTypeSelected(type) }
            )

            if (index != items.lastIndex) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}