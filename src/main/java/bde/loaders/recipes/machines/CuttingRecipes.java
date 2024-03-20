package bde.loaders.recipes.machines;

import gregtech.api.enums.ItemList;
import net.minecraft.item.ItemStack;

import static gregtech.api.util.GT_RecipeBuilder.SECONDS;

public class CuttingRecipes extends gregtech.loaders.postload.recipes.CuttingRecipes {

        @Override
        public void run(){
            //Silicon Wafer additions
            {
                //BDE Changes
                recipeWithClassicFluids(
                    new ItemStack[] { ItemList.Circuit_Silicon_Ingot.get(1) },
                    new ItemStack[] { ItemList.Circuit_Silicon_Wafer.get(16),
                    },
                    10 * SECONDS,
                    8,
                    false);
            }
            //BDE Changes
            recipeWithClassicFluids(
                new ItemStack[] { ItemList.Circuit_Silicon_Ingot2.get(1) },
                new ItemStack[] { ItemList.Circuit_Silicon_Wafer2.get(16),
                },
                20 * SECONDS,
                65,
                true);
            //BDE Changes
            recipeWithClassicFluids(
                new ItemStack[] { ItemList.Circuit_Silicon_Ingot3.get(1) },
                new ItemStack[] { ItemList.Circuit_Silicon_Wafer3.get(16),
                },
                40 * SECONDS,
                384,
                true);
        }

}
