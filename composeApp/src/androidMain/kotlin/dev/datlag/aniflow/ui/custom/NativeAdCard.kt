package dev.datlag.aniflow.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import dev.datlag.aniflow.common.bottomShadowBrush
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NativeAdCard(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier
) {
    NativeAdViewCompose(
        modifier = modifier
    ) { nativeAdView ->
        SideEffect {
            nativeAdView.setNativeAd(nativeAd)
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeAdView(
                        modifier = Modifier.size(56.dp),
                        getView = {
                            nativeAdView.iconView = it
                        }
                    ) {
                        NativeAdImage(
                            model = nativeAd.icon?.uri ?: nativeAd.icon?.drawable,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small)
                        )
                    }
                    nativeAd.callToAction?.ifBlank { null }?.let { action ->
                        NativeAdView(
                            getView = {
                                nativeAdView.callToActionView = it
                            }
                        ) {
                            Button(
                                onClick = {
                                    nativeAdView.callToActionView?.performClick()
                                }
                            ) {
                                Text(text = action)
                            }
                        }
                    }
                }
                nativeAd.body?.ifBlank { null }?.let { body ->
                    NativeAdView(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        getView = {
                            nativeAdView.bodyView = it
                        }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = body
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    nativeAd.mediaContent?.let { media ->
                        NativeAdMediaView(
                            modifier = Modifier
                                .defaultMinSize(
                                    minWidth = 120.dp,
                                    minHeight = 120.dp
                                )
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium),
                            nativeAdView = nativeAdView,
                            mediaContent = media
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .bottomShadowBrush(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            nativeAd.headline?.ifBlank { null }?.let { headline ->
                                NativeAdView(
                                    modifier = Modifier.fillMaxWidth(),
                                    getView = {
                                        nativeAdView.headlineView = it
                                    }
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = headline,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
                            ) {
                                nativeAd.store?.ifBlank { null }?.let { store ->
                                    NativeAdView(
                                        getView = {
                                            nativeAdView.storeView = it
                                        }
                                    ) {
                                        Text(
                                            text = store,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                nativeAd.price?.ifBlank { null }?.let { price ->
                                    NativeAdView(
                                        getView = {
                                            nativeAdView.priceView = it
                                        }
                                    ) {
                                        Text(text = price)
                                    }
                                }
                                nativeAd.starRating?.roundToInt()?.let { rating ->
                                    NativeAdView(
                                        getView = {
                                            nativeAdView.starRatingView = it
                                        }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            repeat(rating) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Star,
                                                    contentDescription = null
                                                )
                                            }
                                            repeat(5 - rating) {
                                                Icon(
                                                    imageVector = Icons.Rounded.StarOutline,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}