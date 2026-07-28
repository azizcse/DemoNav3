package com.tecfort.navigationtest.data.repository

import com.tecfort.navigationtest.data.model.Item
import kotlinx.coroutines.flow.Flow

/**
 * Source of truth for the items shown on the list screen.
 */
interface ItemRepository {
    fun getItems(): Flow<List<Item>>
}
