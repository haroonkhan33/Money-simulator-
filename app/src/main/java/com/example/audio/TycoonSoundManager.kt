package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object TycoonSoundManager {

    var isSoundEnabled: Boolean = true

    private const val SAMPLE_RATE = 22050
    private val audioScope = CoroutineScope(Dispatchers.IO)
    private val soundCache = ConcurrentHashMap<String, ShortArray>()

    init {
        // Pre-generate static sounds in background so playback has zero latency
        audioScope.launch {
            cacheCommonSounds()
        }
    }

    private fun cacheCommonSounds() {
        soundCache["pop"] = generatePop()
        soundCache["cha_ching"] = generateChaChing()
        soundCache["upgrade"] = generateUpgrade()
        soundCache["manager"] = generateManagerHired()
        soundCache["property"] = generatePropertyUnlocked()
        soundCache["buy_long"] = generateBuyLong()
        soundCache["sell_short"] = generateSellShort()
        soundCache["loss"] = generateLoss()
        soundCache["tick"] = generateTick()
        soundCache["mission_completed"] = generateMissionCompleted()
        soundCache["claim_reward"] = generateClaimReward()
        soundCache["swipe"] = generateSwipe()
        soundCache["admin_granted"] = generateAdminGranted()
        soundCache["reset"] = generateProgressReset()
    }

    // 1. Business Asset Tap: Crisp light metallic coin click with pitch variance
    fun playCoinTap() {
        if (!isSoundEnabled) return
        audioScope.launch {
            // Vary pitch between 0.92 and 1.08 to prevent fatigue
            val pitchMultiplier = Random.nextDouble(0.92, 1.08).toFloat()
            val samples = generateCoinTap(pitchMultiplier)
            playPcm(samples)
        }
    }

    // 2. Floating Text Pop: Subtle soft "pop" or "whoosh" sound
    fun playFloatingTextPop() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["pop"] ?: generatePop()
            playPcm(samples)
        }
    }

    // 3. Passive Cash Collect: Fast "cha-ching" or electronic register sound
    fun playPassiveCashCollect() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["cha_ching"] ?: generateChaChing()
            playPcm(samples)
        }
    }

    // 4. Purchase / Upgrade Asset: Punchy mechanical construction + level-up chime
    fun playUpgradeAsset() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["upgrade"] ?: generateUpgrade()
            playPcm(samples)
        }
    }

    // 5. Manager Hired: Sharp page flip / rubber stamp sound
    fun playManagerHired() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["manager"] ?: generateManagerHired()
            playPcm(samples)
        }
    }

    // 6. Property / Car Unlocked: Luxury car engine rev / solid door-lock chime
    fun playPropertyUnlocked() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["property"] ?: generatePropertyUnlocked()
            playPcm(samples)
        }
    }

    // 7. Trade Executed (Buy/Long): Crisp button click + rising synth tone
    fun playTradeBuyLong() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["buy_long"] ?: generateBuyLong()
            playPcm(samples)
        }
    }

    // 8. Trade Executed (Sell/Short): Descending synth tone
    fun playTradeSellShort() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["sell_short"] ?: generateSellShort()
            playPcm(samples)
        }
    }

    // 9. Profit Realized: Rapid coin cascade scaling in duration
    fun playProfitRealized(profit: Double) {
        if (!isSoundEnabled) return
        audioScope.launch {
            val coinCount = when {
                profit >= 50000 -> 7
                profit >= 5000 -> 5
                else -> 4
            }
            val samples = generateCoinCascade(coinCount)
            playPcm(samples)
        }
    }

    // 10. Loss Taken: Low muted buzz or subtle thud sound
    fun playLossTaken() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["loss"] ?: generateLoss()
            playPcm(samples)
        }
    }

    // 11. Chart Interval Switch: Clean digital tick sound
    fun playChartIntervalSwitch() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["tick"] ?: generateTick()
            playPcm(samples)
        }
    }

    // 12. Mission Completed Notification: Subtle ding/chime
    fun playMissionCompleted() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["mission_completed"] ?: generateMissionCompleted()
            playPcm(samples)
        }
    }

    // 13. Claim Reward: Heavy rewarding chest-opening sound or double "cha-ching"
    fun playClaimReward() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["claim_reward"] ?: generateClaimReward()
            playPcm(samples)
        }
    }

    // 14. Screen Transition: Clean soft swipe/slide sound
    fun playScreenTransition() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["swipe"] ?: generateSwipe()
            playPcm(samples)
        }
    }

    // 15. Keypad Entry (Admin Code): Retro digital keypad beep
    fun playKeypadBeep(char: Char) {
        if (!isSoundEnabled) return
        audioScope.launch {
            val freq = when (char) {
                '9' -> 852.0
                'p', 'P' -> 1209.0
                else -> 770.0 + (char.code % 5) * 80.0
            }
            val samples = generateKeypadTone(freq)
            playPcm(samples)
        }
    }

    // 16. Admin Access Granted: Futuristic electronic chime
    fun playAdminGranted() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["admin_granted"] ?: generateAdminGranted()
            playPcm(samples)
        }
    }

    // 17. Progress Reset: Dramatic power-down / digital wipe
    fun playProgressReset() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val samples = soundCache["reset"] ?: generateProgressReset()
            playPcm(samples)
        }
    }

    // --- PROCEDURAL AUDIO SYNTHESIZERS ---

    private fun generateCoinTap(pitch: Float): ShortArray {
        val durationMs = 55
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)
        val f1 = 2600.0 * pitch
        val f2 = 3900.0 * pitch

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 80.0)
            val wave = 0.65 * sin(2.0 * PI * f1 * t) + 0.35 * sin(2.0 * PI * f2 * t)
            buffer[i] = (wave * env * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generatePop(): ShortArray {
        val durationMs = 60
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = i.toDouble() / numSamples
            val freq = 200.0 + 550.0 * (1.0 - normT)
            val env = sin(PI * normT)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * env * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateChaChing(): ShortArray {
        val durationMs = 280
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sample = 0.0

            // Part 1: First bright bell at t=0
            if (t < 0.14) {
                val env1 = exp(-t * 30.0)
                sample += 0.5 * sin(2.0 * PI * 1600.0 * t) * env1
                sample += 0.3 * sin(2.0 * PI * 3200.0 * t) * env1
            }
            // Part 2: Second higher bell at t=0.07
            if (t >= 0.06) {
                val t2 = t - 0.06
                val env2 = exp(-t2 * 22.0)
                sample += 0.6 * sin(2.0 * PI * 2400.0 * t2) * env2
                sample += 0.4 * sin(2.0 * PI * 4800.0 * t2) * env2
            }
            buffer[i] = (sample * 27000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateUpgrade(): ShortArray {
        val durationMs = 240
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var sample = 0.0

            // Hammer mechanical thud (first 50ms)
            if (t < 0.05) {
                val envThud = exp(-t * 90.0)
                val noise = Random.nextDouble(-0.3, 0.3)
                sample += (sin(2.0 * PI * 130.0 * t) + noise) * envThud * 0.7
            }
            // Rising level chime (C5 -> E5 -> G5)
            if (t >= 0.04) {
                val tChime = t - 0.04
                val noteIdx = (tChime / 0.06).toInt().coerceIn(0, 2)
                val freq = when (noteIdx) {
                    0 -> 523.25
                    1 -> 659.25
                    else -> 783.99
                }
                val localT = tChime - (noteIdx * 0.06)
                val envChime = exp(-localT * 35.0)
                sample += sin(2.0 * PI * freq * t) * envChime * 0.6
            }
            buffer[i] = (sample * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateManagerHired(): ShortArray {
        val durationMs = 110
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 45.0)
            val noise = Random.nextDouble(-0.5, 0.5)
            val thud = sin(2.0 * PI * 120.0 * t)
            val wave = 0.6 * noise + 0.4 * thud
            buffer[i] = (wave * env * 27000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generatePropertyUnlocked(): ShortArray {
        val durationMs = 350
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val engineFreq = 75.0 + (t / 0.35) * 160.0
            val engineWave = sin(2.0 * PI * engineFreq * t) * exp(-t * 5.0)

            // Door lock metallic chime at 0.12s
            var lockChime = 0.0
            if (t >= 0.12) {
                val tLock = t - 0.12
                lockChime = (sin(2.0 * PI * 1400.0 * tLock) + 0.5 * sin(2.0 * PI * 2800.0 * tLock)) * exp(-tLock * 30.0)
            }
            val sample = 0.5 * engineWave + 0.5 * lockChime
            buffer[i] = (sample * 27000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateBuyLong(): ShortArray {
        val durationMs = 140
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = i.toDouble() / numSamples
            val freq = 420.0 + (normT * 500.0) // sweeps 420Hz -> 920Hz
            val env = if (normT < 0.1) normT / 0.1 else exp(-(normT - 0.1) * 3.5)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * env * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateSellShort(): ShortArray {
        val durationMs = 140
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = i.toDouble() / numSamples
            val freq = 820.0 - (normT * 460.0) // sweeps 820Hz -> 360Hz
            val env = if (normT < 0.1) normT / 0.1 else exp(-(normT - 0.1) * 3.5)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * env * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateCoinCascade(count: Int): ShortArray {
        val durationMs = 80 * count + 120
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        val baseFreqs = doubleArrayOf(1400.0, 1750.0, 2100.0, 2450.0, 2800.0, 3150.0, 3500.0)

        for (coin in 0 until count) {
            val startSample = (coin * 0.075 * SAMPLE_RATE).toInt()
            val freq = baseFreqs[coin % baseFreqs.size]
            for (i in 0 until (0.12 * SAMPLE_RATE).toInt()) {
                val outIdx = startSample + i
                if (outIdx >= numSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-t * 35.0)
                val wave = sin(2.0 * PI * freq * t) * env * 0.4
                val curVal = buffer[outIdx].toInt()
                buffer[outIdx] = (curVal + (wave * 26000).toInt()).coerceIn(-32767, 32767).toShort()
            }
        }
        return buffer
    }

    private fun generateLoss(): ShortArray {
        val durationMs = 160
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 18.0)
            // Low square/saw buzz at 110Hz
            val cycle = (t * 110.0) % 1.0
            val wave = if (cycle < 0.5) 0.6 else -0.6
            buffer[i] = (wave * env * 24000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateTick(): ShortArray {
        val durationMs = 12
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 300.0)
            val wave = sin(2.0 * PI * 3600.0 * t)
            buffer[i] = (wave * env * 25000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateMissionCompleted(): ShortArray {
        val durationMs = 280
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 12.0)
            val bell = 0.6 * sin(2.0 * PI * 1046.5 * t) + 0.4 * sin(2.0 * PI * 1567.98 * t)
            buffer[i] = (bell * env * 25000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateClaimReward(): ShortArray {
        val durationMs = 380
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            // Low boom impact
            val boom = sin(2.0 * PI * 65.0 * t) * exp(-t * 12.0)
            // Sparkle chimes
            val sparkle1 = sin(2.0 * PI * 1568.0 * t) * exp(-t * 16.0)
            val sparkle2 = if (t > 0.08) sin(2.0 * PI * 2349.0 * (t - 0.08)) * exp(-(t - 0.08) * 14.0) else 0.0

            val sample = 0.45 * boom + 0.35 * sparkle1 + 0.35 * sparkle2
            buffer[i] = (sample * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateSwipe(): ShortArray {
        val durationMs = 45
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val normT = i.toDouble() / numSamples
            val env = sin(PI * normT)
            val noise = Random.nextDouble(-0.5, 0.5)
            buffer[i] = (noise * env * 18000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateKeypadTone(frequency: Double): ShortArray {
        val durationMs = 50
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = i.toDouble() / numSamples
            val env = sin(PI * normT)
            val wave = 0.7 * sin(2.0 * PI * frequency * t) + 0.3 * sin(2.0 * PI * (frequency * 1.5) * t)
            buffer[i] = (wave * env * 24000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateAdminGranted(): ShortArray {
        val durationMs = 320
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        val notes = doubleArrayOf(587.33, 739.99, 880.00, 1174.66)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val noteIdx = (t / 0.075).toInt().coerceIn(0, 3)
            val localT = t - (noteIdx * 0.075)
            val freq = notes[noteIdx]
            val env = exp(-localT * 25.0)
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * env * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    private fun generateProgressReset(): ShortArray {
        val durationMs = 400
        val numSamples = (SAMPLE_RATE * durationMs / 1000)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = i.toDouble() / numSamples
            val freq = 850.0 * (1.0 - normT * 0.9) // plunges down
            val env = 1.0 - normT
            val wave = sin(2.0 * PI * freq * t) + Random.nextDouble(-0.15, 0.15)
            buffer[i] = (wave * env * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }
        return buffer
    }

    // Play raw PCM 16-bit buffer with AudioTrack in MODE_STATIC
    private fun playPcm(samples: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()

            // Release AudioTrack after playback finishes
            val durationMs = (samples.size * 1000L / SAMPLE_RATE) + 60
            audioScope.launch {
                kotlinx.coroutines.delay(durationMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // AudioTrack allocation fallback
        }
    }
}
