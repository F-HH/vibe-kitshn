package de.kitshn.ui.view.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Copyright
import androidx.compose.material3.Badge
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.author
import de.kitshn.launchWebsiteHandler
import de.kitshn.platformDetails
import de.kitshn.ui.component.buttons.BackButton
import de.kitshn.ui.component.settings.SettingsListItem
import de.kitshn.ui.component.settings.SettingsListItemPosition
import de.kitshn.ui.dialog.AboutLibraryBottomSheet
import de.kitshn.ui.dialog.rememberAboutLibraryBottomSheetState
import de.kitshn.ui.theme.Typography
import de.kitshn.ui.view.ViewParameters
import kitshn.shared.generated.resources.Res
import kitshn.shared.generated.resources.common_version
import kitshn.shared.generated.resources.ic_logo_ico
import kitshn.shared.generated.resources.private_app_name
import kitshn.shared.generated.resources.settings_section_about_item_freepik
import kitshn.shared.generated.resources.settings_section_about_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ViewSettingsAbout(
    p: ViewParameters
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())

    val launchWebsite = launchWebsiteHandler()

    val libs by produceLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    val aboutLibraryBottomSheetState = rememberAboutLibraryBottomSheetState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = { BackButton(p.back) },
                title = { Text(stringResource(Res.string.settings_section_about_label)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
        ) {
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                item {
                    SettingsListItem(
                        position = SettingsListItemPosition.SINGULAR,
                        label = { Text("${stringResource(Res.string.private_app_name)} (${platformDetails.buildType}) (${platformDetails.platform.displayName})") },
                        description = { Text("${stringResource(Res.string.common_version)} ${platformDetails.packageExtendedVersion}") },
                        icon = vectorResource(Res.drawable.ic_logo_ico),
                        iconTint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = stringResource(Res.string.private_app_name)
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }

                platformSpecificItems()

                item {
                    SettingsListItem(
                        position = SettingsListItemPosition.TOP,
                        label = { Text(stringResource(Res.string.settings_section_about_item_freepik)) },
                        description = { Text("Icon made by Freepik from www.flaticon.com") },
                        icon = Icons.Rounded.Copyright,
                        contentDescription = stringResource(Res.string.settings_section_about_item_freepik),
                        onClick = {
                            launchWebsite("https://www.flaticon.com/free-icon/chef-hat-outline-symbol_45582")
                        }
                    )
                }

                items(libs?.libraries?.size ?: 0) {
                    val library = libs!!.libraries[it]

                    SettingsListItem(
                        position = when(it) {
                            (libs?.libraries?.size ?: 0) - 1 -> SettingsListItemPosition.BOTTOM
                            else -> SettingsListItemPosition.BETWEEN
                        },
                        overlineContent = { Text(library.author) },
                        label = { Text(library.name) },
                        description = {
                            FlowRow(
                                Modifier.padding(top = 8.dp)
                            ) {
                                library.licenses.forEach {
                                    Badge(
                                        containerColor = if(library.openSource)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = if(library.openSource)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onTertiaryContainer
                                    ) {
                                        Text(
                                            text = it.name
                                        )
                                    }
                                }
                            }
                        },
                        trailingContent = {
                            Text(
                                library.artifactVersion ?: "",
                                style = Typography().labelMedium
                            )
                        },
                        contentDescription = library.name,
                        onClick = { aboutLibraryBottomSheetState.open(library) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    AboutLibraryBottomSheet(state = aboutLibraryBottomSheetState)
}

expect fun LazyListScope.platformSpecificItems()
