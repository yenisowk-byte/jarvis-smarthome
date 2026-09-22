package com.jarvis.smarthome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF050914)
private val Card = Color(0xFF0C1424)
private val Blue = Color(0xFF22B8FF)
private val Cyan = Color(0xFF00E5FF)
private val Text = Color(0xFFEAF7FF)
private val Muted = Color(0xFF8CA3B8)

data class Device(val name: String, val icon: ImageVector, var on: Boolean, val room: String)

@Composable
fun JarvisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Bg,
            surface = Card,
            primary = Blue,
            onBackground = Text,
            onSurface = Text
        ),
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JarvisTheme { JarvisApp() } }
    }
}

@Composable
fun JarvisApp() {
    var tab by remember { mutableStateOf(0) }
    var listening by remember { mutableStateOf(false) }
    val devices = remember {
        mutableStateListOf(
            Device("Lights", Icons.Default.Lightbulb, true, "Living Room"),
            Device("TV", Icons.Default.Tv, false, "Living Room"),
            Device("AC", Icons.Default.AcUnit, true, "Bedroom"),
            Device("Fan", Icons.Default.Air, false, "Living Room"),
            Device("Kettle", Icons.Default.Coffee, false, "Kitchen"),
            Device("Curtains", Icons.Default.Blinds, false, "Living Room"),
            Device("Plug", Icons.Default.Power, true, "Kitchen"),
            Device("Vacuum", Icons.Default.CleaningServices, false, "Hall")
        )
    }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF071225), Bg, Color(0xFF03060D)))
        )
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(16.dp))
            TopBar()
            Spacer(Modifier.height(18.dp))

            when (tab) {
                0 -> HomeScreen(devices, listening) { listening = !listening }
                1 -> DevicesScreen(devices)
                2 -> ScenesScreen()
                else -> ProfileScreen()
            }

            Spacer(Modifier.weight(1f))
            BottomBar(tab) { tab = it }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun TopBar() {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("J A R V I S", fontSize = 24.sp, fontWeight = FontWeight.Light, letterSpacing = 5.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(7.dp).background(Color(0xFF36E28A), CircleShape))
                Spacer(Modifier.width(6.dp))
                Text("OFFLINE • LOCAL CONTROL", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
            }
        }
        IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings", tint = Muted) }
    }
}

@Composable
fun HomeScreen(devices: List<Device>, listening: Boolean, onMic: () -> Unit) {
    Text("Good evening.", color = Muted, fontSize = 14.sp)
    Text("How can I help you today?", fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(14.dp))

    GlassCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            JarvisOrb(listening)
            Spacer(Modifier.width(18.dp))
            Column(Modifier.weight(1f)) {
                Text(if (listening) "Listening…" else "JARVIS is ready", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(if (listening) "Say your command" else "Tap the orb or use a saved command", color = Muted, fontSize = 12.sp)
            }
            IconButton(onClick = onMic) {
                Icon(if (listening) Icons.Default.Stop else Icons.Default.Mic, "Voice", tint = Cyan)
            }
        }
    }

    Spacer(Modifier.height(14.dp))
    Text("House status", fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    GlassCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Stat("ONLINE", "${devices.count { it.on }}/${devices.size}", Color(0xFF36E28A))
            Stat("ROOMS", "4", Blue)
            Stat("MODE", "LOCAL", Cyan)
        }
    }

    Spacer(Modifier.height(18.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Quick devices", fontWeight = FontWeight.SemiBold)
        Text("View all", color = Blue, fontSize = 12.sp)
    }
    Spacer(Modifier.height(8.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(280.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(devices.take(6)) { DeviceCard(it) }
    }
}

@Composable
fun JarvisOrb(listening: Boolean) {
    val infinite = rememberInfiniteTransition(label = "orb")
    val pulse by infinite.animateFloat(
        initialValue = 0.96f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "pulse"
    )
    Box(
        Modifier.size(72.dp).scale(if (listening) pulse else 1f)
            .background(Brush.radialGradient(listOf(Cyan, Blue, Color(0xFF122A4B))), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Mic, "JARVIS", tint = Color.White, modifier = Modifier.size(30.dp))
    }
}

@Composable
fun Stat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(85.dp)) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun DevicesScreen(devices: List<Device>) {
    Text("Devices", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text("Everything connected to JARVIS", color = Muted, fontSize = 12.sp)
    Spacer(Modifier.height(14.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(devices.size) { DeviceCard(devices[it]) }
    }
}

@Composable
fun DeviceCard(device: Device) {
    GlassCard(Modifier.height(118.dp).clickable { device.on = !device.on }) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(device.icon, device.name, tint = if (device.on) Cyan else Muted)
                Switch(checked = device.on, onCheckedChange = { device.on = it })
            }
            Column {
                Text(device.name, fontWeight = FontWeight.SemiBold)
                Text(device.room, color = Muted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ScenesScreen() {
    Text("Scenes", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text("One command. Multiple actions.", color = Muted, fontSize = 12.sp)
    Spacer(Modifier.height(14.dp))
    listOf(
        Triple("Good Morning", "Lights + curtains + AC", Icons.Default.WbSunny),
        Triple("Movie Night", "TV + lights + curtains", Icons.Default.Movie),
        Triple("Sleep", "Lights off + AC", Icons.Default.DarkMode),
        Triple("Leaving Home", "Power off + security", Icons.Default.ExitToApp)
    ).forEach { scene ->
        GlassCard(Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(scene.third, scene.first, tint = Cyan)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(scene.first, fontWeight = FontWeight.SemiBold)
                    Text(scene.second, color = Muted, fontSize = 11.sp)
                }
                Icon(Icons.Default.ChevronRight, "Open", tint = Muted)
            }
        }
    }
    Spacer(Modifier.height(6.dp))
    Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
        Icon(Icons.Default.Add, null)
        Spacer(Modifier.width(6.dp))
        Text("Create custom scene")
    }
}

@Composable
fun ProfileScreen() {
    Text("JARVIS", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text("AI & Home Settings", color = Muted, fontSize = 12.sp)
    Spacer(Modifier.height(16.dp))
    listOf(
        "Voice Commands" to Icons.Default.RecordVoiceOver,
        "Language" to Icons.Default.Language,
        "Device Management" to Icons.Default.Devices,
        "Wi-Fi & Hub" to Icons.Default.Wifi,
        "Security & Privacy" to Icons.Default.Security,
        "About JARVIS" to Icons.Default.Info
    ).forEach { item ->
        GlassCard(Modifier.fillMaxWidth().padding(bottom = 9.dp).clickable { }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(item.second, item.first, tint = Blue)
                Spacer(Modifier.width(14.dp))
                Text(item.first, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ChevronRight, null, tint = Muted)
            }
        }
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier
            .background(
                Brush.linearGradient(listOf(Color(0xFF101D32), Color(0xFF091221))),
                RoundedCornerShape(20.dp)
            )
            .padding(15.dp),
        content = content
    )
}

@Composable
fun BottomBar(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar(containerColor = Color(0xDD070D19)) {
        val items = listOf(
            Icons.Default.Home to "Home",
            Icons.Default.Devices to "Devices",
            Icons.Default.AutoAwesome to "Scenes",
            Icons.Default.Person to "Profile"
        )
        items.forEachIndexed { i, pair ->
            NavigationBarItem(
                selected = selected == i,
                onClick = { onSelect(i) },
                icon = { Icon(pair.first, pair.second) },
                label = { Text(pair.second, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Cyan,
                    selectedTextColor = Cyan,
                    indicatorColor = Color(0x3322B8FF),
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )
        }
    }
}
