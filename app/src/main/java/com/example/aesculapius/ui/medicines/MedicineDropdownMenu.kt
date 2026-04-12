package com.example.aesculapius.ui.medicines

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    menuName: String,
    menuList: List<String>,
    onCloseAction: (Int) -> Unit,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    isChoosen: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(menuList[initialIndex]) }
    val scrollState = rememberScrollState()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (isChoosen) selectedItem else "",
            label = { Text(text = menuName, color = MaterialTheme.colorScheme.primary) },
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .wrapContentHeight()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .scrollable(state = scrollState, orientation = Orientation.Vertical)
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                .heightIn(max = 240.dp)
        ) {
            menuList.forEachIndexed { index, selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        selectedItem = selectionOption
                        onCloseAction(index)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        color =
                            if (selectedItem != selectionOption) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}