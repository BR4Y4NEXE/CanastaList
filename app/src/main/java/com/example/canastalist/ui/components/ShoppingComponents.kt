package com.example.canastalist.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canastalist.model.ShoppingItem
import com.example.canastalist.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onSetReminder: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> DeleteRed
                    else -> Color.Transparent
                }, label = "bgColor"
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f,
                label = "iconScale"
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, DeleteRed)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Borrar",
                    tint = Color.White,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        content = {
            val hasReminder = item.reminderTimestamp != null
            val gradientColors = if (item.isChecked) {
                listOf(Color.LightGray.copy(alpha = 0.3f), Color.LightGray.copy(alpha = 0.1f))
            } else if (hasReminder) {
                listOf(GreenFreshLight.copy(alpha = 0.15f), TealAccent.copy(alpha = 0.1f))
            } else {
                listOf(SurfaceLight, SurfaceLight.copy(alpha = 0.95f))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (item.isChecked) 0.dp else 3.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = Brush.horizontalGradient(gradientColors))
                        .clickable { onToggle() }
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { onToggle() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = GreenFresh,
                                uncheckedColor = GreenFreshDark.copy(alpha = 0.5f),
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (item.isChecked) Color.DarkGray else OnSurfaceLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (item.isChecked) Color.Gray else PinkVibrant)
                                )
                                Text(
                                    text = item.authorName ?: "Yo",
                                    fontSize = 12.sp,
                                    color = if (item.isChecked) Color.DarkGray else PinkVibrantDark,
                                    fontWeight = FontWeight.Bold
                                )
                                if (item.reminderTimestamp != null) {
                                    val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                                    Text(
                                        text = "• ${sdf.format(Date(item.reminderTimestamp))}",
                                        fontSize = 11.sp,
                                        color = TealAccent.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (!item.isChecked) {
                            FilledIconButton(
                                onClick = onSetReminder,
                                modifier = Modifier.size(44.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (hasReminder) TealAccent.copy(alpha = 0.15f) else GreenFreshLight.copy(alpha = 0.15f),
                                    contentColor = if (hasReminder) TealAccent else GreenFresh
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (hasReminder) Icons.Default.Notifications else Icons.Outlined.NotificationsNone,
                                    contentDescription = "Configurar recordatorio",
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDialogs(
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    if (!showTimePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = { showTimePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenFresh)
                ) { Text("Siguiente") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = GreenFresh,
                    todayContentColor = GreenFresh,
                    todayDateBorderColor = GreenFresh
                )
            )
        }
    } else {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        datePickerState.selectedDateMillis?.let { calendar.timeInMillis = it }
                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                        onDateTimeSelected(calendar.timeInMillis)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenFresh)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Atrás") }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Selecciona la hora",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = GreenFreshLight.copy(alpha = 0.3f),
                            selectorColor = GreenFresh,
                            containerColor = SurfaceLight
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun AddItemBar(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "¿Qué necesitas comprar?",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            },
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenFresh,
                unfocusedBorderColor = GreenFreshDark.copy(alpha = 0.3f),
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceLight,
                cursorColor = GreenFresh,
                focusedTextColor = OnSurfaceLight,
                unfocusedTextColor = OnSurfaceLight
            ),
            textStyle = LocalTextStyle.current.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onAdd(text)
                    text = ""
                }
            },
            modifier = Modifier
                .size(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenFresh,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Añadir",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun EmptyListPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GreenFreshLight.copy(alpha = 0.3f), GreenFreshLight.copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🛒",
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu canasta está vacía",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurfaceLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Añade productos para comenzar",
            fontSize = 15.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "¡Vamos a llenarla!",
            fontSize = 14.sp,
            color = GreenFresh,
            fontWeight = FontWeight.SemiBold
        )
    }
}
