package com.tecfort.navigationtest.di

import com.tecfort.navigationtest.data.repository.DefaultItemRepository
import com.tecfort.navigationtest.data.repository.ItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindItemRepository(impl: DefaultItemRepository): ItemRepository
}
