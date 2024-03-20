package bde.loaders.recipes;

import bde.loaders.recipes.machines.BlastFurnaceRecipes;
import bde.loaders.recipes.machines.CuttingRecipes;

public class BDE_MachineRecipeLoader implements Runnable{
    @Override
    public void run() {
        new BlastFurnaceRecipes().run();
        new CuttingRecipes().run();
    }
}
