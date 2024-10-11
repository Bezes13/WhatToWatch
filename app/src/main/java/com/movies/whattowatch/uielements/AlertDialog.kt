package com.movies.whattowatch.uielements

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.movies.whattowatch.R

@Composable
fun TextFieldDialog(
    title: String,
    text: String,
    saveID: Int,
    onDismissRequest: () -> Unit,
    saveName: (String, Int) -> Unit,

    ) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(Icons.Filled.Face, contentDescription = "Icon")
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                Text(text = text)
                TextField(value = name, onValueChange = { name = it })
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    saveName(name, saveID)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Preview
@Composable
fun AlertDialogPreview() {
    TextFieldDialog(
        "Title",
        "Please enter some Text",
        213,
        onDismissRequest = { },
        saveName = { _, _ -> })
}