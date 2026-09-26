package de.kitshn.ui.component.model.recipe

import de.kitshn.api.tandoor.model.TandoorFoodProperty
import de.kitshn.api.tandoor.model.recipe.TandoorRecipe
import de.kitshn.api.tandoor.model.recipe.TandoorRecipeProperty
import de.kitshn.api.tandoor.model.recipe.TandoorRecipePropertyType
import de.kitshn.formatAmount
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecipePropertiesCardTest {

    @Test
    fun ignoresFoodPropertiesWhenRecipePropertiesExist() {
        val recipe = recipe(
            properties = listOf(property(name = "Protein", amount = 12.0, unit = "g"))
        ).withFoodProperty()

        assertEquals(
            listOf(RecipePropertyRow("Protein", "12", "g")),
            recipe.getRecipePropertyRows(showFractionalValues = false)
        )
    }

    @Test
    fun returnsNoRowsWhenOnlyFoodPropertiesExist() {
        val recipe = recipe().withFoodProperty()

        assertTrue(recipe.getRecipePropertyRows(showFractionalValues = false).isEmpty())
    }

    @Test
    fun keepsRecipePropertyNameAmountAndUnit() {
        val recipe = recipe(
            properties = listOf(property(name = "Calories", amount = 245.0, unit = "kcal"))
        )

        assertEquals(
            listOf(RecipePropertyRow("Calories", "245", "kcal")),
            recipe.getRecipePropertyRows(showFractionalValues = false)
        )
    }

    @Test
    fun servingsDoNotChangeRecipePropertyRows() {
        val properties = listOf(property(name = "Fat", amount = 7.25, unit = "g"))

        assertEquals(
            recipe(servings = 1, properties = properties).getRecipePropertyRows(false),
            recipe(servings = 12, properties = properties).getRecipePropertyRows(false)
        )
    }

    @Test
    fun sortsRecipePropertiesByPropertyTypeOrder() {
        val recipe = recipe(
            properties = listOf(
                property(name = "Fat", amount = 3.0, order = 30),
                property(name = "Calories", amount = 100.0, order = 10),
                property(name = "Protein", amount = 8.0, order = 20)
            )
        )

        assertEquals(
            listOf("Calories", "Protein", "Fat"),
            recipe.getRecipePropertyRows(false).map { it.name }
        )
    }

    @Test
    fun respectsFractionalValuesSetting() {
        val recipe = recipe(
            properties = listOf(property(name = "Carbohydrates", amount = 2.5, unit = "g"))
        )

        assertEquals(2.5.formatAmount(true), recipe.getRecipePropertyRows(true).single().value)
        assertEquals(2.5.formatAmount(false), recipe.getRecipePropertyRows(false).single().value)
    }

    @Test
    fun excludesNonPositiveRecipeProperties() {
        val recipe = recipe(
            properties = listOf(
                property(name = "Zero", amount = 0.0),
                property(name = "Negative", amount = -1.0)
            )
        )

        assertTrue(recipe.getRecipePropertyRows(false).isEmpty())
    }

    private fun recipe(
        servings: Int = 4,
        properties: List<TandoorRecipeProperty> = emptyList()
    ) = TandoorRecipe(
        id = 1,
        name = "Test recipe",
        keywords = emptyList(),
        stepsRaw = JsonArray(emptyList()),
        working_time = 0,
        waiting_time = 0,
        created_at = "2026-01-01",
        updated_at = "2026-01-01",
        internal = false,
        properties = properties,
        foodPropertiesRaw = JsonObject(emptyMap()),
        servings = servings,
        servings_text = "servings"
    )

    private fun property(
        name: String,
        amount: Double,
        unit: String? = "g",
        order: Long = 0
    ) = TandoorRecipeProperty(
        id = order,
        property_amount = amount,
        property_type = TandoorRecipePropertyType(
            id = order,
            name = name,
            unit = unit,
            order = order
        )
    )

    private fun TandoorRecipe.withFoodProperty() = apply {
        food_properties += TandoorFoodProperty(
            id = 1,
            name = "Automatically calculated calories",
            unit = "kcal",
            food_values = JsonObject(mapOf("ingredient" to JsonPrimitive(1))),
            total_value = 999.0
        )
    }
}
