package com.weatherapp.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.domain.model.City
import com.weatherapp.presentation.R
import com.weatherapp.presentation.components.LoadingSkeleton
import com.weatherapp.presentation.components.MessageState
import com.weatherapp.presentation.message.text
import com.weatherapp.presentation.theme.Dimens
import com.weatherapp.presentation.weather.placeLabel

@Composable
fun SearchScreen(
    onCitySelected: (City) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.gap),
        ) {
            Text(stringResource(R.string.nav_search), style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                singleLine = true,
                supportingText = { Text(stringResource(R.string.search_hint)) },
            )
            when {
                state.isLoading -> LoadingSkeleton()
                state.error != null -> MessageState(
                    title = stringResource(R.string.nav_search),
                    body = state.error.text(),
                    primaryLabel = stringResource(R.string.action_retry),
                    onPrimary = viewModel::retry,
                )
                state.submitted && state.results.isEmpty() -> MessageState(
                    title = stringResource(R.string.nav_search),
                    body = stringResource(R.string.search_empty, state.query.trim()),
                )
                state.results.isEmpty() -> MessageState(
                    title = stringResource(R.string.nav_search),
                    body = stringResource(R.string.search_hint),
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(vertical = Dimens.gap),
                    verticalArrangement = Arrangement.spacedBy(Dimens.gap),
                ) {
                    items(state.results, key = { city -> "${city.geocodingId}-${city.latitude}-${city.longitude}" }) { city ->
                        CityRow(city, onClick = { onCitySelected(city) })
                    }
                }
            }
        }
}

@Composable
private fun CityRow(city: City, onClick: () -> Unit) {
    val label = placeLabel(city.name, city.adminArea, city.country, city.name)
    Card(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(city.name) },
            supportingContent = {
                val region = listOf(city.adminArea, city.country).filter { it.isNotBlank() }
                if (region.isNotEmpty()) Text(region.joinToString(", "))
            },
            modifier = Modifier
                .clickable(onClick = onClick)
                .semantics { contentDescription = label },
        )
    }
}
