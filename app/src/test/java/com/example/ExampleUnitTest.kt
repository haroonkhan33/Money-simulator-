package com.example

import com.example.model.Business
import com.example.model.DailyMission
import com.example.model.TradeDirection
import com.example.model.TradePosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testTradePositionLongProfit() {
        val pos = TradePosition(
            assetSymbol = "BTC/USD",
            direction = TradeDirection.LONG,
            entryPrice = 60000.0,
            marginAmount = 1000.0,
            leverage = 2
        )
        // Price goes up 10% from 60K to 66K
        // With 2x leverage, gain should be 20% of 1000 = 200
        val pnl = pos.calculateUnrealizedPnl(66000.0)
        assertEquals(200.0, pnl, 0.01)
    }

    @Test
    fun testTradePositionShortProfit() {
        val pos = TradePosition(
            assetSymbol = "ETH/USD",
            direction = TradeDirection.SHORT,
            entryPrice = 3000.0,
            marginAmount = 1000.0,
            leverage = 5
        )
        // Price drops 10% from 3000 to 2700
        // With 5x leverage, gain should be 50% of 1000 = 500
        val pnl = pos.calculateUnrealizedPnl(2700.0)
        assertEquals(500.0, pnl, 0.01)
    }

    @Test
    fun testBusinessIncomeScaling() {
        val business = Business(
            id = "b1",
            name = "Test Business",
            category = "Tech",
            icon = "🔬",
            baseIncome = 10.0,
            baseCost = 100.0,
            managerCost = 500.0,
            cycleTimeSeconds = 2.0f,
            level = 1
        )
        assertTrue(business.isUnlocked)
        assertTrue(business.incomePerSecond > 0)
    }

    @Test
    fun testDailyMissionStatus() {
        val mission = DailyMission(
            id = "m1",
            title = "Tap 50 times",
            description = "Make 50 taps",
            target = 50,
            currentProgress = 50,
            rewardCash = 1000.0
        )
        assertTrue(mission.isCompleted)
        assertFalse(mission.isClaimed)
    }
}
