package com.tiation.rigger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _isDungeonMaster = MutableStateFlow(false)
    val isDungeonMaster: StateFlow<Boolean> = _isDungeonMaster

    private val _currentTheme = MutableStateFlow(Theme.HERO)
    val currentTheme: StateFlow<Theme> = _currentTheme

    private val _selectedNavItem = MutableStateFlow(0)
    val selectedNavItem: StateFlow<Int> = _selectedNavItem

    fun setRole(isDM: Boolean) {
        viewModelScope.launch {
            _isDungeonMaster.value = isDM
            _currentTheme.value = if (isDM) Theme.DUNGEON_MASTER else Theme.HERO
        }
    }

    fun setNavItem(index: Int) {
        viewModelScope.launch {
            _selectedNavItem.value = index
        }
    }
}

enum class Theme {
    HERO,
    DUNGEON_MASTER
}
