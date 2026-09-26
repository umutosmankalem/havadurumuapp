package com.weatherapp.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weatherapp.domain.model.City
import com.weatherapp.presentation.R
import com.weatherapp.presentation.components.LoadingSkeleton
import com.weatherapp.presentation.components.MessageState
import com.weatherapp.presentation.message.text
import com.weatherapp.presentation.theme.Dimens

@Composable
fun FavoritesScreen(
    onCitySelected: (City) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.screenPadding),
        ) {
            Text(stringResource(R.string.nav_favorites), style = MaterialTheme.typography.headlineMedium)
            when {
                state.isLoading -> LoadingSkeleton()
                state.error != null && state.cities.isEmpty() -> MessageState(
                    title = stringResource(R.string.nav_favorites),
                    body = state.error.text(),
                )
                state.cities.isEmpty() -> MessageState(
                    title = stringResource(R.string.favorites_empty_title),
                    body = stringResource(R.string.favorites_empty_body),
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(vertical = Dimens.sectionGap),
                    verticalArrangement = Arrangement.spacedBy(Dimens.gap),
                ) {
                    items(state.cities, key = { "${it.geocodingId}-${it.latitude}-${it.longitude}" }) { city ->
                        FavoriteRow(
                            city = city,
                            onOpen = { onCitySelected(city) },
                            onDelete = { viewModel.delete(city) },
                        )
                    }
                }
            }
        }
}

@Composable
private fun FavoriteRow(city: City, onOpen: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(city.name.ifBlank { stringResource(R.string.current_location_name) }) },
            supportingContent = {
                val region = listOf(city.adminArea, city.country).filter { it.isNotBlank() }
                if (region.isNotEmpty()) Text(region.joinToString(", "))
            },
            modifier = Modifier.clickable(onClick = onOpen),
            trailingContent = {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.favorites_remove, city.name),
                    )
                }
            },
        )
    }
}
