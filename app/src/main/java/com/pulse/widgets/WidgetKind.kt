package com.pulse.widgets

import androidx.annotation.LayoutRes
import com.pulse.widgets.widgets.*

enum class WidgetKind(
    val title: String,
    val isClock: Boolean,
    val cols: Int,
    val rows: Int,
    @LayoutRes val layout: Int,
    val provider: Class<*>,
) {
    CLOCK_ANALOG_1X1("Analog 1×1", true, 1, 1, R.layout.w_clock_analog_1x1, ClockAnalog1x1::class.java),
    CLOCK_STACK_1X2("Stacked 1×2", true, 1, 2, R.layout.w_clock_stack_1x2, ClockStack1x2::class.java),
    CLOCK_DIGITAL_2X1("Digital 2×1", true, 2, 1, R.layout.w_clock_digital_2x1, ClockDigital2x1::class.java),
    CLOCK_ANALOG_2X2("Analog 2×2", true, 2, 2, R.layout.w_clock_analog_2x2, ClockAnalog2x2::class.java),
    CLOCK_DIGITAL_4X2("Digital 4×2", true, 4, 2, R.layout.w_clock_digital_4x2, ClockDigital4x2::class.java),
    CLOCK_HERO_4X4("Hero 4×4", true, 4, 4, R.layout.w_clock_hero_4x4, ClockHero4x4::class.java),
    CAL_TILE_1X1("Tile 1×1", false, 1, 1, R.layout.w_cal_tile_1x1, CalTile1x1::class.java),
    CAL_STRIP_2X1("Strip 2×1", false, 2, 1, R.layout.w_cal_strip_2x1, CalStrip2x1::class.java),
    CAL_CARD_2X2("Card 2×2", false, 2, 2, R.layout.w_cal_card_2x2, CalCard2x2::class.java),
    CAL_WEEK_4X2("Week 4×2", false, 4, 2, R.layout.w_cal_week_4x2, CalWeek4x2::class.java),
    CAL_MONTH_4X4("Month 4×4", false, 4, 4, R.layout.w_cal_month_4x4, CalMonth4x4::class.java);
}
