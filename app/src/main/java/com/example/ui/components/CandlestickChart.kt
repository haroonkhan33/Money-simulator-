package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CandlestickChart(
    priceHistory: List<Float>,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true
) {
    val lineColor = if (isPositive) Color(0xFF00FF88) else Color(0xFFFF3B5C)
    val gradientColor = if (isPositive) Color(0x3300FF88) else Color(0x33FF3B5C)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0B1218))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (priceHistory.size < 2) return@Canvas

            val minPrice = priceHistory.minOrNull() ?: 0f
            val maxPrice = priceHistory.maxOrNull() ?: 1f
            val range = (maxPrice - minPrice).coerceAtLeast(0.001f)

            val width = size.width
            val height = size.height
            val padTop = 20f
            val padBottom = 20f
            val chartHeight = height - padTop - padBottom

            // Draw horizontal grid lines
            for (i in 1..3) {
                val y = padTop + (chartHeight / 4) * i
                drawLine(
                    color = Color(0xFF1B2A36),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val stepX = width / (priceHistory.size - 1)
            val points = priceHistory.mapIndexed { index, price ->
                val normY = 1f - ((price - minPrice) / range)
                Offset(index * stepX, padTop + normY * chartHeight)
            }

            // Draw area gradient
            val fillPath = Path().apply {
                moveTo(points.first().x, height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    startY = padTop,
                    endY = height
                )
            )

            // Draw smooth price curve
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val midX = (prev.x + curr.x) / 2
                    cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                }
            }

            drawPath(
                path = strokePath,
                color = lineColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw candlestick-like high/low bars at intervals
            val candleStep = (priceHistory.size / 6).coerceAtLeast(1)
            for (i in priceHistory.indices step candleStep) {
                val pt = points[i]
                val candleHeight = 16.dp.toPx()
                val isUp = (i == 0) || (priceHistory[i] >= priceHistory[i - 1])
                val candleColor = if (isUp) Color(0xFF00FF88) else Color(0xFFFF3B5C)

                // Wick
                drawLine(
                    color = candleColor,
                    start = Offset(pt.x, (pt.y - candleHeight).coerceAtLeast(padTop)),
                    end = Offset(pt.x, (pt.y + candleHeight).coerceAtMost(height - padBottom)),
                    strokeWidth = 1.dp.toPx()
                )
                // Body
                drawRect(
                    color = candleColor,
                    topLeft = Offset(pt.x - 3.dp.toPx(), pt.y - 6.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(6.dp.toPx(), 12.dp.toPx())
                )
            }

            // Highlight last price dot
            val lastPoint = points.last()
            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = lastPoint
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = lastPoint
            )
        }
    }
}
