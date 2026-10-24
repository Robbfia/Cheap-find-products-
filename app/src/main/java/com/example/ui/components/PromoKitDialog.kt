package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GeneratedPromoKit
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromoKitDialog(
    kit: GeneratedPromoKit,
    onDismiss: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onLinkClickRecorded: (String) -> Unit,
    onNotify: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedSectionTab by remember { mutableIntStateOf(0) } // 0: Video Script, 1: Social Caption, 2: Affiliate Link

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .testTag("promo_kit_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldContainer
                            ) {
                                Text(
                                    text = kit.targetPlatform,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnEmeraldContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = kit.tone,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = kit.productTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { onToggleFavorite(kit.id) },
                            modifier = Modifier.testTag("dialog_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (kit.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (kit.isFavorite) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("dialog_close_button")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats overview banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Est. Sale Price",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₱${String.format("%,.2f", kit.productPrice)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(
                                text = "Est. Commission",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₱${String.format("%,.2f", kit.commissionEstimate)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "Tracking Tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = kit.affiliateTag,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section Tabs
                TabRow(
                    selectedTabIndex = selectedSectionTab,
                    containerColor = Color.Transparent,
                    contentColor = EmeraldPrimary
                ) {
                    Tab(
                        selected = selectedSectionTab == 0,
                        onClick = { selectedSectionTab = 0 },
                        text = { Text("Video Script", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedSectionTab == 1,
                        onClick = { selectedSectionTab = 1 },
                        text = { Text("Caption & Tags", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedSectionTab == 2,
                        onClick = { selectedSectionTab = 2 },
                        text = { Text("Affiliate Link", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content Area Scrollable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedSectionTab) {
                        0 -> VideoScriptSection(
                            kit = kit,
                            onCopySection = { label, text ->
                                clipboardManager.setText(AnnotatedString(text))
                                onNotify("Copied $label to clipboard!")
                            }
                        )
                        1 -> SocialCaptionSection(
                            kit = kit,
                            onCopyCaption = {
                                val fullCaption = "${kit.socialCaption}\n\n${kit.hashtags.joinToString(" ")}"
                                clipboardManager.setText(AnnotatedString(fullCaption))
                                onNotify("Copied caption & hashtags!")
                            }
                        )
                        2 -> AffiliateLinkSection(
                            kit = kit,
                            onCopyLink = {
                                clipboardManager.setText(AnnotatedString(kit.affiliateUrl))
                                onLinkClickRecorded(kit.id)
                                onNotify("Copied tracked affiliate link!")
                            },
                            onTestLink = {
                                onLinkClickRecorded(kit.id)
                                onNotify("Link click logged (+1 click recorded in Analytics)!")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Row: Share and Copy All
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareBody = """
                                🛍️ ${kit.productTitle}
                                🏷️ Price: ₱${kit.productPrice}
                                
                                🎬 Video Hook: ${kit.scriptHook}
                                🎙️ Script: ${kit.scriptVoiceover}
                                
                                🔗 Buy / Affiliate Link:
                                ${kit.affiliateUrl}
                                
                                ${kit.hashtags.joinToString(" ")}
                            """.trimIndent()

                            shareText(context, "Promo Kit: ${kit.productTitle}", shareBody)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dialog_share_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Kit")
                    }

                    Button(
                        onClick = {
                            val copyAll = """
                                ${kit.socialCaption}
                                
                                🔗 Link: ${kit.affiliateUrl}
                                
                                ${kit.hashtags.joinToString(" ")}
                            """.trimIndent()
                            clipboardManager.setText(AnnotatedString(copyAll))
                            onLinkClickRecorded(kit.id)
                            onNotify("Copied all content ready to post!")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dialog_copy_all_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy All")
                    }
                }
            }
        }
    }
}

@Composable
fun VideoScriptSection(
    kit: GeneratedPromoKit,
    onCopySection: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Teleprompter Banner Tip
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEF3C7),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Short-Form Video Teleprompter (15-30s TikTok/Reels)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
            }
        }

        ScriptCard(
            badge = "1. ATTENTION HOOK (0:00 - 0:03)",
            badgeColor = Color(0xFFFEE2E2),
            badgeTextColor = Color(0xFFB91C1C),
            content = kit.scriptHook,
            onCopy = { onCopySection("Hook", kit.scriptHook) }
        )

        ScriptCard(
            badge = "2. SCENE & VISUAL ACTION",
            badgeColor = Color(0xFFE0E7FF),
            badgeTextColor = Color(0xFF3730A3),
            content = kit.scriptVisualScene,
            onCopy = { onCopySection("Visual Scene", kit.scriptVisualScene) }
        )

        ScriptCard(
            badge = "3. VOICEOVER / TALKING POINTS",
            badgeColor = EmeraldContainer,
            badgeTextColor = OnEmeraldContainer,
            content = kit.scriptVoiceover,
            onCopy = { onCopySection("Voiceover", kit.scriptVoiceover) }
        )

        ScriptCard(
            badge = "4. CALL TO ACTION (CTA)",
            badgeColor = Color(0xFFFEF3C7),
            badgeTextColor = Color(0xFFB45309),
            content = kit.scriptCta,
            onCopy = { onCopySection("CTA", kit.scriptCta) }
        )
    }
}

@Composable
fun ScriptCard(
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color,
    content: String,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SocialCaptionSection(
    kit: GeneratedPromoKit,
    onCopyCaption: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Social Post Caption",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onCopyCaption,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Caption",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = kit.socialCaption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }

        // Hashtags cluster
        Column {
            Text(
                text = "Optimized Hashtags",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                kit.hashtags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AffiliateLinkSection(
    kit: GeneratedPromoKit,
    onCopyLink: () -> Unit,
    onTestLink: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Your Trackable Deep Affiliate Link",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = kit.affiliateUrl,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCopyLink,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Link", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = onTestLink,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Click", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Deep-linking explanation note
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = EmeraldContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "✓ App Deep-Linking Active",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnEmeraldContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When users tap this link, your tagged tracking parameter (?aff_id=${kit.affiliateTag}) guarantees commissions on qualifying sales in this session.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnEmeraldContainer
                )
            }
        }
    }
}

private fun shareText(context: Context, title: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, "Share Promo Kit via"))
}
