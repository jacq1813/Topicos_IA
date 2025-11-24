package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.ui.theme.DeTECTheme
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeTECTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Necesario para componentes nuevos como TopAppBar
@Composable
fun HomeScreen() {
    // Estado para controlar si el menú lateral está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Para animaciones (abrir/cerrar menú)

    // ESTRUCTURA PRINCIPAL: EL MENÚ LATERAL
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("deTec Menú", modifier = Modifier.padding(16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Divider()

                // Opciones del menú lateral
                NavigationDrawerItem(
                    label = { Text(text = "Reportes") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Evidenciar") },
                    selected = false,
                    onClick = { /* Navegar a la cámara */ },
                    icon = { Icon(painter = painterResource(android.R.drawable.ic_menu_camera), contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Polit. Seg") },
                    selected = false,
                    onClick = { /* Navegar a políticas */ },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Config.") },
                    selected = false,
                    onClick = { /* Navegar a config */ },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
        }
    ) {
        // CONTENIDO DE LA PANTALLA (Page 3)
        Scaffold(
            // 1. Barra Superior (TopBar) con botón de Menú
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Reportes", fontWeight = FontWeight.Bold) }, // [cite: 16]
                    // 1. IZQUIERDA: Ícono de Menú
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },

                    // 2. DERECHA: Ícono de Usuario
                    actions = {
                        IconButton(onClick = {
                            /* Aquí navegarías a la pantalla de Perfil */
                        }) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil de Usuario", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF6200EE), // Tu color morado
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            // 2. Botón Flotante (FAB) para "Haz un reporte aquí"
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Acción nuevo reporte */ },
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear reporte")
                }
            }
        ) { paddingValues ->

            // 3. Cuerpo de la pantalla (Empty State)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Importante: Respeta el espacio de la barra superior
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Icono de "Caja vacía" o similar
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.LightGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Texto central
                Text(
                    text = "Parece que no hay nada por aqui...\nHaz un reporte aqui",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                // Footer [cite: 18]
                Text(
                    text = "Todos los derechos reservados @deTec",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    DeTECTheme {
        HomeScreen()
    }
}