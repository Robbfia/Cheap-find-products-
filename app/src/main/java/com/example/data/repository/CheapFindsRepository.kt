package com.example.data.repository

import com.example.data.model.AffiliateStats
import com.example.data.model.GeneratedPromoKit
import com.example.data.model.Product
import com.example.data.model.SellerBoostCampaign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CheapFindsRepository {

    private val _products = MutableStateFlow<List<Product>>(getInitialProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _savedKits = MutableStateFlow<List<GeneratedPromoKit>>(getInitialPromoKits())
    val savedKits: StateFlow<List<GeneratedPromoKit>> = _savedKits.asStateFlow()

    private val _affiliateStats = MutableStateFlow(AffiliateStats())
    val affiliateStats: StateFlow<AffiliateStats> = _affiliateStats.asStateFlow()

    private val _sellerCampaigns = MutableStateFlow<List<SellerBoostCampaign>>(getInitialSellerCampaigns())
    val sellerCampaigns: StateFlow<List<SellerBoostCampaign>> = _sellerCampaigns.asStateFlow()

    private val _affiliateTag = MutableStateFlow("george_finds")
    val affiliateTag: StateFlow<String> = _affiliateTag.asStateFlow()

    fun updateAffiliateTag(newTag: String) {
        _affiliateTag.value = newTag.trim()
    }

    fun recordLinkClick(kitId: String? = null) {
        val current = _affiliateStats.value
        val newClicks = current.totalClicks + 1
        // Simulate realistic occasional conversion
        val newOrders = if (newClicks % 12 == 0) current.convertedOrders + 1 else current.convertedOrders
        val newEarned = if (newClicks % 12 == 0) current.totalCommissionEarned + 125.50 else current.totalCommissionEarned
        _affiliateStats.value = current.copy(
            totalClicks = newClicks,
            convertedOrders = newOrders,
            totalCommissionEarned = newEarned
        )
    }

    fun generatePromoKit(
        rawUrl: String,
        customTitle: String? = null,
        customPrice: Double? = null,
        tone: String = "Viral UGC",
        targetPlatform: String = "TikTok"
    ): GeneratedPromoKit {
        val cleanUrl = if (rawUrl.startsWith("http")) rawUrl else "https://$rawUrl"
        val currentTag = _affiliateTag.value.ifBlank { "george_finds" }

        // Find if this URL matches an existing product in our catalog
        val matchedProduct = _products.value.find { cleanUrl.contains(it.id, ignoreCase = true) || it.productUrl.contains(cleanUrl) }

        val title = customTitle?.takeIf { it.isNotBlank() }
            ?: matchedProduct?.title
            ?: parseTitleFromUrl(cleanUrl)

        val price = customPrice ?: matchedProduct?.salePrice ?: 499.0
        val commissionRate = matchedProduct?.commissionPercent ?: 15
        val estCommission = price * (commissionRate / 100.0)

        // Generate deep affiliate link with sub_id and tracking parameters
        val affiliateUrl = buildAffiliateUrl(cleanUrl, currentTag, targetPlatform)

        // Script generation logic tailored to the selected tone & product
        val (hook, visual, voiceover, cta, caption, hashtags) = generateScriptContent(
            title = title,
            price = price,
            tone = tone,
            platform = targetPlatform,
            affiliateUrl = affiliateUrl
        )

        val kit = GeneratedPromoKit(
            id = UUID.randomUUID().toString(),
            productTitle = title,
            productPrice = price,
            commissionEstimate = estCommission,
            originalUrl = cleanUrl,
            affiliateUrl = affiliateUrl,
            affiliateTag = currentTag,
            scriptHook = hook,
            scriptVisualScene = visual,
            scriptVoiceover = voiceover,
            scriptCta = cta,
            socialCaption = caption,
            hashtags = hashtags,
            tone = tone,
            targetPlatform = targetPlatform
        )

        // Auto save to repository
        _savedKits.value = listOf(kit) + _savedKits.value
        return kit
    }

    fun toggleFavoriteKit(kitId: String) {
        _savedKits.value = _savedKits.value.map {
            if (it.id == kitId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun deleteKit(kitId: String) {
        _savedKits.value = _savedKits.value.filter { it.id != kitId }
    }

    private fun buildAffiliateUrl(originalUrl: String, affTag: String, platform: String): String {
        val separator = if (originalUrl.contains("?")) "&" else "?"
        val subId = platform.lowercase().replace(" ", "_")
        return "$originalUrl${separator}aff_id=$affTag&sub_id=$subId&utm_source=cheapfinds"
    }

    private fun parseTitleFromUrl(url: String): String {
        return try {
            val parts = url.substringAfter("://").split("/")
            val lastPart = parts.lastOrNull { it.isNotBlank() && !it.contains("?") } ?: "Affordable Marketplace Gem"
            val sanitized = lastPart.replace("-", " ").replace("_", " ")
                .replace(Regex("[0-9]{5,}"), "")
                .trim()
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            sanitized.ifBlank { "Affordable Viral Product" }
        } catch (e: Exception) {
            "Affordable Marketplace Find"
        }
    }

    private fun generateScriptContent(
        title: String,
        price: Double,
        tone: String,
        platform: String,
        affiliateUrl: String
    ): ScriptBundle {
        val formattedPrice = "₱${String.format("%.2f", price)}"

        return when (tone) {
            "Shocking Deal / Budol" -> {
                ScriptBundle(
                    hook = "🚨 STOP SCROLLING! I literally can't believe this $title is only $formattedPrice right now!",
                    visualScene = "Hold product close to camera with shocked face, cut to split-screen showing the original price vs sale price.",
                    voiceover = "I found the ultimate hack for getting top quality on a shoestring budget. This $title usually costs 3x more, but they put up an unlisted voucher. Tested it all week and the build quality is insane.",
                    cta = "Grab the voucher link before the seller notices the price glitch! Tap the yellow basket or link in bio.",
                    caption = "Sulit find alert! 😱 Got this $title for only $formattedPrice instead of normal mall price. Link in bio before voucher expires! 🛒✨",
                    hashtags = listOf("#BudolFinds", "#ShopeeBudol", "#CheapFinds", "#AffordableGems", "#SulitDeals", "#TikTokMadeMeBuyIt")
                )
            }
            "Problem-Solver" -> {
                ScriptBundle(
                    hook = "If you've been struggling with finding a reliable $title that won't break your bank, watch this.",
                    visualScene = "Show common everyday frustration, then reveal the $title cleanly opening from the box and solving the issue.",
                    voiceover = "Most brands charge thousands for this, but this hidden gem does the exact same job for just $formattedPrice. The reviews weren't lying—5 stars well deserved.",
                    cta = "Save this video for your payday haul or check the affiliate link in my bio/comments!",
                    caption = "Here's the honest review you needed for $title ($formattedPrice). Worth every single peso! Link in bio. 💯📦",
                    hashtags = listOf("#ProductReview", "#LifeHack", "#CheapFinds", "#SmartShopping", "#AffiliateCreator")
                )
            }
            "Aesthetic / Minimalist" -> {
                ScriptBundle(
                    hook = "POV: Finding the cleanest $title under ₱1,000 for your everyday routine. ✨",
                    visualScene = "Aesthetic B-roll lighting, slow-motion pan over texture and details, calm music cue.",
                    voiceover = "Quality doesn't have to be expensive. Here is an honest look at this minimalist $title. Only $formattedPrice on sale today.",
                    cta = "Comment 'LINK' and I'll send it directly, or click the link in bio to shop.",
                    caption = "Minimalist vibes on an affordable budget. $title at $formattedPrice ✨ Direct link in my bio! 🏷️",
                    hashtags = listOf("#AestheticFinds", "#AffordableLuxury", "#CheapFinds", "#DailyEssentials", "#TikTokShopFinds")
                )
            }
            else -> { // Viral UGC default
                ScriptBundle(
                    hook = "You won't believe what arrived in the mail today! Best $formattedPrice spent this month! 🔥",
                    visualScene = "Fast-paced unboxing, tapping microphone for ASMR box sound, testing key features side by side.",
                    voiceover = "Everyone on TikTok was raving about this $title, so I bought it to see if the hype was real. Verdict? 10/10. Definitely adding this to my daily favorites.",
                    cta = "Link is right here in my profile bio! Don't miss out while stock is available.",
                    caption = "Found this trending $title for only $formattedPrice! Totally worth the hype. Tap link below! 🛍️👇",
                    hashtags = listOf("#TikTokMadeMeBuyIt", "#CheapFinds", "#ViralGadgets", "#AffiliateHacks", "#Unboxing")
                )
            }
        }
    }

    private data class ScriptBundle(
        val hook: String,
        val visualScene: String,
        val voiceover: String,
        val cta: String,
        val caption: String,
        val hashtags: List<String>
    )

    private fun getInitialProducts(): List<Product> = listOf(
        Product(
            id = "A96-4G-PH",
            title = "A96 Dual SIM 8GB RAM 4G Smartphone",
            originalPrice = 7999.0,
            salePrice = 5599.0,
            commissionPercent = 14,
            marketplace = "Lazada",
            category = "Electronics",
            brand = "Xiaomi",
            rating = 4.9f,
            reviewCount = 3840,
            couponCode = "CHEAP30",
            salesCount = "12.8k sold",
            productUrl = "https://lazada.com.ph/products/a96-dual-sim-8gb-ram",
            tagline = "Extra 30% OFF with voucher code CHEAP30",
            isTrending = true,
            isSellerBoosted = true
        ),
        Product(
            id = "HM-OVR-TEE",
            title = "Heavyweight Oversized Boxy Graphic Tee",
            originalPrice = 999.0,
            salePrice = 399.0,
            commissionPercent = 20,
            marketplace = "Shopee",
            category = "Fashion",
            brand = "HM",
            rating = 4.8f,
            reviewCount = 1920,
            couponCode = "HM20OFF",
            salesCount = "8.4k sold",
            productUrl = "https://shopee.ph/products/hm-oversized-boxy-tee",
            tagline = "Viral TikTok streetwear staple at 60% discount",
            isTrending = true,
            isSellerBoosted = false
        ),
        Product(
            id = "ANK-MINI-PB",
            title = "Ultra-Compact 10,000mAh 22.5W Fast Charger",
            originalPrice = 1499.0,
            salePrice = 649.0,
            commissionPercent = 16,
            marketplace = "TikTok Shop",
            category = "Gadgets",
            brand = "Anker",
            rating = 4.9f,
            reviewCount = 4210,
            couponCode = "POWER20",
            salesCount = "24.5k sold",
            productUrl = "https://shop.tiktok.com/view/product/anker-compact-powerbank",
            tagline = "Pocket-sized airplane approved magnetic battery",
            isTrending = true,
            isSellerBoosted = true
        ),
        Product(
            id = "NK-RUN-SNEAK",
            title = "Breathable Lightweight Cushion Sport Runners",
            originalPrice = 3290.0,
            salePrice = 1499.0,
            commissionPercent = 12,
            marketplace = "Lazada",
            category = "Fashion",
            brand = "Nike",
            rating = 4.7f,
            reviewCount = 980,
            couponCode = "NIKEFLASH",
            salesCount = "3.1k sold",
            productUrl = "https://lazada.com.ph/products/nike-cushion-sport-runners",
            tagline = "Weekend flash deal with official mall store warranty",
            isTrending = false,
            isSellerBoosted = false
        ),
        Product(
            id = "RGB-DESK-BAR",
            title = "Smart Monitor Light Bar with Wireless Puck",
            originalPrice = 1899.0,
            salePrice = 799.0,
            commissionPercent = 18,
            marketplace = "Shopee",
            category = "Home",
            brand = "Baseus",
            rating = 4.8f,
            reviewCount = 1530,
            couponCode = "DESKSETUP",
            salesCount = "6.9k sold",
            productUrl = "https://shopee.ph/products/baseus-rgb-monitor-light-bar",
            tagline = "Eye-care asymmetric lighting for aesthetic desk setups",
            isTrending = true,
            isSellerBoosted = true
        ),
        Product(
            id = "LIP-TINT-MATTE",
            title = "Velvet Matte Water Tint Non-Drying Lip Stain",
            originalPrice = 450.0,
            salePrice = 199.0,
            commissionPercent = 25,
            marketplace = "TikTok Shop",
            category = "Beauty",
            brand = "BeautyGlow",
            rating = 4.9f,
            reviewCount = 6840,
            couponCode = "GLOWUP",
            salesCount = "45.1k sold",
            productUrl = "https://shop.tiktok.com/view/product/velvet-matte-water-tint",
            tagline = "Top viral TikTok beauty budol in Southeast Asia",
            isTrending = true,
            isSellerBoosted = true
        )
    )

    private fun getInitialPromoKits(): List<GeneratedPromoKit> = listOf(
        GeneratedPromoKit(
            id = "kit-1",
            productTitle = "A96 Dual SIM 8GB RAM 4G Smartphone",
            productPrice = 5599.0,
            commissionEstimate = 783.86,
            originalUrl = "https://lazada.com.ph/products/a96-dual-sim-8gb-ram",
            affiliateUrl = "https://lazada.com.ph/products/a96-dual-sim-8gb-ram?aff_id=george_finds&sub_id=tiktok_ugc",
            affiliateTag = "george_finds",
            scriptHook = "🚨 STOP SCROLLING! The A96 8GB RAM smartphone is ₱5,599 with the unlisted coupon!",
            scriptVisualScene = "Hold smartphone up to show smooth 90Hz scrolling, benchmark test, then show voucher code.",
            scriptVoiceover = "If you need a secondary gaming phone or daily driver under ₱6,000, this is the best value in 2026. Use voucher code CHEAP30 for an extra discount.",
            scriptCta = "Yellow basket is pinned below before promo stock clears!",
            socialCaption = "Budget phone king! 📱 A96 with 8GB RAM & Dual SIM down to ₱5,599 with voucher CHEAP30! Link in bio. ⚡",
            hashtags = listOf("#CheapFinds", "#SmartphoneDeal", "#BudolFinds", "#TechReview", "#LazadaFinds"),
            tone = "Viral UGC",
            isFavorite = true,
            targetPlatform = "TikTok"
        ),
        GeneratedPromoKit(
            id = "kit-2",
            productTitle = "Ultra-Compact 10,000mAh 22.5W Fast Charger",
            productPrice = 649.0,
            commissionEstimate = 103.84,
            originalUrl = "https://shop.tiktok.com/view/product/anker-compact-powerbank",
            affiliateUrl = "https://shop.tiktok.com/view/product/anker-compact-powerbank?aff_id=george_finds&sub_id=fb_group",
            affiliateTag = "george_finds",
            scriptHook = "Never run out of phone battery on a commute again with this pocket beast.",
            scriptVisualScene = "Drop battery into pocket, connect fast charging wire, show phone jump from 10% to 50% quickly.",
            scriptVoiceover = "This is smaller than a credit card and charges your phone 3 times over. 22.5W fast charge, airline-safe. Under ₱650 right now.",
            scriptCta = "Direct link in the comments and bio for free shipping!",
            socialCaption = "Best travel gadget under ₱700! 🔋 Anker 10,000mAh mini powerbank. Link in bio! ✈️",
            hashtags = listOf("#LifeHack", "#TravelEssentials", "#Anker", "#TechDeals", "#CheapFinds"),
            tone = "Problem-Solver",
            isFavorite = false,
            targetPlatform = "Facebook"
        )
    )

    private fun getInitialSellerCampaigns(): List<SellerBoostCampaign> = listOf(
        SellerBoostCampaign(
            id = "boost-1",
            sellerName = "Xiaomi Official Flagship",
            productTitle = "A96 Dual SIM 8GB RAM 4G Smartphone",
            bonusCommissionRate = 10,
            totalCommissionRate = 24,
            bountyReward = "₱1,000 bonus cash for videos over 25k views",
            budgetRemaining = "₱45,000",
            slotsAvailable = 14,
            productUrl = "https://lazada.com.ph/products/a96-dual-sim-8gb-ram",
            category = "Electronics"
        ),
        SellerBoostCampaign(
            id = "boost-2",
            sellerName = "GlowLab Skincare",
            productTitle = "Velvet Matte Water Tint Non-Drying Lip Stain",
            bonusCommissionRate = 15,
            totalCommissionRate = 35,
            bountyReward = "₱500 bonus + Free PR Sample for creators > 5k followers",
            budgetRemaining = "₱20,000",
            slotsAvailable = 28,
            productUrl = "https://shop.tiktok.com/view/product/velvet-matte-water-tint",
            category = "Beauty"
        ),
        SellerBoostCampaign(
            id = "boost-3",
            sellerName = "Baseus Digital Mall",
            productTitle = "Smart Monitor Light Bar with Wireless Puck",
            bonusCommissionRate = 8,
            totalCommissionRate = 26,
            bountyReward = "₱750 bonus for top 10 creator conversions",
            budgetRemaining = "₱32,000",
            slotsAvailable = 8,
            productUrl = "https://shopee.ph/products/baseus-rgb-monitor-light-bar",
            category = "Home"
        )
    )
}
