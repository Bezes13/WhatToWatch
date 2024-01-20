package com.example.whattowatch.uielements

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
import androidx.compose.ui.tooling.preview.Preview
import com.example.whattowatch.R

@Composable
fun ShareFriendDialog(
    onDismissRequest: () -> Unit,
    saveName: (String, Int) -> Unit,

    ) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(Icons.Filled.Face, contentDescription = "Icon")
        },
        title = {
            Text(text = "Sharing is Caring")
        },
        text = {
            Column {
                Text(text = "Enter the name of your friend")
                TextField(value = name, onValueChange = { name = it })
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    saveName(name, R.string.friend_name)
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Preview
@Composable
fun AlertDialogPreview(){
ShareFriendDialog(onDismissRequest = { /*TODO*/ }, saveName = {_,_->})
}