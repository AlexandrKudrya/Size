package com.example.sizetracker.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddSleepDialog(
    onDismiss: () -> Unit,
    onConfirm: (hours: Float, quality: Int) -> Unit
) {
    var hours by remember { mutableStateOf("") }
    var quality by remember { mutableIntStateOf(3) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "😴 Добавить сон",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hours input
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Часы сна") },
                    placeholder = { Text("Например: 7.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Quality selector
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Качество сна",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Star rating display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            Text(
                                text = if (index < quality) "⭐" else "☆",
                                fontSize = 32.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider
                    Slider(
                        value = quality.toFloat(),
                        onValueChange = { quality = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quality labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Плохо", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Отлично", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    hours.toFloatOrNull()?.let { h ->
                        if (h > 0 && h <= 24) {
                            onConfirm(h, quality)
                        }
                    }
                },
                enabled = hours.toFloatOrNull()?.let { it > 0 && it <= 24 } == true
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
