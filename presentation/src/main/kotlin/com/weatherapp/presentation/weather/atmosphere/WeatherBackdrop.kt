package com.weatherapp.presentation.weather.atmosphere

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.weatherapp.domain.model.WeatherCondition
import com.weatherapp.presentation.theme.Atmosphere
import com.weatherapp.presentation.theme.Motion
import com.weatherapp.presentation.theme.WeatherAtmospheres
import kotlin.random.Random
import androidx.compose.material3.MaterialTheme

@Composable
internal fun WeatherBackdrop(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < LUMINANCE_THRESHOLD
    val atmosphere = remember(condition, dark) { WeatherAtmospheres.forCondition(condition, dark) }
    val resumed = rememberResumed()
    val phase = if (resumed) animatedPhase() else 0f
    val rain = remember { seeds(Motion.RAIN_DROP_COUNT) }
    val snow = remember { seeds(Motion.SNOWFLAKE_COUNT) }
    Canvas(modifier) {
        drawRect(Brush.verticalGradient(listOf(atmosphere.top, atmosphere.bottom)))
        when (condition) {
            WeatherCondition.CLEAR -> drawSun(atmosphere, phase, withClouds = false)
            WeatherCondition.PARTLY_CLOUDY -> {
                drawSun(atmosphere, phase, withClouds = false)
                drawClouds(atmosphere, phase, count = 2)
            }
            WeatherCondition.CLOUDY -> drawClouds(atmosphere, phase, count = Motion.CLOUD_COUNT)
            WeatherCondition.RAIN -> {
                drawClouds(atmosphere, phase, count = 3)
                drawRain(atmosphere, phase, rain, heavy = false)
            }
            WeatherCondition.HEAVY_RAIN -> {
                drawClouds(atmosphere, phase, count = Motion.CLOUD_COUNT)
                drawRain(atmosphere, phase, rain, heavy = true)
            }
            WeatherCondition.THUNDERSTORM -> {
                drawClouds(atmosphere, phase, count = Motion.CLOUD_COUNT)
                drawRain(atmosphere, phase, rain, heavy = true)
                drawLightning(atmosphere, phase)
            }
            WeatherCondition.SNOW -> drawSnow(atmosphere, phase, snow)
            WeatherCondition.FOG -> drawFog(atmosphere, phase)
            WeatherCondition.UNKNOWN -> drawClouds(atmosphere, phase, count = 2)
        }
    }
}

@Composable
private fun animatedPhase(): Float {
    val transition = rememberInfiniteTransition(label = "weather-phase")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Motion.CYCLE_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )
    return phase
}

@Composable
private fun rememberResumed(): Boolean {
    val owner = LocalLifecycleOwner.current
    var resumed by androidx.compose.runtime.remember { mutableFloatStateOf(0f) }
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, _ ->
            resumed = if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) 1f else 0f
        }
        owner.lifecycle.addObserver(observer)
        resumed = if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) 1f else 0f
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    return resumed > 0f
}

private fun seeds(count: Int): List<ParticleSeed> = List(count) { index ->
    val random = Random(index * SEED_STEP)
    ParticleSeed(
        x = random.nextFloat(),
        speed = SPEED_MIN + random.nextFloat() * SPEED_SPAN,
        size = SIZE_MIN + random.nextFloat() * SIZE_SPAN,
        drift = random.nextFloat(),
    )
}

private fun DrawScope.drawSun(atmosphere: Atmosphere, phase: Float, withClouds: Boolean) {
    val center = Offset(size.width * SUN_X, size.height * SUN_Y)
    val radius = size.minDimension * SUN_RADIUS
    drawCircle(atmosphere.glow, radius = radius * GLOW_SCALE, center = center)
    drawCircle(atmosphere.accent, radius = radius, center = center)
    val rays = 8
    for (index in 0 until rays) {
        val angle = (index / rays.toFloat() + phase) * FULL_TURN
        val inner = radius * 1.35f
        val outer = radius * (1.9f + if (withClouds) 0f else 0.15f)
        val start = Offset(
            center.x + kotlin.math.cos(angle) * inner,
            center.y + kotlin.math.sin(angle) * inner,
        )
        val end = Offset(
            center.x + kotlin.math.cos(angle) * outer,
            center.y + kotlin.math.sin(angle) * outer,
        )
        drawLine(atmosphere.accent, start, end, strokeWidth = radius * 0.12f)
    }
}

private fun DrawScope.drawClouds(atmosphere: Atmosphere, phase: Float, count: Int) {
    repeat(count) { index ->
        val shift = ((phase + index * 0.17f) % 1f) * size.width
        val y = size.height * (0.12f + index * 0.08f)
        val width = size.width * (0.28f + index * 0.04f)
        drawCloud(Offset(shift - width, y), width, atmosphere.particle)
    }
}

private fun DrawScope.drawCloud(origin: Offset, width: Float, color: Color) {
    val height = width * 0.42f
    drawCircle(color, radius = height * 0.55f, center = origin + Offset(width * 0.3f, height * 0.55f))
    drawCircle(color, radius = height * 0.7f, center = origin + Offset(width * 0.52f, height * 0.42f))
    drawCircle(color, radius = height * 0.48f, center = origin + Offset(width * 0.74f, height * 0.58f))
    drawRoundRect(
        color = color,
        topLeft = origin + Offset(width * 0.18f, height * 0.5f),
        size = androidx.compose.ui.geometry.Size(width * 0.7f, height * 0.42f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(height),
    )
}

private fun DrawScope.drawRain(atmosphere: Atmosphere, phase: Float, seeds: List<ParticleSeed>, heavy: Boolean) {
    val length = if (heavy) size.height * 0.045f else size.height * 0.03f
    seeds.forEach { seed ->
        val progress = (phase * seed.speed + seed.drift) % 1f
        val x = seed.x * size.width
        val y = progress * (size.height + length) - length
        drawLine(
            color = atmosphere.particle,
            start = Offset(x, y),
            end = Offset(x - size.width * 0.012f, y + length),
            strokeWidth = if (heavy) 3f else 2f,
        )
    }
}

private fun DrawScope.drawSnow(atmosphere: Atmosphere, phase: Float, seeds: List<ParticleSeed>) {
    seeds.forEach { seed ->
        val progress = (phase * seed.speed * 0.55f + seed.drift) % 1f
        val sway = kotlin.math.sin((phase + seed.drift) * FULL_TURN) * size.width * 0.02f
        val x = seed.x * size.width + sway
        val y = progress * size.height
        drawCircle(atmosphere.particle, radius = seed.size * size.minDimension * 0.012f, center = Offset(x, y))
    }
}

private fun DrawScope.drawFog(atmosphere: Atmosphere, phase: Float) {
    repeat(3) { index ->
        val travel = ((phase + index * 0.2f) % 1f) * size.width * 0.25f
        drawRect(
            color = atmosphere.particle.copy(alpha = 0.18f + index * 0.06f),
            topLeft = Offset(-size.width * 0.1f + travel, size.height * (0.25f + index * 0.18f)),
            size = androidx.compose.ui.geometry.Size(size.width * 1.2f, size.height * 0.16f),
        )
    }
}

private fun DrawScope.drawLightning(atmosphere: Atmosphere, phase: Float) {
    val window = (phase * LIGHTNING_BEATS) % 1f
    if (window < LIGHTNING_WINDOW) {
        drawRect(atmosphere.glow.copy(alpha = 0.28f))
    }
}

private data class ParticleSeed(val x: Float, val speed: Float, val size: Float, val drift: Float)

private const val LUMINANCE_THRESHOLD = 0.5f
private const val SEED_STEP = 97
private const val SPEED_MIN = 0.65f
private const val SPEED_SPAN = 0.7f
private const val SIZE_MIN = 0.45f
private const val SIZE_SPAN = 1f
private const val SUN_X = 0.78f
private const val SUN_Y = 0.16f
private const val SUN_RADIUS = 0.07f
private const val GLOW_SCALE = 2.4f
private const val FULL_TURN = (Math.PI * 2).toFloat()
private const val LIGHTNING_BEATS = 3f
private const val LIGHTNING_WINDOW = 0.06f
