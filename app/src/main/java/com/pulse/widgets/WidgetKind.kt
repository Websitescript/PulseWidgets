package com.pulse.widgets

import androidx.annotation.LayoutRes
import com.pulse.widgets.widgets.*

enum class Binder { NONE, YEAR, WEEK, MONTH }

enum class WidgetKind(
    val title: String,
    val isClock: Boolean,
    val cols: Int,
    val rows: Int,
    val binder: Binder,
    @LayoutRes val layout: Int,
    val provider: Class<*>,
) {
    CLOCK_ANALOG_1X1("Analog 1×1", true, 1, 1, Binder.NONE, R.layout.w_clock_analog_1x1, ClockAnalog1x1::class.java),
    CLOCK_STACK_1X2("Stacked 1×2", true, 1, 2, Binder.NONE, R.layout.w_clock_stack_1x2, ClockStack1x2::class.java),
    CLOCK_DIGITAL_2X1("Digital 2×1", true, 2, 1, Binder.NONE, R.layout.w_clock_digital_2x1, ClockDigital2x1::class.java),
    CLOCK_ANALOG_2X2("Analog 2×2", true, 2, 2, Binder.NONE, R.layout.w_clock_analog_2x2, ClockAnalog2x2::class.java),
    CLOCK_DIGITAL_3X1("Digital 3×1", true, 3, 1, Binder.NONE, R.layout.w_clock_digital_3x1, ClockDigital3x1::class.java),
    CLOCK_DIGITAL_3X2("Digital+Dial 3×2", true, 3, 2, Binder.NONE, R.layout.w_clock_digital_3x2, ClockDigital3x2::class.java),
    CLOCK_ANALOG_3X3("Analog 3×3", true, 3, 3, Binder.NONE, R.layout.w_clock_analog_3x3, ClockAnalog3x3::class.java),
    CLOCK_DIGITAL_4X1("Digital 4×1", true, 4, 1, Binder.NONE, R.layout.w_clock_digital_4x1, ClockDigital4x1::class.java),
    CLOCK_DIGITAL_4X2("Digital+Dial 4×2", true, 4, 2, Binder.NONE, R.layout.w_clock_digital_4x2, ClockDigital4x2::class.java),
    CLOCK_HERO_4X4("Hero 4×4", true, 4, 4, Binder.NONE, R.layout.w_clock_hero_4x4, ClockHero4x4::class.java),
    CLOCK_DIGITAL_5X1("Digital 5×1", true, 5, 1, Binder.NONE, R.layout.w_clock_digital_5x1, ClockDigital5x1::class.java),
    CLOCK_DIGITAL_5X2("Digital+Dial 5×2", true, 5, 2, Binder.NONE, R.layout.w_clock_digital_5x2, ClockDigital5x2::class.java),
    CLOCK_HERO_5X5("Hero 5×5", true, 5, 5, Binder.NONE, R.layout.w_clock_hero_5x5, ClockHero5x5::class.java),
    CAL_TILE_1X1("Tile 1×1", false, 1, 1, Binder.NONE, R.layout.w_cal_tile_1x1, CalTile1x1::class.java),
    CAL_STRIP_2X1("Strip 2×1", false, 2, 1, Binder.NONE, R.layout.w_cal_strip_2x1, CalStrip2x1::class.java),
    CAL_CARD_2X2("Card 2×2", false, 2, 2, Binder.YEAR, R.layout.w_cal_card_2x2, CalCard2x2::class.java),
    CAL_STRIP_3X1("Strip 3×1", false, 3, 1, Binder.NONE, R.layout.w_cal_strip_3x1, CalStrip3x1::class.java),
    CAL_CARDWIDE_3X2("Wide Card 3×2", false, 3, 2, Binder.YEAR, R.layout.w_cal_cardwide_3x2, CalCardwide3x2::class.java),
    CAL_CARD_3X3("Card 3×3", false, 3, 3, Binder.YEAR, R.layout.w_cal_card_3x3, CalCard3x3::class.java),
    CAL_MONTH_3X3("Month 3×3", false, 3, 3, Binder.MONTH, R.layout.w_cal_month_3x3, CalMonth3x3::class.java),
    CAL_WEEKBAR_4X1("Week Bar 4×1", false, 4, 1, Binder.WEEK, R.layout.w_cal_weekbar_4x1, CalWeekbar4x1::class.java),
    CAL_WEEK_4X2("Week 4×2", false, 4, 2, Binder.WEEK, R.layout.w_cal_week_4x2, CalWeek4x2::class.java),
    CAL_MONTH_4X4("Month 4×4", false, 4, 4, Binder.MONTH, R.layout.w_cal_month_4x4, CalMonth4x4::class.java),
    CAL_WEEKBAR_5X1("Week Bar 5×1", false, 5, 1, Binder.WEEK, R.layout.w_cal_weekbar_5x1, CalWeekbar5x1::class.java),
    CAL_WEEK_5X2("Week 5×2", false, 5, 2, Binder.WEEK, R.layout.w_cal_week_5x2, CalWeek5x2::class.java),
    CAL_MONTH_5X5("Month 5×5", false, 5, 5, Binder.MONTH, R.layout.w_cal_month_5x5, CalMonth5x5::class.java);
}
