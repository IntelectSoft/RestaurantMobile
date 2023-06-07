package md.edi.mobilewaiter.presentation.settings.language

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.common.delegates.CompositeAdapter
import md.edi.mobilewaiter.databinding.ActivityLanguagesBinding
import md.edi.mobilewaiter.presentation.settings.language.items.ItemRowCheckDelegate
import java.util.*


class ActivityLanguage : AppCompatActivity() {

    val viewModel: LanguageViewModel by viewModels()

    private val compositeAdapter by lazy {
        CompositeAdapter.Builder()
            .add(ItemRowCheckDelegate { lang ->
                viewModel.setLanguage(Locale(lang), this@ActivityLanguage)
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                AppCompatDelegate.setApplicationLocales(appLocale)
            })

            .build()
    }

    val binding by lazy {
        ActivityLanguagesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()

        compositeAdapter.submitList(viewModel.setItems(this))


        binding.toolbar.setTitle(getString(R.string.application_language))
        binding.toolbar.showBottomLine(true)

        binding.toolbar.showLeftBtn(true)
        binding.toolbar.setLeftIcon(R.drawable.ic_arrow_back) {
            finish()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = compositeAdapter
    }
}