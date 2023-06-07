package md.edi.mobilewaiter.presentation.settings.language

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import md.edi.mobilewaiter.presentation.settings.language.items.ItemRowCheck
import md.edi.mobilewaiter.presentation.settings.language.items.ItemRowCheckBinder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import md.edi.mobilewaiter.common.delegates.DelegateAdapterItem
import md.edi.mobilewaiter.common.styles.Text
import md.edi.mobilewaiter.data.datastore.SettingsRepository
import md.edi.mobilewaiter.utils.capitaliseWord
import java.util.*

class LanguageViewModel : ViewModel() {

    private val languages = listOf(
        Locale("ro"),
        Locale("ru"),
//        Locale("en")
    )

    fun setItems(context: Context): List<DelegateAdapterItem> {
        val items = mutableListOf<DelegateAdapterItem>()

        val current = runBlocking { SettingsRepository(context).getLanguage().first() }

        languages.forEach {
            items.add(
                ItemRowCheckBinder(
                    ItemRowCheck(
                        tag = it.language,
                        isChecked = current.language == it.language
                                || Locale.getDefault().language == it.language,
                        text = Text.Simple(it.getDisplayLanguage(it).capitaliseWord())
                    )
                )
            )
        }

        return items
    }


    fun setLanguage(locale: Locale, context: Context) {
        viewModelScope.launch {
            SettingsRepository(context).setLanguage(locale)
        }
    }
}