package com.pulse.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private val Bg = Color(0xFF0A1214)
private val Panel = Color(0xFF111D20)
private val Teal = Color(0xFF2DD4BF)
private val Hi = Color(0xFFE6FFFB)
private val Lo = Color(0xFF86BDB6)
private val Mono = FontFamily.Monospace

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MidnightScheduler.schedule(this)
        setContent { PulseApp() }
    }
}

@Composable
private fun PulseApp() {
    val ctx = LocalContext.current
    var themeKey by remember { mutableStateOf(Prefs.theme(ctx).key) }
    var animBg by remember { mutableStateOf(Prefs.animBg(ctx)) }
    var animRing by remember { mutableStateOf(Prefs.animRing(ctx)) }
    var weekStart by remember { mutableIntStateOf(Prefs.weekStart(ctx)) }
    val version = "$themeKey|$animBg|$animRing|$weekStart"

    MaterialTheme(colorScheme = darkColorScheme(primary = Teal, background = Bg, surface = Panel, onBackground = Hi, onSurface = Hi)) {
        Surface(color = Bg, modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Column {
                        Text("> pulse_widgets", color = Teal, fontFamily = Mono, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Animated clock & calendar widgets. Long-press your home screen → Widgets → Pulse Widgets, or tap Add below.",
                            color = Lo, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
                item {
                    Section("THEME") {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            PulseTheme.values().forEach { th ->
                                val sel = th.key == themeKey
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(th.accent))
                                            .border(if (sel) 3.dp else 0.dp, Hi, CircleShape)
                                            .clickable {
                                                Prefs.setTheme(ctx, th); themeKey = th.key; WidgetUpdater.updateAll(ctx)
                                            }
                                    )
                                    Text(th.label.split(" ").last(), color = if (sel) Hi else Lo, fontSize = 10.sp, fontFamily = Mono, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
                item {
                    Section("ANIMATION") {
                        ToggleRow("Animated backgrounds", animBg) { Prefs.setAnimBg(ctx, it); animBg = it; WidgetUpdater.updateAll(ctx) }
                        ToggleRow("Spinning rings", animRing) { Prefs.setAnimRing(ctx, it); animRing = it; WidgetUpdater.updateAll(ctx) }
                        Text("Turn both off to save battery.", color = Lo, fontSize = 11.sp, fontFamily = Mono)
                    }
                }
                item {
                    Section("WEEK STARTS ON") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Locale", "Sunday", "Monday").forEachIndexed { i, label ->
                                Chip(label, weekStart == i) { Prefs.setWeekStart(ctx, i); weekStart = i; WidgetUpdater.updateAll(ctx) }
                            }
                        }
                    }
                }
                item { SectionLabel("CLOCK WIDGETS") }
                items(WidgetKind.values().filter { it.isClock }) { WidgetCard(it, version) }
                item { SectionLabel("CALENDAR WIDGETS") }
                items(WidgetKind.values().filter { !it.isClock }) { WidgetCard(it, version) }
                item {
                    Section("REALME / COLOROS TIP") {
                        Text(
                            "Settings → Apps → App management → Pulse Widgets → Battery usage → Allow background activity, and enable Auto-launch. Without it ColorOS may delay the midnight date refresh.",
                            color = Lo, fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) =
    Text(text, color = Teal, fontFamily = Mono, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Teal, fontFamily = Mono, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Hi, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = value, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = Teal, checkedThumbColor = Bg)
        )
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Teal else Color.Transparent)
            .border(BorderStroke(1.dp, if (selected) Teal else Lo), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(label, color = if (selected) Bg else Hi, fontSize = 12.sp, fontFamily = Mono)
    }
}

@Composable
private fun WidgetCard(kind: WidgetKind, version: String) {
    val ctx = LocalContext.current
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(kind.title, color = Hi, fontFamily = Mono, fontSize = 13.sp, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { pin(ctx, kind) }, border = BorderStroke(1.dp, Teal)) {
                    Text("Add", color = Teal, fontFamily = Mono, fontSize = 12.sp)
                }
            }
            key(version) { WidgetPreview(kind) }
        }
    }
}

@Composable
private fun WidgetPreview(kind: WidgetKind) {
    val ctx = LocalContext.current
    AndroidView(
        modifier = Modifier.size((kind.cols * 72).dp, (kind.rows * 72).dp),
        factory = { FrameLayout(it) },
        update = { frame ->
            frame.removeAllViews()
            try {
                frame.addView(WidgetBuilder.build(ctx, kind, -1).apply(ctx, frame))
            } catch (_: Exception) {
            }
        }
    )
}

private fun pin(ctx: Context, kind: WidgetKind) {
    val mgr = AppWidgetManager.getInstance(ctx)
    if (mgr.isRequestPinAppWidgetSupported) {
        mgr.requestPinAppWidget(ComponentName(ctx, kind.provider), null, null)
    } else {
        Toast.makeText(ctx, "Long-press the home screen → Widgets → Pulse Widgets", Toast.LENGTH_LONG).show()
    }
}
