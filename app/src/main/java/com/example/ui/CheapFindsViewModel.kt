package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AffiliateStats
import com.example.data.model.GeneratedPromoKit
import com.example.data.model.Product
import com.example.data.model.SellerBoostCampaign
import com.example.data.repository.CheapFindsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    DISCOVER,
    GENERATOR,
    SAVED_KITS,
    ANALYTICS,
    SELLER_BOOST
}

class CheapFindsViewModel(
    private val repository: CheapFindsRepository = CheapFindsRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(ScreenTab.DISCOVER)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedBrand = MutableStateFlow<String?>(null)
    val selectedBrand: StateFlow<String?> = _selectedBrand.asStateFlow()

    val affiliateTag: StateFlow<String> = repository.affiliateTag
    val affiliateStats: StateFlow<AffiliateStats> = repository.affiliateStats
    val savedKits: StateFlow<List<GeneratedPromoKit>> = repository.savedKits
    val sellerCampaigns: StateFlow<List<SellerBoostCampaign>> = repository.sellerCampaigns

    // Filtered products list
    val filteredProducts: StateFlow<List<Product>> = combine(
        repository.products,
        _searchQuery,
        _selectedCategory,
        _selectedBrand
    ) { products, query, category, brand ->
        products.filter { product ->
            val matchesQuery = query.isBlank() ||
                    product.title.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true) ||
                    product.tagline.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true) ||
                    (product.couponCode?.contains(query, ignoreCase = true) == true)

            val matchesCategory = when (category) {
                "All" -> true
                "Top Commission" -> product.commissionPercent >= 16
                else -> product.category.equals(category, ignoreCase = true)
            }

            val matchesBrand = brand == null || product.brand.equals(brand, ignoreCase = true)

            matchesQuery && matchesCategory && matchesBrand
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Kit Dialog / Teleprompter
    private val _activeKitDialog = MutableStateFlow<GeneratedPromoKit?>(null)
    val activeKitDialog: StateFlow<GeneratedPromoKit?> = _activeKitDialog.asStateFlow()

    // Generator Screen Form States
    private val _generatorInputUrl = MutableStateFlow("")
    val generatorInputUrl: StateFlow<String> = _generatorInputUrl.asStateFlow()

    private val _generatorCustomTitle = MutableStateFlow("")
    val generatorCustomTitle: StateFlow<String> = _generatorCustomTitle.asStateFlow()

    private val _generatorCustomPrice = MutableStateFlow("")
    val generatorCustomPrice: StateFlow<String> = _generatorCustomPrice.asStateFlow()

    private val _generatorSelectedTone = MutableStateFlow("Viral UGC")
    val generatorSelectedTone: StateFlow<String> = _generatorSelectedTone.asStateFlow()

    private val _generatorSelectedPlatform = MutableStateFlow("TikTok")
    val generatorSelectedPlatform: StateFlow<String> = _generatorSelectedPlatform.asStateFlow()

    // Snackbar event message
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onBrandSelected(brand: String?) {
        _selectedBrand.value = if (_selectedBrand.value == brand) null else brand
    }

    fun updateAffiliateTag(newTag: String) {
        repository.updateAffiliateTag(newTag)
        emitMessage("Affiliate ID updated to: $newTag")
    }

    fun openKitDetail(kit: GeneratedPromoKit) {
        _activeKitDialog.value = kit
    }

    fun closeKitDetail() {
        _activeKitDialog.value = null
    }

    fun updateGeneratorUrl(url: String) {
        _generatorInputUrl.value = url
    }

    fun updateGeneratorTitle(title: String) {
        _generatorCustomTitle.value = title
    }

    fun updateGeneratorPrice(price: String) {
        _generatorCustomPrice.value = price
    }

    fun updateGeneratorTone(tone: String) {
        _generatorSelectedTone.value = tone
    }

    fun updateGeneratorPlatform(platform: String) {
        _generatorSelectedPlatform.value = platform
    }

    fun quickPromoteProduct(product: Product) {
        _generatorInputUrl.value = product.productUrl
        _generatorCustomTitle.value = product.title
        _generatorCustomPrice.value = product.salePrice.toString()
        _currentTab.value = ScreenTab.GENERATOR
    }

    fun triggerGeneration() {
        val url = _generatorInputUrl.value.trim()
        if (url.isBlank()) {
            emitMessage("Please paste a product URL first!")
            return
        }

        val price = _generatorCustomPrice.value.toDoubleOrNull()
        val title = _generatorCustomTitle.value.takeIf { it.isNotBlank() }

        val kit = repository.generatePromoKit(
            rawUrl = url,
            customTitle = title,
            customPrice = price,
            tone = _generatorSelectedTone.value,
            targetPlatform = _generatorSelectedPlatform.value
        )

        _activeKitDialog.value = kit
        emitMessage("✨ Magic Promo Kit generated successfully!")
    }

    fun toggleFavoriteKit(kitId: String) {
        repository.toggleFavoriteKit(kitId)
    }

    fun deleteKit(kitId: String) {
        repository.deleteKit(kitId)
        if (_activeKitDialog.value?.id == kitId) {
            _activeKitDialog.value = null
        }
        emitMessage("Campaign kit deleted")
    }

    fun recordClick(kitId: String? = null) {
        repository.recordLinkClick(kitId)
    }

    fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }
}
