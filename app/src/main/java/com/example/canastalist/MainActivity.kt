package com.example.canastalist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canastalist.model.ShoppingItem
import com.example.canastalist.ui.components.*
import com.example.canastalist.ui.theme.*
import com.example.canastalist.viewmodel.ShoppingViewModel
import com.example.canastalist.viewmodel.ShoppingViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        window.statusBarColor = android.graphics.Color.parseColor("#F8F9FA")
        window.navigationBarColor = android.graphics.Color.parseColor("#F8F9FA")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or 
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        
        setContent {
            CanastaListTheme {
                ShoppingApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingApp() {
    val context = LocalContext.current

    var showProfileDialog by remember { mutableStateOf(false) }
    var tempUserName by remember { mutableStateOf("") }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val database = (context.applicationContext as ShoppingApplication).database
    val viewModel: ShoppingViewModel = viewModel(
        factory = ShoppingViewModelFactory(database.shoppingDao())
    )

    val items by viewModel.items.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val groups by viewModel.groups.collectAsState()

    var itemForReminder by remember { mutableStateOf<ShoppingItem?>(null) }
    var showGroupDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(GreenFresh, GreenFreshDark)
                                    )
                                ),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            "CanastaList",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            letterSpacing = 0.5.sp,
                            color = OnSurfaceLight
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            tempUserName = preferences?.userName ?: ""
                            showProfileDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = PinkVibrant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            GroupSelector(
                activeGroupId = preferences?.activeGroupId,
                groups = groups,
                onSelect = { viewModel.switchGroup(it) },
                onManageGroups = { showGroupDialog = true }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                GreenFresh.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            AddItemBar(onAdd = { viewModel.addItem(it) })

            Box(modifier = Modifier.weight(1f)) {
                if (items.isEmpty()) {
                    EmptyListPlaceholder()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = items,
                            key = { it.id }
                        ) { item ->
                            Box(modifier = Modifier.animateItem(
                                fadeInSpec = tween(300),
                                fadeOutSpec = tween(300),
                                placementSpec = tween(300)
                            )) {
                                ShoppingItemRow(
                                    item = item,
                                    onToggle = { viewModel.toggleItem(item) },
                                    onDelete = { viewModel.removeItem(item) },
                                    onSetReminder = { itemForReminder = item }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showProfileDialog) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                containerColor = SurfaceLight,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        "¿Quién eres?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = OnSurfaceLight
                    )
                },
                text = {
                    OutlinedTextField(
                        value = tempUserName,
                        onValueChange = { tempUserName = it },
                        placeholder = { Text("Ej: Mamá, Juan, Perfil...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkVibrant,
                            unfocusedBorderColor = PinkVibrantDark.copy(alpha = 0.3f),
                            cursorColor = PinkVibrant,
                            focusedTextColor = OnSurfaceLight,
                            unfocusedTextColor = OnSurfaceLight
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setUserName(tempUserName)
                            showProfileDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkVibrant,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Guardar") }
                }
            )
        }

        if (showGroupDialog) {
            GroupManagementDialog(
                activeGroupId = preferences?.activeGroupId,
                groups = groups,
                onDismiss = { showGroupDialog = false },
                onCreate = { viewModel.createGroup(it) },
                onJoin = { viewModel.joinGroup(it) },
                onSwitch = { viewModel.switchGroup(it) }
            )
        }

        itemForReminder?.let { item ->
            ReminderDialogs(
                onDismiss = { itemForReminder = null },
                onDateTimeSelected = { timestamp ->
                    viewModel.setReminder(context, item, timestamp)
                    itemForReminder = null
                }
            )
        }
    }
}
