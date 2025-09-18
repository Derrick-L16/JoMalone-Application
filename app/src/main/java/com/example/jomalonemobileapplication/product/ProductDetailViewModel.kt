package com.example.jomalonemobileapplication.product
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jomalonemobileapplication.data.Perfume
import com.example.jomalonemobileapplication.repository.PerfumeRepository
import com.example.jomalonemobileapplication.auth.UserCloudDataRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.forEach

data class ProductDetailUiState(
    val perfume: Perfume? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val wishlistMessage: String? = null,
    val isInWishlist: Boolean = false
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val perfumeRepository: PerfumeRepository,
    private val userCloudDataRepository: UserCloudDataRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val PREFERENCE_ADJUSTMENT_VALUE = 1.0

    init {
        Log.i("ProductDetailVM_Init", "ViewModel Initialized.")
        // No test mode logging here
    }

    fun loadPerfume(perfumeId: String?) {
        if (perfumeId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Perfume ID is missing.", isLoading = false)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, wishlistMessage = null)
            try {
                val perfume = perfumeRepository.getPerfumeById(perfumeId)
                var isCurrentlyInWishlist = false
                val activeUserId = firebaseAuth.currentUser?.uid // Get current logged-in user

                if (activeUserId != null && perfume != null) {
                    // TODO: CRUCIAL - Implement actual check with your UserCloudDataRepository
                    // isCurrentlyInWishlist = userCloudDataRepository.isPerfumeInWishlist(activeUserId, perfume.id)
                    Log.d("ProductDetailVM", "loadPerfume: TODO - Check if perfume ${perfume.id} is in wishlist for user $activeUserId.")
                }

                _uiState.value = _uiState.value.copy(
                    perfume = perfume,
                    isLoading = false,
                    isInWishlist = isCurrentlyInWishlist
                )
            } catch (e: Exception) {
                Log.e("ProductDetailVM", "Error loading perfume $perfumeId: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error loading product: ${e.message}")
            }
        }
    }

    // This function can be called by the UI if it needs to refresh the perfume data.
    // For example, after an error or if the data might have changed server-side.
    fun refreshPerfume() {
        val currentPerfumeId = uiState.value.perfume?.id
        if (currentPerfumeId != null) {
            Log.d("ProductDetailVM", "Refreshing perfume details for ID: $currentPerfumeId")
            loadPerfume(currentPerfumeId)
        } else {
            // If there's no perfume ID (e.g., initial error before any perfume was loaded),
            // we can't refresh. The UI might need to provide the ID again or go back.
            // For now, if an error occurred before perfume ID was known, this won't do much.
            // The initial loadPerfume would be triggered by perfumeId from NavArgs.
            Log.w("ProductDetailVM", "Cannot refresh: Perfume ID not available in current state.")
            if (uiState.value.error != null) {
                // If there was an error, just clear it to potentially allow retry from UI if ID becomes known
                // _uiState.value = _uiState.value.copy(error = null, isLoading = false)
            }
        }
    }


    fun toggleWishlistAndUpdatePreferences() {
        val currentPerfume = _uiState.value.perfume
        if (currentPerfume == null) {
            _uiState.value = _uiState.value.copy(error = "Cannot toggle wishlist: Perfume not loaded.")
            return
        }
        val currentlyInWishlist = _uiState.value.isInWishlist

        if (currentlyInWishlist) {
            removePerfumeFromWishlistAndUpdatePreferences(currentPerfume)
        } else {
            addPerfumeToWishlistAndUpdatePreferences(currentPerfume)
        }
    }

    private fun addPerfumeToWishlistAndUpdatePreferences(perfume: Perfume) {
        Log.d("ProductDetailVM", "ADD ${perfume.name} to wishlist & update preferences.")
        val activeUserId = firebaseAuth.currentUser?.uid

        if (activeUserId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please log in to add to wishlist.", isLoading = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, wishlistMessage = null)
            var wishlistUpdatedSuccessfully = false
            var preferencesUpdatedSuccessfully = false
            try {
                // TODO: CRUCIAL - Add to wishlist in Firestore
                // wishlistUpdatedSuccessfully = userCloudDataRepository.addPerfumeToUserWishlist(activeUserId, perfume.id)
                wishlistUpdatedSuccessfully = true // Assume success for now
                Log.d("ProductDetailVM", "TODO: Call repository to ADD ${perfume.id} to wishlist for $activeUserId")

                val currentTastes = perfume.tastes
                if (!currentTastes.isNullOrEmpty()) {
                    val userDocument = userCloudDataRepository.getUserFromFirestore(activeUserId)
                    val updatedScores = userDocument?.scentPreferenceScores?.toMutableMap() ?: mutableMapOf()
                    currentTastes.forEach { taste ->
                        if (taste.isNotBlank()) {
                            val currentScore = updatedScores[taste.trim()] ?: 0.0
                            updatedScores[taste.trim()] = currentScore + PREFERENCE_ADJUSTMENT_VALUE
                        }
                    }
                    preferencesUpdatedSuccessfully = userCloudDataRepository.updateUserScentPreferences(activeUserId, updatedScores)
                } else {
                    preferencesUpdatedSuccessfully = true
                    Log.i("ProductDetailVM", "No tastes to update for ${perfume.name}")
                }

                if (wishlistUpdatedSuccessfully && preferencesUpdatedSuccessfully) {
                    _uiState.value = _uiState.value.copy(isLoading = false, wishlistMessage = "Added to wishlist & preferences increased!", isInWishlist = true)
                } else if (wishlistUpdatedSuccessfully) {
                    _uiState.value = _uiState.value.copy(isLoading = false, wishlistMessage = "Added to wishlist (preferences update failed).", isInWishlist = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to add to wishlist.", isInWishlist = false)
                }

            } catch (e: Exception) {
                Log.e("ProductDetailVM", "Error adding to wishlist for $activeUserId: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error: ${e.message}", isInWishlist = _uiState.value.isInWishlist)
            }
        }
    }

    private fun removePerfumeFromWishlistAndUpdatePreferences(perfume: Perfume) {
        Log.d("ProductDetailVM", "REMOVE ${perfume.name} from wishlist & update preferences.")
        val activeUserId = firebaseAuth.currentUser?.uid

        if (activeUserId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please log in to remove from wishlist.", isLoading = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, wishlistMessage = null)
            var wishlistUpdatedSuccessfully = false
            var preferencesUpdatedSuccessfully = false

            try {
                // TODO: CRUCIAL - Remove from wishlist in Firestore
                // wishlistUpdatedSuccessfully = userCloudDataRepository.removePerfumeFromUserWishlist(activeUserId, perfume.id)
                wishlistUpdatedSuccessfully = true // Assume success for now
                Log.d("ProductDetailVM", "TODO: Call repository to REMOVE ${perfume.id} from wishlist for $activeUserId")

                val currentTastes = perfume.tastes
                if (!currentTastes.isNullOrEmpty()) {
                    val userDocument = userCloudDataRepository.getUserFromFirestore(activeUserId)
                    if (userDocument != null) {
                        val updatedScores = userDocument.scentPreferenceScores?.toMutableMap() ?: mutableMapOf()
                        currentTastes.forEach { taste ->
                            if (taste.isNotBlank()) {
                                val currentScore = updatedScores[taste.trim()] ?: 0.0
                                updatedScores[taste.trim()] = (currentScore - PREFERENCE_ADJUSTMENT_VALUE).coerceAtLeast(0.0)
                            }
                        }
                        preferencesUpdatedSuccessfully = userCloudDataRepository.updateUserScentPreferences(activeUserId, updatedScores)
                    } else {
                        Log.w("ProductDetailVM", "Cannot get user document for $activeUserId to decrement scores.")
                        preferencesUpdatedSuccessfully = true
                    }
                } else {
                    preferencesUpdatedSuccessfully = true
                    Log.i("ProductDetailVM", "No tastes to update for ${perfume.name}")
                }

                if (wishlistUpdatedSuccessfully && preferencesUpdatedSuccessfully) {
                    _uiState.value = _uiState.value.copy(isLoading = false, wishlistMessage = "Removed from wishlist & preferences decreased!", isInWishlist = false)
                } else if (wishlistUpdatedSuccessfully) {
                    _uiState.value = _uiState.value.copy(isLoading = false, wishlistMessage = "Removed from wishlist (preferences update failed).", isInWishlist = false)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to remove from wishlist.", isInWishlist = true)
                }

            } catch (e: Exception) {
                Log.e("ProductDetailVM", "Error removing from wishlist for $activeUserId: ${e.message}", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Error: ${e.message}", isInWishlist = _uiState.value.isInWishlist)
            }
        }
    }

    // --- Functions to clear messages after they are shown by the UI ---
    fun clearWishlistMessage() {
        _uiState.value = _uiState.value.copy(wishlistMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    // --- End of clear message functions ---


    // setPerfume might be used if perfume details are passed directly via navigation arguments
    // and not just the ID. If only ID is passed, loadPerfume is primary.
    fun setPerfume(perfume: Perfume) {
        val activeUserId = firebaseAuth.currentUser?.uid
        var isCurrentlyInWishlist = false
        if (activeUserId != null) {
            // TODO: CRUCIAL - Implement actual check with your UserCloudDataRepository
            // isCurrentlyInWishlist = userCloudDataRepository.isPerfumeInWishlist(activeUserId, perfume.id)
            Log.d("ProductDetailVM", "setPerfume: TODO - Check if perfume ${perfume.id} is in wishlist for user $activeUserId.")
        }
        _uiState.value = _uiState.value.copy(
            perfume = perfume,
            isLoading = false, // Assuming if perfume object is passed, it's already "loaded"
            error = null,
            wishlistMessage = null,
            isInWishlist = isCurrentlyInWishlist
        )
    }
}

