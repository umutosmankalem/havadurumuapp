package com.weatherapp.data.locale

import com.weatherapp.domain.repository.LanguageProvider
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLanguageProvider @Inject constructor() : LanguageProvider {
    override fun languageCode(): String = Locale.getDefault().language.ifBlank { "en" }
}
