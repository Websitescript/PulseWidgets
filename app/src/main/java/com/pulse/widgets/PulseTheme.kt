package com.pulse.widgets

import android.graphics.Color

enum class PulseTheme(
    val key: String,
    val label: String,
    accentHex: String,
    primaryHex: String,
    secondaryHex: String,
    onAccentHex: String,
    val bgBase: Int,
    val bgAlt: Int,
    val circle: Int,
    val halo: Int,
    val ringId: Int,
    val barId: Int,
) {
    TEAL("teal", "Terminal Teal", "#2DD4BF", "#E6FFFB", "#86BDB6", "#04201D",
        R.drawable.bg_teal_a, R.drawable.bg_teal_b, R.drawable.circle_teal, R.drawable.halo_teal, R.id.ring_teal, R.id.yp_teal),
    CORAL("coral", "Coral", "#FF6B5B", "#FFF0ED", "#D79C94", "#2A0A07",
        R.drawable.bg_coral_a, R.drawable.bg_coral_b, R.drawable.circle_coral, R.drawable.halo_coral, R.id.ring_coral, R.id.yp_coral),
    GOLD("gold", "Gold", "#F5C451", "#FFF7E0", "#C8B07A", "#2B2004",
        R.drawable.bg_gold_a, R.drawable.bg_gold_b, R.drawable.circle_gold, R.drawable.halo_gold, R.id.ring_gold, R.id.yp_gold),
    AURORA("aurora", "Aurora", "#7DF9D0", "#F1EEFF", "#A9A5D8", "#0B1B2B",
        R.drawable.bg_aurora_a, R.drawable.bg_aurora_b, R.drawable.circle_aurora, R.drawable.halo_aurora, R.id.ring_aurora, R.id.yp_aurora);

    val accent: Int = Color.parseColor(accentHex)
    val primary: Int = Color.parseColor(primaryHex)
    val secondary: Int = Color.parseColor(secondaryHex)
    val onAccent: Int = Color.parseColor(onAccentHex)
    val dim: Int = (secondary and 0x00FFFFFF) or 0x80000000.toInt()

    companion object {
        fun fromKey(key: String?): PulseTheme = values().firstOrNull { it.key == key } ?: TEAL
    }
}
