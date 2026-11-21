package com.pulse.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var scroll: ScrollView
    private lateinit var content: LinearLayout
    private var cityDraft = ""

    private val match = ViewGroup.LayoutParams.MATCH_PARENT
    private val wrap = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Scheduler.schedule(this)
        scroll = ScrollView(this)
        scroll.setBackgroundColor(getColor(R.color.app_bg))
        content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(16), dp(16), dp(16), dp(40))
        scroll.addView(content, ViewGroup.LayoutParams(match, wrap))
        setContentView(scroll)
        render()
        WidgetUpdater.refreshWeatherThenUpdate(this, null) { runOnUiThread { render() } }
    }

    override fun onResume() {
        super.onResume()
        WidgetUpdater.updateAll(this)
    }

    // ---------------------------------------------------------------- small helpers
    private fun dp(v: Int): Int = (v * resources.displayMetrics.density + 0.5f).toInt()
    private fun col(res: Int): Int = getColor(res)

    private fun label(text: String, sp: Float, color: Int, bold: Boolean = false, mono: Boolean = false): TextView {
        val t = TextView(this)
        t.text = text
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        t.setTextColor(color)
        if (mono) t.typeface = Typeface.create(Typeface.MONOSPACE, if (bold) Typeface.BOLD else Typeface.NORMAL)
        else if (bold) t.setTypeface(t.typeface, Typeface.BOLD)
        return t
    }

    private fun roundBg(color: Int, radiusDp: Int, strokeColor: Int? = null): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(radiusDp).toFloat()
        if (strokeColor != null) g.setStroke(dp(1), strokeColor)
        return g
    }

    private fun card(title: String): LinearLayout {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(16), dp(14), dp(16), dp(16))
        box.background = roundBg(col(R.color.app_card), 18, col(R.color.app_line))
        box.addView(label(title.uppercase(), 11f, col(R.color.app_accent), bold = true, mono = true))
        val lp = LinearLayout.LayoutParams(match, wrap)
        lp.topMargin = dp(12)
        content.addView(box, lp)
        return box
    }

    private fun addTo(parent: LinearLayout, v: View, topDp: Int = 10) {
        val lp = LinearLayout.LayoutParams(match, wrap)
        lp.topMargin = dp(topDp)
        parent.addView(v, lp)
    }

    private fun chips(options: List<String>, selected: Int, dots: List<Int>?, onPick: (Int) -> Unit): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        for (i in options.indices) {
            val sel = i == selected
            val t = TextView(this)
            t.text = options[i]
            t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            t.setTextColor(if (sel) col(R.color.app_on_accent) else col(R.color.app_text))
            t.gravity = Gravity.CENTER
            t.setPadding(dp(14), dp(9), dp(14), dp(9))
            t.background = roundBg(if (sel) col(R.color.app_accent) else col(R.color.app_chip), 20)
            if (dots != null) {
                val dot = GradientDrawable()
                dot.shape = GradientDrawable.OVAL
                dot.setColor(dots[i])
                dot.setSize(dp(12), dp(12))
                t.setCompoundDrawablesRelativeWithIntrinsicBounds(dot, null, null, null)
                t.compoundDrawablePadding = dp(8)
            }
            t.setOnClickListener { onPick(i) }
            val lp = LinearLayout.LayoutParams(wrap, wrap)
            lp.rightMargin = dp(8)
            row.addView(t, lp)
        }
        val hs = HorizontalScrollView(this)
        hs.isHorizontalScrollBarEnabled = false
        hs.addView(row)
        return hs
    }

    private fun switchRow(parent: LinearLayout, text: String, checked: Boolean, onChange: (Boolean) -> Unit) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.addView(label(text, 14f, col(R.color.app_text)), LinearLayout.LayoutParams(0, wrap, 1f))
        val sw = Switch(this)
        sw.isChecked = checked
        sw.setOnCheckedChangeListener { _, v -> onChange(v) }
        row.addView(sw)
        addTo(parent, row, 12)
    }

    private fun slider(parent: LinearLayout, title: String, min: Int, max: Int, value: Int, onDone: (Int) -> Unit) {
        val pct = label("$title: $value%", 13f, col(R.color.app_text))
        addTo(parent, pct, 14)
        val sb = SeekBar(this)
        sb.max = max - min
        sb.progress = value - min
        sb.progressTintList = ColorStateList.valueOf(col(R.color.app_accent))
        sb.thumbTintList = ColorStateList.valueOf(col(R.color.app_accent))
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                pct.text = "$title: ${p + min}%"
            }

            override fun onStartTrackingTouch(s: SeekBar?) {}

            override fun onStopTrackingTouch(s: SeekBar?) {
                onDone((s?.progress ?: 0) + min)
            }
        })
        addTo(parent, sb, 2)
    }

    private fun changed() {
        WidgetUpdater.updateAll(this)
        render()
    }

    // ---------------------------------------------------------------- screen
    private fun render() {
        val keepY = scroll.scrollY
        content.removeAllViews()

        // header
        val head = LinearLayout(this)
        head.orientation = LinearLayout.HORIZONTAL
        head.gravity = Gravity.CENTER_VERTICAL
        val logo = ImageView(this)
        logo.setImageResource(R.mipmap.ic_launcher)
        head.addView(logo, LinearLayout.LayoutParams(dp(60), dp(60)))
        val titles = LinearLayout(this)
        titles.orientation = LinearLayout.VERTICAL
        titles.setPadding(dp(14), 0, 0, 0)
        titles.addView(label("Pulse Widgets", 22f, col(R.color.app_text), bold = true))
        titles.addView(label("Clock, day, weather and calendar widgets that adapt to their size", 12f, col(R.color.app_sub)))
        head.addView(titles, LinearLayout.LayoutParams(0, wrap, 1f))
        content.addView(head, LinearLayout.LayoutParams(match, wrap))

        colorCard()
        backgroundCard()
        clockCard()
        weatherCard()
        weekCard()
        for (k in WidgetKind.values()) previewCard(k)

        scroll.post { scroll.scrollTo(0, keepY) }
    }

    private fun colorCard() {
        val box = card("Colors")
        val names = ArrayList<String>()
        val dots = ArrayList<Int>()
        names.add("System")
        dots.add(col(R.color.w_accent))
        names.add("Wallpaper")
        dots.add(0xFF9AA5B1.toInt())
        for (p in Palettes.ALL) {
            names.add(p.label)
            dots.add(p.darkAccent)
        }
        addTo(box, chips(names, Prefs.source(this), dots) { Prefs.setSource(this, it); changed() }, 12)
        val hint = when (Prefs.source(this)) {
            Prefs.SRC_SYSTEM -> "Follows your phone's theme: light/dark and Material You colors."
            Prefs.SRC_WALLPAPER -> "Colors are taken from your current wallpaper. They refresh about every 15 minutes, or when you open this app."
            else -> "A fixed color style. It still switches between light and dark."
        }
        addTo(box, label(hint, 11f, col(R.color.app_sub)), 8)
        addTo(box, label("Appearance", 12f, col(R.color.app_sub), bold = true), 14)
        addTo(box, chips(listOf("System", "Light", "Dark"), Prefs.appearance(this), null) { Prefs.setAppearance(this, it); changed() }, 6)
    }

    private fun backgroundCard() {
        val box = card("Background")
        addTo(box, chips(listOf("Solid", "Gradient", "Frosted glass"), Prefs.bgStyle(this), null) { Prefs.setBgStyle(this, it); changed() }, 12)
        val style = Prefs.bgStyle(this)
        if (style == 1) {
            switchRow(box, "Animate the gradient", Prefs.animGradient(this)) { Prefs.setAnimGradient(this, it); changed() }
        }
        if (style == 2) {
            slider(box, "Glass opacity", 15, 90, Prefs.glassOpacity(this)) { Prefs.setGlassOpacity(this, it); changed() }
            addTo(
                box,
                label("Android does not let widgets blur the wallpaper behind them, so this is a frosted-glass look: a translucent tint with soft light blobs and a glass edge. Lower opacity means more see-through.", 11f, col(R.color.app_sub)),
                8
            )
        } else {
            slider(box, "Opacity", 30, 100, Prefs.opacity(this)) { Prefs.setOpacity(this, it); changed() }
        }
    }

    private fun clockCard() {
        val box = card("Analog clock")
        switchRow(box, "Smooth second hand", Prefs.smoothHand(this)) { Prefs.setSmoothHand(this, it); changed() }
        val ok = Smooth.supported(this)
        addTo(
            box,
            label(
                if (ok) "Supported on this phone: the second hand sweeps at 30 fps (uses a little more battery)."
                else "This phone's system does not allow a smooth sweep in widgets, so the second hand ticks once per second.",
                11f, col(R.color.app_sub)
            ),
            8
        )
    }

    private fun weatherCard() {
        val box = card("Weather")
        val d = Weather.cached(this)
        val status = if (d == null) Prefs.city(this) + " · loading..." else {
            val dsc = Weather.describe(d.code, d.day)
            Prefs.city(this) + " · " + Weather.fmt(this, d.temp) + " " + dsc.second
        }
        addTo(box, label(status, 15f, col(R.color.app_text)), 10)
        addTo(box, label("Shown in widgets from 2×2 up (analog from 4×2). Data from open-meteo.com.", 11f, col(R.color.app_sub)), 4)

        val input = EditText(this)
        input.hint = "Search a city"
        input.setText(cityDraft)
        input.setSingleLine(true)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setTextColor(col(R.color.app_text))
        input.setHintTextColor(col(R.color.app_sub))
        addTo(box, input, 10)
        val setBtn = TextView(this)
        setBtn.text = "Set city"
        setBtn.setTextColor(col(R.color.app_on_accent))
        setBtn.gravity = Gravity.CENTER
        setBtn.setPadding(dp(16), dp(10), dp(16), dp(10))
        setBtn.background = roundBg(col(R.color.app_accent), 20)
        setBtn.setOnClickListener {
            val q = input.text.toString().trim()
            cityDraft = q
            if (q.isNotEmpty()) searchCity(q)
        }
        addTo(box, setBtn, 8)
        addTo(box, chips(listOf("°C", "°F"), if (Prefs.fahrenheit(this)) 1 else 0, null) { Prefs.setFahrenheit(this, it == 1); changed() }, 12)
    }

    private fun searchCity(q: String) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show()
        Thread {
            val place = Weather.geocode(q)
            if (place == null) {
                runOnUiThread { Toast.makeText(this, "City not found (or no internet)", Toast.LENGTH_LONG).show() }
            } else {
                Prefs.setPlace(this, place.name, place.lat, place.lon)
                Weather.refresh(this)
                cityDraft = ""
                runOnUiThread { changed() }
            }
        }.start()
    }

    private fun weekCard() {
        val box = card("Week starts on")
        addTo(box, chips(listOf("Locale", "Sunday", "Monday"), Prefs.weekStart(this), null) { Prefs.setWeekStart(this, it); changed() }, 12)
    }

    // ---------------------------------------------------------------- live previews
    private fun previewCard(kind: WidgetKind) {
        val box = card(kind.title)
        val add = TextView(this)
        add.text = "Add to home screen"
        add.setTextColor(col(R.color.app_on_accent))
        add.gravity = Gravity.CENTER
        add.setPadding(dp(16), dp(10), dp(16), dp(10))
        add.background = roundBg(col(R.color.app_accent), 20)
        add.setOnClickListener { pin(kind) }
        addTo(box, add, 10)

        val sw = resources.configuration.screenWidthDp
        val cell = (sw - 32) / 4f
        val sizes = listOf(Pair(1, 1), Pair(2, 1), Pair(2, 2), Pair(4, 2), Pair(4, 4))
        for (s in sizes) {
            val wDp = minOf((cell * s.first - 12).toInt(), sw - 64)
            val hDp = (cell * 1.15f * s.second - 12).toInt()
            addTo(box, label("${s.first}×${s.second}", 11f, col(R.color.app_sub), mono = true), 14)
            val frame = FrameLayout(this)
            try {
                frame.addView(WidgetBuilder.build(this, kind, wDp, hDp, -1).apply(this, frame))
            } catch (e: Exception) {
                // preview only
            }
            val lp = LinearLayout.LayoutParams(dp(wDp), dp(hDp))
            lp.topMargin = dp(6)
            box.addView(frame, lp)
        }
    }

    private fun pin(kind: WidgetKind) {
        val mgr = AppWidgetManager.getInstance(this)
        if (mgr.isRequestPinAppWidgetSupported) {
            mgr.requestPinAppWidget(ComponentName(this, kind.provider), null, null)
        } else {
            Toast.makeText(this, "Long-press the home screen, then Widgets, then Pulse Widgets", Toast.LENGTH_LONG).show()
        }
    }
}
