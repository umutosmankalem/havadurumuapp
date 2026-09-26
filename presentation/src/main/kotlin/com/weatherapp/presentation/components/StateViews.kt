package com.weatherapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.weatherapp.presentation.theme.Dimens

@Composable
internal fun MessageState(
    title: String,
    body: String,
    primaryLabel: String? = null,
    onPrimary: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(Dimens.gap))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (primaryLabel != null && onPrimary != null) {
            Spacer(Modifier.height(Dimens.sectionGap))
            Button(onClick = onPrimary, modifier = Modifier.fillMaxWidth()) {
                Text(primaryLabel)
            }
        }
        if (secondaryLabel != null && onSecondary != null) {
            Spacer(Modifier.height(Dimens.gap))
            Button(onClick = onSecondary, modifier = Modifier.fillMaxWidth()) {
                Text(secondaryLabel)
            }
        }
    }
}

@Composable
internal fun LoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.screenPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.gap),
    ) {
        SkeletonBlock(Modifier.fillMaxWidth(0.46f).height(Dimens.touchTarget))
        SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(Dimens.touchTarget * 2))
        SkeletonBlock(Modifier.fillMaxWidth().height(Dimens.metricMinHeight))
        SkeletonBlock(Modifier.fillMaxWidth().height(Dimens.metricMinHeight * 2))
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(Dimens.cardRadius))
            .background(MaterialTheme.colorScheme.surfaceContainer),
    )
}

@Composable
internal fun InlineProgressSpacer() {
    Spacer(Modifier.size(Dimens.gap))
}
