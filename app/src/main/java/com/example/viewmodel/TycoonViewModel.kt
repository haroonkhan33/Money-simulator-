package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Business
import com.example.model.DailyMission
import com.example.model.FloatingText
import com.example.model.MarketAsset
import com.example.model.Property
import com.example.model.TradeDirection
import com.example.model.TradePosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TycoonUiState(
    val cash: Double = 500.0,
    val totalTaps: Int = 0,
    val totalEarnedFromTaps: Double = 0.0,
    val totalEarnedFromBusinesses: Double = 0.0,
    val totalTradesCount: Int = 0,
    val isDeveloperAdmin: Boolean = false,
    val creditScore: Int = 680,
    val selectedTab: Int = 0 // 0: Businesses, 1: Trading, 2: Investments, 3: Settings
)

class TycoonViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("money_tycoon_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(TycoonUiState())
    val uiState: StateFlow<TycoonUiState> = _uiState.asStateFlow()

    private val _businesses = MutableStateFlow<List<Business>>(emptyList())
    val businesses: StateFlow<List<Business>> = _businesses.asStateFlow()

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _marketAssets = MutableStateFlow<List<MarketAsset>>(emptyList())
    val marketAssets: StateFlow<List<MarketAsset>> = _marketAssets.asStateFlow()

    private val _openPositions = MutableStateFlow<List<TradePosition>>(emptyList())
    val openPositions: StateFlow<List<TradePosition>> = _openPositions.asStateFlow()

    private val _dailyMissions = MutableStateFlow<List<DailyMission>>(emptyList())
    val dailyMissions: StateFlow<List<DailyMission>> = _dailyMissions.asStateFlow()

    private val _floatingTexts = MutableStateFlow<List<FloatingText>>(emptyList())
    val floatingTexts: StateFlow<List<FloatingText>> = _floatingTexts.asStateFlow()

    private val _selectedMarketAsset = MutableStateFlow<MarketAsset?>(null)
    val selectedMarketAsset: StateFlow<MarketAsset?> = _selectedMarketAsset.asStateFlow()

    private var floatingIdCounter = 0L

    init {
        loadInitialData()
        startGameLoop()
        startMarketSimulation()
    }

    private fun loadInitialData() {
        val savedCash = prefs.getFloat("cash", 500f).toDouble()
        val savedTaps = prefs.getInt("total_taps", 0)
        val savedAdmin = prefs.getBoolean("is_admin", false)

        _uiState.update {
            it.copy(
                cash = savedCash,
                totalTaps = savedTaps,
                isDeveloperAdmin = savedAdmin
            )
        }

        // Initialize Businesses
        val defaultBusinesses = listOf(
            Business("b1", "Cyber Lemonade & Cafe", "Retail", "☕", baseIncome = 4.0, baseCost = 15.0, managerCost = 250.0, cycleTimeSeconds = 1.0f, level = 1),
            Business("b2", "Autonomous Taxi Fleet", "Transport", "🚕", baseIncome = 24.0, baseCost = 200.0, managerCost = 1200.0, cycleTimeSeconds = 2.0f, level = 0),
            Business("b3", "Hardware & Local Store", "Commerce", "🏪", baseIncome = 90.0, baseCost = 1500.0, managerCost = 6000.0, cycleTimeSeconds = 3.0f, level = 0),
            Business("b4", "Crypto Mining Rig Farm", "FinTech", "⛏️", baseIncome = 380.0, baseCost = 8000.0, managerCost = 25000.0, cycleTimeSeconds = 4.0f, level = 0),
            Business("b5", "Neural Microchip Foundry", "Tech", "🔬", baseIncome = 1600.0, baseCost = 45000.0, managerCost = 150000.0, cycleTimeSeconds = 5.0f, level = 0),
            Business("b6", "Autonomous Cargo Freight", "Logistics", "🚢", baseIncome = 7500.0, baseCost = 250000.0, managerCost = 800000.0, cycleTimeSeconds = 6.0f, level = 0),
            Business("b7", "Clean Fusion Power Grid", "Energy", "⚡", baseIncome = 32000.0, baseCost = 1500000.0, managerCost = 5000000.0, cycleTimeSeconds = 8.0f, level = 0),
            Business("b8", "Orbital Space Logistics", "Aerospace", "🚀", baseIncome = 150000.0, baseCost = 10000000.0, managerCost = 35000000.0, cycleTimeSeconds = 10.0f, level = 0)
        )
        _businesses.value = defaultBusinesses

        // Initialize Real Estate & Luxury Properties
        val defaultProperties = listOf(
            Property("p1", "Suburban Family Villa", "Real Estate", 45000.0, 350.0, 20, "🏡"),
            Property("p2", "Cyberpunk Supercar", "Luxury Asset", 120000.0, 0.0, 15, "🏎️"),
            Property("p3", "Downtown Sky Penthouse", "Real Estate", 450000.0, 3200.0, 35, "🏙️"),
            Property("p4", "Ocean Explorer Mega Yacht", "Luxury Asset", 2500000.0, 0.0, 30, "🛥️"),
            Property("p5", "Beverly Hills Estate", "Real Estate", 6500000.0, 38000.0, 50, "🏰"),
            Property("p6", "Commercial Mega-Tower", "Real Estate", 35000000.0, 240000.0, 80, "🏢"),
            Property("p7", "Private Tropical Atoll", "Real Estate", 120000000.0, 950000.0, 100, "🏝️"),
            Property("p8", "Gulfstream Private Jet", "Luxury Asset", 45000000.0, 0.0, 40, "✈️")
        )
        _properties.value = defaultProperties

        // Initialize Market Assets
        val defaultMarkets = listOf(
            MarketAsset("BTC/USD", "Cyber Bitcoin", 68500.0, 68500.0, generateInitialHistory(68500.0)),
            MarketAsset("ETH/USD", "Quantum Ether", 3450.0, 3450.0, generateInitialHistory(3450.0)),
            MarketAsset("NVDA/USD", "Neural AI Corp", 132.50, 132.50, generateInitialHistory(132.50)),
            MarketAsset("GOLD/USD", "Digital Bullion", 2640.0, 2640.0, generateInitialHistory(2640.0))
        )
        _marketAssets.value = defaultMarkets
        _selectedMarketAsset.value = defaultMarkets.first()

        // Initialize Daily Missions
        val defaultMissions = listOf(
            DailyMission("m1", "Tapping Mogul", "Tap to earn 50 times", 50, currentProgress = 0, rewardCash = 1500.0),
            DailyMission("m2", "Market Maverick", "Execute 3 market trades", 3, currentProgress = 0, rewardCash = 5000.0),
            DailyMission("m3", "Empire Expander", "Upgrade any business 5 times", 5, currentProgress = 0, rewardCash = 8000.0),
            DailyMission("m4", "Automated Wealth", "Earn $10,000 from businesses", 10000, currentProgress = 0, rewardCash = 12000.0),
            DailyMission("m5", "Real Estate Baron", "Acquire at least 1 property", 1, currentProgress = 0, rewardCash = 25000.0)
        )
        _dailyMissions.value = defaultMissions
    }

    private fun generateInitialHistory(base: Double): List<Float> {
        val list = mutableListOf<Float>()
        var cur = base.toFloat()
        for (i in 0 until 24) {
            val delta = cur * (Random.nextFloat() * 0.04f - 0.02f)
            cur = (cur + delta).coerceAtLeast(base.toFloat() * 0.7f)
            list.add(cur)
        }
        return list
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                delay(200) // 5 ticks per second
                val dt = 0.2f
                var earnedInTick = 0.0

                _businesses.update { list ->
                    list.map { b ->
                        if (b.level > 0 && b.hasManager) {
                            val newProgress = b.currentCycleProgress + (dt / b.cycleTimeSeconds)
                            if (newProgress >= 1.0f) {
                                val cycles = newProgress.toInt()
                                val payout = b.incomePerCycle * cycles
                                earnedInTick += payout
                                b.copy(currentCycleProgress = newProgress - cycles)
                            } else {
                                b.copy(currentCycleProgress = newProgress)
                            }
                        } else {
                            b
                        }
                    }
                }

                if (earnedInTick > 0) {
                    addCash(earnedInTick)
                    _uiState.update { it.copy(totalEarnedFromBusinesses = it.totalEarnedFromBusinesses + earnedInTick) }
                    updateMissionProgress("m4", earnedInTick.toInt())
                }

                // Clean up expired floating texts
                val now = System.currentTimeMillis()
                _floatingTexts.update { list -> list.filter { now - it.createdAt < 900 } }
            }
        }
    }

    private fun startMarketSimulation() {
        viewModelScope.launch {
            while (true) {
                delay(1200)
                _marketAssets.update { list ->
                    list.map { asset ->
                        val volatility = 0.012f
                        val deltaPercent = (Random.nextFloat() * (volatility * 2) - volatility)
                        val newPrice = (asset.currentPrice * (1.0 + deltaPercent)).coerceAtLeast(1.0)
                        val newHistory = (asset.priceHistory + newPrice.toFloat()).takeLast(30)
                        val high = Math.max(asset.high24h, newPrice)
                        val low = Math.min(asset.low24h, newPrice)
                        val change = ((newPrice - asset.basePrice) / asset.basePrice) * 100.0

                        val updated = asset.copy(
                            currentPrice = newPrice,
                            priceHistory = newHistory,
                            high24h = high,
                            low24h = low,
                            change24h = change
                        )
                        if (_selectedMarketAsset.value?.symbol == asset.symbol) {
                            _selectedMarketAsset.value = updated
                        }
                        updated
                    }
                }
            }
        }
    }

    // Cash and Tap actions
    fun handleTap(x: Float, y: Float) {
        val tapMultiplier = 1.0 + (_businesses.value.sumOf { it.level } * 0.05)
        val tapEarnings = (25.0 * tapMultiplier).coerceAtLeast(25.0)

        addCash(tapEarnings)

        // Spawn floating text
        val newFloatingText = FloatingText(
            id = ++floatingIdCounter,
            text = "+$${formatCompact(tapEarnings)}",
            x = x,
            y = y
        )
        _floatingTexts.update { it + newFloatingText }

        _uiState.update {
            it.copy(
                totalTaps = it.totalTaps + 1,
                totalEarnedFromTaps = it.totalEarnedFromTaps + tapEarnings
            )
        }
        updateMissionProgress("m1", 1)
        saveState()
    }

    fun addCash(amount: Double) {
        _uiState.update { it.copy(cash = (it.cash + amount).coerceAtLeast(0.0)) }
    }

    fun spendCash(amount: Double): Boolean {
        if (_uiState.value.cash >= amount) {
            _uiState.update { it.copy(cash = it.cash - amount) }
            saveState()
            return true
        }
        return false
    }

    // Business Upgrades & Managers
    fun upgradeBusiness(businessId: String) {
        val business = _businesses.value.find { it.id == businessId } ?: return
        if (spendCash(business.currentCost)) {
            _businesses.update { list ->
                list.map {
                    if (it.id == businessId) it.copy(level = it.level + 1) else it
                }
            }
            updateMissionProgress("m3", 1)
            saveState()
        }
    }

    fun hireManager(businessId: String) {
        val business = _businesses.value.find { it.id == businessId } ?: return
        if (!business.hasManager && spendCash(business.managerCost)) {
            _businesses.update { list ->
                list.map {
                    if (it.id == businessId) it.copy(hasManager = true) else it
                }
            }
            saveState()
        }
    }

    fun manuallyCollectBusiness(businessId: String, x: Float, y: Float) {
        val business = _businesses.value.find { it.id == businessId } ?: return
        if (business.level <= 0) return
        val earnings = business.incomePerCycle
        addCash(earnings)

        val newFloatingText = FloatingText(
            id = ++floatingIdCounter,
            text = "+$${formatCompact(earnings)}",
            x = x,
            y = y
        )
        _floatingTexts.update { it + newFloatingText }
        _businesses.update { list ->
            list.map {
                if (it.id == businessId) it.copy(currentCycleProgress = 0f) else it
            }
        }
    }

    // Real Estate & Property Purchase
    fun purchaseProperty(propertyId: String) {
        val property = _properties.value.find { it.id == propertyId } ?: return
        if (!property.isPurchased && spendCash(property.price)) {
            _properties.update { list ->
                list.map {
                    if (it.id == propertyId) it.copy(isPurchased = true) else it
                }
            }
            _uiState.update { it.copy(creditScore = (it.creditScore + property.creditScoreBoost).coerceAtMost(850)) }
            updateMissionProgress("m5", 1)
            saveState()
        }
    }

    // Trading: Long / Short with Leverage
    fun selectMarketAsset(symbol: String) {
        _selectedMarketAsset.value = _marketAssets.value.find { it.symbol == symbol }
    }

    fun openTrade(direction: TradeDirection, marginAmount: Double, leverage: Int): Boolean {
        val asset = _selectedMarketAsset.value ?: return false
        if (marginAmount <= 0 || !spendCash(marginAmount)) return false

        val newPosition = TradePosition(
            assetSymbol = asset.symbol,
            direction = direction,
            entryPrice = asset.currentPrice,
            marginAmount = marginAmount,
            leverage = leverage
        )
        _openPositions.update { it + newPosition }
        _uiState.update { it.copy(totalTradesCount = it.totalTradesCount + 1) }
        updateMissionProgress("m2", 1)
        saveState()
        return true
    }

    fun closeTrade(positionId: String) {
        val position = _openPositions.value.find { it.id == positionId } ?: return
        val asset = _marketAssets.value.find { it.symbol == position.assetSymbol } ?: return
        val pnl = position.calculateUnrealizedPnl(asset.currentPrice)
        val returnAmount = (position.marginAmount + pnl).coerceAtLeast(0.0)

        addCash(returnAmount)
        _openPositions.update { it.filterNot { p -> p.id == positionId } }
        saveState()
    }

    // Missions
    private fun updateMissionProgress(missionId: String, increment: Int) {
        _dailyMissions.update { list ->
            list.map { mission ->
                if (mission.id == missionId && !mission.isClaimed) {
                    val newProgress = (mission.currentProgress + increment).coerceAtMost(mission.target)
                    mission.copy(currentProgress = newProgress)
                } else {
                    mission
                }
            }
        }
    }

    fun claimMissionReward(missionId: String) {
        val mission = _dailyMissions.value.find { it.id == missionId } ?: return
        if (mission.isCompleted && !mission.isClaimed) {
            addCash(mission.rewardCash)
            _dailyMissions.update { list ->
                list.map { if (it.id == missionId) it.copy(isClaimed = true) else it }
            }
            saveState()
        }
    }

    // Navigation
    fun setSelectedTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // Secret Admin Developer Code
    fun submitSecretAdminCode(code: String): Boolean {
        if (code.trim().equals("9ppp", ignoreCase = false)) {
            _uiState.update { it.copy(isDeveloperAdmin = true) }
            prefs.edit().putBoolean("is_admin", true).apply()
            return true
        }
        return false
    }

    // Admin Controls
    fun adminAddMillion() {
        addCash(1_000_000.0)
    }

    fun adminAddBillion() {
        addCash(1_000_000_000.0)
    }

    fun adminUnlockAllManagers() {
        _businesses.update { list -> list.map { it.copy(hasManager = true) } }
        saveState()
    }

    fun adminMaxAllBusinesses() {
        _businesses.update { list ->
            list.map { it.copy(level = it.level + 50, hasManager = true) }
        }
        saveState()
    }

    fun adminResetProgress() {
        prefs.edit().clear().apply()
        loadInitialData()
    }

    // Calculations
    val totalNetWorth: Double
        get() {
            val cash = _uiState.value.cash
            val businessValuation = _businesses.value.sumOf { it.level * it.baseCost * 1.5 }
            val realEstateValuation = _properties.value.filter { it.isPurchased }.sumOf { it.price }
            val tradingEquity = _openPositions.value.sumOf { pos ->
                val asset = _marketAssets.value.find { it.symbol == pos.assetSymbol }
                if (asset != null) pos.marginAmount + pos.calculateUnrealizedPnl(asset.currentPrice) else pos.marginAmount
            }
            return cash + businessValuation + realEstateValuation + tradingEquity
        }

    val totalCashFlowPerSec: Double
        get() {
            val businessSec = _businesses.value.filter { it.hasManager }.sumOf { it.incomePerSecond }
            val propertySec = _properties.value.filter { it.isPurchased }.sumOf { it.monthlyRentalIncome / 30.0 / 86400.0 }
            return businessSec + propertySec
        }

    val monthlyCashFlow: Double
        get() = totalCashFlowPerSec * 30.0 * 86400.0

    private fun saveState() {
        prefs.edit()
            .putFloat("cash", _uiState.value.cash.toFloat())
            .putInt("total_taps", _uiState.value.totalTaps)
            .putBoolean("is_admin", _uiState.value.isDeveloperAdmin)
            .apply()
    }

    companion object {
        fun formatCompact(amount: Double): String {
            return when {
                amount >= 1_000_000_000 -> String.format("%.2fB", amount / 1_000_000_000)
                amount >= 1_000_000 -> String.format("%.2fM", amount / 1_000_000)
                amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
                else -> String.format("%.0f", amount)
            }
        }
    }
}
