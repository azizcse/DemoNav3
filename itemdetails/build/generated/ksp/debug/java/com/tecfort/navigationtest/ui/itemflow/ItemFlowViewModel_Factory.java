package com.tecfort.navigationtest.ui.itemflow;

import com.tecfort.navigationtest.data.repository.ItemRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ItemFlowViewModel_Factory implements Factory<ItemFlowViewModel> {
  private final Provider<ItemRepository> repositoryProvider;

  private ItemFlowViewModel_Factory(Provider<ItemRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ItemFlowViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ItemFlowViewModel_Factory create(Provider<ItemRepository> repositoryProvider) {
    return new ItemFlowViewModel_Factory(repositoryProvider);
  }

  public static ItemFlowViewModel newInstance(ItemRepository repository) {
    return new ItemFlowViewModel(repository);
  }
}
