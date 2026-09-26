package de.kitshn.ui.component.model.recipe

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.kitshn.api.tandoor.model.recipe.TandoorRecipe
import de.kitshn.formatAmount
import de.kitshn.ui.theme.Typography
import kitshn.shared.generated.resources.Res
import kitshn.shared.generated.resources.recipe_nutrition_per_100_g
import org.jetbrains.compose.resources.stringResource

internal data class RecipePropertyRow(
    val name: String,
    val value: String,
    val unit: String
)

internal fun TandoorRecipe.getRecipePropertyRows(
    showFractionalValues: Boolean
): List<RecipePropertyRow> = getRelevantRecipeProperties()
    .sortedBy { it.property_type.order }
    .map {
        RecipePropertyRow(
            name = it.property_type.name,
            value = it.property_amount.formatAmount(showFractionalValues).ifBlank { "—" },
            unit = it.property_type.unit.orEmpty()
        )
    }

@Composable
fun RecipePropertiesCard(
    modifier: Modifier = Modifier,
    columnModifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    colors: CardColors = CardDefaults.cardColors(),
    recipe: TandoorRecipe? = null,
    showFractionalValues: Boolean,
    prependContent: @Composable () -> Unit = { }
) {
    if(recipe == null) return

    val propertyRows = recipe.getRecipePropertyRows(showFractionalValues)

    if(propertyRows.isEmpty()) return

    prependContent()

    Card(
        modifier = modifier,
        interactionSource = interactionSource,
        colors = colors,
        onClick = { }
    ) {
        Column(
            columnModifier
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                text = stringResource(Res.string.recipe_nutrition_per_100_g),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = Typography().titleLarge
            )

            @Composable
            fun TableTextBox(
                text: String,
                weight: Float,
                contentAlignment: Alignment = Alignment.CenterEnd
            ) {
                Box(
                    Modifier
                        .weight(weight)
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 8.dp
                        ),
                    contentAlignment = contentAlignment
                ) {
                    Text(
                        text = text,
                        Modifier.basicMarquee(),
                        maxLines = 1
                    )
                }
            }

            Column(
                Modifier.fillMaxWidth()
            ) {
                propertyRows.forEach {
                    HorizontalDivider()

                    Row(
                        Modifier.fillMaxWidth()
                    ) {
                        TableTextBox(
                            text = it.name,
                            weight = 0.6f,
                            contentAlignment = Alignment.CenterStart
                        )

                        TableTextBox(
                            text = it.value,
                            weight = 0.25f
                        )

                        TableTextBox(
                            text = it.unit,
                            weight = 0.15f,
                            contentAlignment = Alignment.CenterStart
                        )
                    }
                }
            }
        }
    }
}
