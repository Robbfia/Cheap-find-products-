package com.example.data.model

data class Product(
    val id: String,
    val title: String,
    val originalPrice: Double,
    val salePrice: Double,
    val commissionPercent: Int,
    val marketplace: String,
    val category: String,
    val brand: String,
    val rating: Float,
    val reviewCount: Int,
    val couponCode: String? = null,
    val salesCount: String,
    val productUrl: String,
    val tagline: String,
    val isTrending: Boolean = false,
    val isSellerBoosted: Boolean = false
) {
    val discountPercent: Int
        get() = if (originalPrice > 0) (((originalPrice - salePrice) / originalPrice) * 100).toInt() else 0

    val estimatedCommission: Double
        get() = salePrice * (commissionPercent / 100.0)
}

data class GeneratedPromoKit(
    val id: String,
    val productTitle: String,
    val productPrice: Double,
    val commissionEstimate: Double,
    val originalUrl: String,
    val affiliateUrl: String,
    val affiliateTag: String,
    val scriptHook: String,
    val scriptVisualScene: String,
    val scriptVoiceover: String,
    val scriptCta: String,
    val socialCaption: String,
    val hashtags: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val tone: String = "Viral UGC",
    val targetPlatform: String = "TikTok"
)

data class AffiliateStats(
    val totalClicks: Int = 1842,
    val convertedOrders: Int = 146,
    val totalCommissionEarned: Double = 8740.50,
    val pendingCommission: Double = 2310.00,
    val topPerformingProduct: String = "A96 Dual SIM 8GB RAM 4G Smartphone",
    val currencySymbol: String = "₱"
)

data class SellerBoostCampaign(
    val id: String,
    val sellerName: String,
    val productTitle: String,
    val bonusCommissionRate: Int, // e.g. +12%
    val totalCommissionRate: Int, // e.g. 24%
    val bountyReward: String, // e.g. "₱500 bonus per 10k video views"
    val budgetRemaining: String,
    val slotsAvailable: Int,
    val productUrl: String,
    val category: String,
    val verifiedSeller: Boolean = true
)
