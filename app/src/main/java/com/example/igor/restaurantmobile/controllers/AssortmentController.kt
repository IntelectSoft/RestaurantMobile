package com.example.igor.restaurantmobile.controllers

import com.example.igor.restaurantmobile.common.delegates.DelegateAdapterItem
import com.example.igor.restaurantmobile.data.remote.response.assortment.*
import com.example.igor.restaurantmobile.presentation.table.items.ItemTable
import com.example.igor.restaurantmobile.presentation.table.items.ItemTableBinder
import kotlinx.coroutines.flow.MutableSharedFlow


object AssortmentController {
    var assortmentBody: AssortmentListResponse? = null

    fun getClosureTypes(): List<ClosureTypeItem> = assortmentBody?.ClosureTypeList ?: emptyList()
    fun getTables(): List<TableItem> = assortmentBody?.TableList ?: emptyList()
    fun getComments(): List<CommentItem> = assortmentBody?.CommentsList ?: emptyList()

    fun getAssortmentNameById(id: String): String = assortmentBody?.AssortimentList?.find { it.Uid == id }?.Name ?: "Not found"

    fun getParentsDefault(): List<AssortmentItem> {
        val mutableList = assortmentBody?.AssortimentList?.let {
            it.toMutableList().filter { item ->
                item.ParentUid == "00000000-0000-0000-0000-000000000000"
            }
        }
        return mutableList ?: emptyList()
    }

    fun getChildrenByParentId(parentId: String): List<AssortmentItem> {
        val mutableList = assortmentBody?.AssortimentList?.let {
            it.toMutableList().filter { item ->
                item.ParentUid == parentId
            }
        }
        return mutableList ?: emptyList()
    }

    fun getTableNumberById(tableId: String): String {
        return assortmentBody?.TableList?.let {
            it.toMutableList().find {
                it.Uid == tableId
            }
        }?.Name ?: "-"
    }

    fun getTablesDelegate() : List<DelegateAdapterItem> {
        val tables = mutableListOf<DelegateAdapterItem>()
        assortmentBody?.TableList?.toMutableList()?.forEach {
            tables.add(
                ItemTableBinder(
                    ItemTable(
                        tag = "table",
                        id = it.Uid,
                        name = it.Name
                    )
                )
            )
        }
        return tables
    }
}