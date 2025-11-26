package com.example.detec

import android.os.Bundle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddCircle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {},
    onReport: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "deTec Menú",
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )

                Divider(modifier = Modifier.padding(bottom = 8.dp))

                NavigationDrawerItem(
                    label = { Text(text = "Reportes", fontSize = 16.sp) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Reportes", tint = Color(0xFF6200EE)) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Evidenciar", fontSize = 16.sp) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }
                              onReport()
                              },
                    icon = { Icon(painterResource(android.R.drawable.ic_menu_camera), contentDescription = "Evidenciar", tint = Color.Gray) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Perfil de Usuario", fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.Gray) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Políticas de Seguridad", fontSize = 16.sp) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Políticas", tint = Color.Gray) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    label = { Text(text = "Configuración", fontSize = 16.sp) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = Color.Gray) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                Divider()

                NavigationDrawerItem(
                    label = { Text(text = "Cerrar Sesión", fontSize = 16.sp, color = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Cerrar Sesión", tint = Color.Red) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Reportes",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToProfile() }) {
                            Icon(Icons.Default.Person, contentDescription = "Ir al perfil", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF6200EE),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onReport() },
                    containerColor = Color(0xFF6200EE),
                    contentColor = Color.White,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear nuevo reporte", modifier = Modifier.size(32.dp))
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ilustración o Ícono más amigable
                    Icon(
                        imageVector = Icons.Default.Home, // O usa un PainterResource si tienes imagen
                        contentDescription = "Inicio",
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFF6200EE).copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "¡Bienvenido a deTec!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "No tienes reportes recientes.",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // BOTÓN NORMALIZADO (Estilo ReportActivity)
                    Button(
                        onClick = { onReport() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // Altura consistente
                        shape = RoundedCornerShape(12.dp), // Borde consistente
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "CREAR PRIMER REPORTE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Footer
                    Text(
                        text = "Todos los derechos reservados @deTec",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    DeTECTheme {
        HomeScreen(
            onNavigateToProfile = { println("Navegando a perfil...") },
            onLogout = { println("Cerrando sesión...") }
        )
    }
}