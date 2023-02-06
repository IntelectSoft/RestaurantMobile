package md.edi.mobilewaiter.presentation.assortment.viewmodels

import androidx.lifecycle.ViewModel
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.controllers.AssortmentController
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.data.repo.RepositoryServiceImpl
import md.edi.mobilewaiter.presentation.assortment.items.ItemAssortment
import md.edi.mobilewaiter.presentation.assortment.items.ItemAssortmentBinder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AssortmentViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    var lastParentFolder = "00000000-0000-0000-0000-000000000000"
    val listUidNavigation = mutableListOf<String>()
    val assortmentList = MutableStateFlow<List<DelegateAdapterItem>>(emptyList())
    val assortmentChildList = MutableStateFlow<List<DelegateAdapterItem>>(emptyList())

    suspend fun getDefaultAssortment() {
        val assortment = mutableListOf<DelegateAdapterItem>()
        AssortmentController.getParentsDefault().toMutableList()
            .forEach {
                assortment.add(
                    ItemAssortmentBinder(
                        ItemAssortment(
                            tag = "assortment",
                            assortment = it
                        )
                    )
                )
            }

        assortmentList.emit(assortment)
    }

    suspend fun getChildAssortment(parentId: String, superParentUid: String? = null) {
        val assortment = mutableListOf<DelegateAdapterItem>()
        if (superParentUid != null)
            listUidNavigation.add(superParentUid)
        AssortmentController.getChildrenByParentId(parentId).toMutableList()
            .forEach {
                assortment.add(
                    ItemAssortmentBinder(
                        ItemAssortment(
                            tag = "assortment",
                            assortment = it
                        )
                    )
                )
            }

        assortmentChildList.emit(assortment)
    }

    suspend fun searchAssortment(text: String){
        val assortment = mutableListOf<DelegateAdapterItem>()

        AssortmentController.searchAssortmentByName(text).toMutableList()
            .forEach {
                assortment.add(
                    ItemAssortmentBinder(
                        ItemAssortment(
                            tag = "assortment",
                            assortment = it
                        )
                    )
                )
            }
        assortmentChildList.emit(assortment)
    }

    suspend fun getBackPage(): Boolean {
        if (listUidNavigation.size == 0) {
            return true
        } else {
            val assortment = mutableListOf<DelegateAdapterItem>()
            AssortmentController.getChildrenByParentId(listUidNavigation.last()).toMutableList()
                .forEach {
                    assortment.add(
                        ItemAssortmentBinder(
                            ItemAssortment(
                                tag = "assortment",
                                assortment = it
                            )
                        )
                    )
                }

            assortmentChildList.emit(assortment)

            listUidNavigation.remove(listUidNavigation.last())
            return false
        }
    }

}