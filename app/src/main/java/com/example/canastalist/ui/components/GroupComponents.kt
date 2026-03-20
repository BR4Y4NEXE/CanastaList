package com.example.canastalist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canastalist.model.ShoppingGroup
import com.example.canastalist.ui.theme.*

@Composable
fun GroupSelector(
    activeGroupId: String?,
    groups: List<ShoppingGroup>,
    onSelect: (String?) -> Unit,
    onManageGroups: () -> Unit
) {
    val currentGroupName = groups.find { it.id == activeGroupId }?.name ?: "Mi Lista Personal"
    val isPersonalList = activeGroupId == null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isPersonalList) {
                                listOf(GreenFreshLight.copy(alpha = 0.2f), GreenFreshLight.copy(alpha = 0.05f))
                            } else {
                                listOf(OrangeVibrantLight.copy(alpha = 0.2f), OrangeVibrantLight.copy(alpha = 0.05f))
                            }
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPersonalList) GreenFresh else OrangeVibrant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPersonalList) Icons.Outlined.Checklist else Icons.Default.Group,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isPersonalList) "Lista Personal" else "Grupo Familiar",
                                fontSize = 11.sp,
                                color = if (isPersonalList) GreenFreshDark else OrangeVibrantDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentGroupName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceLight
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onManageGroups,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isPersonalList) GreenFreshLight.copy(alpha = 0.3f) else OrangeVibrantLight.copy(alpha = 0.3f),
                            contentColor = if (isPersonalList) GreenFreshDark else OrangeVibrantDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Gestionar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupManagementDialog(
    activeGroupId: String?,
    groups: List<ShoppingGroup>,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    onJoin: (String) -> Unit,
    onSwitch: (String?) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var groupCode by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceLight,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Gestionar Grupos",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = OnSurfaceLight
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isCreating) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = OrangeVibrantLight.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Unirse a una familia",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangeVibrantDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = groupCode,
                                onValueChange = { groupCode = it.uppercase() },
                                placeholder = { Text("Ej: AB12-CD34", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangeVibrant,
                                    unfocusedBorderColor = OrangeVibrantDark.copy(alpha = 0.3f),
                                    focusedTextColor = OnSurfaceLight,
                                    unfocusedTextColor = OnSurfaceLight,
                                    cursorColor = OrangeVibrant
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onJoin(groupCode) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = groupCode.length >= 4,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OrangeVibrant,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.GroupAdd, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Unirse con Código", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    TextButton(
                        onClick = { isCreating = true },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            "¿Quieres crear un grupo nuevo?",
                            color = GreenFresh,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenFreshLight.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Nombre de la Familia/Casa",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenFreshDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = groupName,
                                onValueChange = { groupName = it },
                                placeholder = { Text("Ej: Casa García", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenFresh,
                                    unfocusedBorderColor = GreenFreshDark.copy(alpha = 0.3f),
                                    focusedTextColor = OnSurfaceLight,
                                    unfocusedTextColor = OnSurfaceLight,
                                    cursorColor = GreenFresh
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onCreate(groupName) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenFresh,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Crear y Generar Código", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    TextButton(onClick = { isCreating = false }) {
                        Text("Volver", color = Color.Gray)
                    }
                }

                if (groups.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )

                    Text(
                        "Tus Grupos Actuales",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    groups.forEach { group ->
                        val isSelected = group.id == activeGroupId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) GreenFreshLight.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.1f)
                            )
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        group.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) GreenFreshDark else Color.DarkGray
                                    )
                                },
                                supportingContent = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            "Código: ${group.id}",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                        IconButton(
                                            onClick = {  },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Share,
                                                contentDescription = null,
                                                Modifier.size(14.dp),
                                                tint = PinkVibrant
                                            )
                                        }
                                    }
                                },
                                trailingContent = {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onSwitch(group.id) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = GreenFresh
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (activeGroupId == null) PinkVibrantLight.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.1f)
                        )
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Lista Personal",
                                    fontWeight = if (activeGroupId == null) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeGroupId == null) PinkVibrantDark else Color.DarkGray
                                )
                            },
                            trailingContent = {
                                RadioButton(
                                    selected = activeGroupId == null,
                                    onClick = { onSwitch(null) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PinkVibrant
                                    )
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenFresh,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar")
            }
        }
    )
}
