package com.tecfort.navigationtest.data.repository

import com.tecfort.navigationtest.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * In-memory [ItemRepository] implementation. Swap this out for a network- or
 * database-backed implementation without touching any callers of [ItemRepository].
 */
class DefaultItemRepository @Inject constructor() : ItemRepository {

    override fun getItems(): Flow<List<Item>> {
        val items = List(ITEM_COUNT) { index -> Item(id = index, title = "This is item number $index") }
        return flowOf(items)
    }

    private companion object {
        const val ITEM_COUNT = 50
    }
}
