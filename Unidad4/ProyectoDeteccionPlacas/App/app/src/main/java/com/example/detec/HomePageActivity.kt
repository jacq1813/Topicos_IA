package com.example.detec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.detec.model.ReporteData
import com.example.detec.network.RetrofitClient
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
    val context = LocalContext.current
    val session = SessionManager(context)

    // VARIABLES PARA GUARDAR LOS DATOS
    var reportesList by remember { mutableStateOf<List<ReporteData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // --- CONSULTAR LA API AL INICIAR ---
    LaunchedEffect(Unit) {
        val userId = session.getUserId()
        if (userId != -1) {
            try {
                // Llamamos a la API usando el ID del usuario guardado en sesión
                val response = RetrofitClient.apiService.getReportesUsuario(userId)
                if (response.isSuccessful && response.body() != null) {
                    reportesList = response.body()!!
                }
            } catch (e: Exception) {
                // Error silencioso o Toast
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("deTec Menú", modifier = Modifier.padding(16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
                Divider(modifier = Modifier.padding(bottom = 8.dp))

                // OPCIÓN 1: INICIO
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, null, tint = Color(0xFF6200EE)) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // --- AGREGAMOS ESTA NUEVA OPCIÓN (PERFIL) ---
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile() // <--- Esta función ya la tienes disponible
                    },
                    icon = { Icon(Icons.Default.Person, null, tint = Color.Gray) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                // --------------------------------------------

                // OPCIÓN 3: REPORTAR
                NavigationDrawerItem(
                    label = { Text("Reportar") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onReport()
                    },
                    icon = { Icon(painterResource(android.R.drawable.ic_menu_camera), null, tint = Color.Gray) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // OPCIÓN 4: CERRAR SESIÓN
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    ){
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mis Reportes", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF6200EE))
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { onReport() }, containerColor = Color(0xFF6200EE), contentColor = Color.White) {
                    Icon(Icons.Default.Add, null)
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF6200EE))
                } else if (reportesList.isEmpty()) {
                    // VISTA VACÍA
                    EmptyStateView(onReport)
                } else {
                    // LISTA DE REPORTES
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reportesList) { reporte ->
                            ReportItemCard(reporte)
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

// TARJETA PARA CADA REPORTE
@Composable
fun ReportItemCard(reporte: ReporteData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).background(Color(0xFFE8EAF6), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(android.R.drawable.ic_menu_report_image), null, tint = Color(0xFF6200EE))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Placa: ${reporte.numPlaca}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF333333))
                Text(text = reporte.descripcion ?: "Sin descripción", fontSize = 14.sp, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                // Recortar fecha para que no salga la hora larga
                val fechaCorta = if (reporte.fecha != null && reporte.fecha.length >= 10) reporte.fecha.substring(0, 10) else "Fecha desconocida"
                Text(text = "📅 $fechaCorta", fontSize = 12.sp, color = Color(0xFF6200EE))
            }
        }
    }
}

// VISTA VACÍA
@Composable
fun EmptyStateView(onReport: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Home, null, modifier = Modifier.size(100.dp), tint = Color(0xFF6200EE).copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(30.dp))
        Text("Sin reportes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6200EE))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Aún no has generado reportes.", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { onReport() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Icon(Icons.Default.AddCircle, null)
            Spacer(modifier = Modifier.width(12.dp))
            Text("CREAR PRIMER REPORTE", fontWeight = FontWeight.Bold)
        }
    }
}