package com.example.whattowatch.uielements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkDropDown(items: List<String>, onUpdate: (String) -> Unit, movieId: Int) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var type by remember {
        mutableStateOf("")
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { newValue ->
            isExpanded = newValue
        }
    ) {
        TextField(
            value = type,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            placeholder = {
                Text(text = "Mark Film")
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        ) }
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            isExpanded = false
        }
    ) {
        items.forEach{
            DropdownMenuItem(
                text = {
                    Text(text = it)
                },
                onClick = {
                    type = it
                    isExpanded = false
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }


}