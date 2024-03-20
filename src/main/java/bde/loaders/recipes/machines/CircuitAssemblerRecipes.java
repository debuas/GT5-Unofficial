package bde.loaders.recipes.machines;

import gregtech.api.enums.*;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.util.GT_OreDictUnificator;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static bde.loaders.recipes.BDE_MachineRecipeLoader.solderingMats;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.enums.Mods.Railcraft;
import static gregtech.api.recipe.RecipeMaps.circuitAssemblerRecipes;
import static gregtech.api.util.GT_ModHandler.getModItem;
import static gregtech.api.util.GT_RecipeBuilder.SECONDS;
import static gregtech.api.util.GT_RecipeBuilder.TICKS;

public class CircuitAssemblerRecipes extends gregtech.loaders.postload.recipes.CircuitAssemblerRecipes {



    @Override
    public void run(){
        materialRunner(
            this::loadCircuitRecipes
        );
    }

    @SafeVarargs
    public final void materialRunner(BiConsumer<Materials, Integer>... runners){
        for (Materials tMat : solderingMats){
            int tMultiplier = tMat.contains(SubTag.SOLDERING_MATERIAL_GOOD) ? 1
                : tMat.contains(SubTag.SOLDERING_MATERIAL_BAD) ? 4 : 2;
            for (BiConsumer runner : runners) {
                runner.accept(tMat, tMultiplier);
            }
        }
    }

    public void loadCircuitRecipes(Materials tMat,int tMultiplier){
        //IC2 LV Circuit
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Basic.get(1),
                ItemList.Circuit_Parts_Resistor.get(2),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01,Materials.RedAlloy,2),
                ItemList.Circuit_Parts_Vacuum_Tube.get(2)
            )
            .itemOutputs(getModItem(IndustrialCraft2.ID, "itemPartCircuit", 1L, 0))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) 16)
            .addTo(circuitAssemblerRecipes);
        //LV T2
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Basic.get(1),
                ItemList.Circuit_Chip_ILC.get(1),
                ItemList.Circuit_Parts_Resistor.get(2),
                ItemList.Circuit_Parts_Diode.get(2),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Copper,2),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.Tin,2)

            )
            .itemOutputs(ItemList.Circuit_Basic.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) 16)
            .addTo(circuitAssemblerRecipes);
        //LV T3
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_CPU.get(1),
                ItemList.Circuit_Parts_Resistor.get(2),
                ItemList.Circuit_Parts_Capacitor.get(2),
                ItemList.Circuit_Parts_Transistor.get(2),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Copper,2)

            )
            .itemOutputs(ItemList.Circuit_Microprocessor.get(2))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) TierEU.RECIPE_MV/2)
            .addTo(circuitAssemblerRecipes);
        //LV T3 SOC
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_SoC.get(1),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Copper,2),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.Copper,2)

            )
            .itemOutputs(ItemList.Circuit_Microprocessor.get(2))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(2 * SECONDS + 10 * TICKS)
            .eut((int) 600)
            .addTo(circuitAssemblerRecipes);
        //MV T1
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Phenolic_Good.get(1),
                GT_OreDictUnificator.get(OrePrefixes.circuit,Materials.Basic,2),
                ItemList.Circuit_Parts_Diode.get(2),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01,Materials.Copper,2)

            )
            .itemOutputs(ItemList.Circuit_Good.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(15 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(circuitAssemblerRecipes);
        //MV T2
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Phenolic_Good.get(1),
                ItemList.Circuit_Basic.get(2),
                ItemList.Circuit_Parts_Resistor.get(4),
                ItemList.Circuit_Parts_Diode.get(4),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Gold,4),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.Silver,4)

                )
            .itemOutputs(ItemList.Circuit_Integrated_Good.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(20 * SECONDS)
            .eut((int) 24)
            .addTo(circuitAssemblerRecipes);
        //MV T2 Advanced
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Phenolic_Good.get(1),
                ItemList.Circuit_Basic.get(2),
                ItemList.Circuit_Parts_ResistorASMD.get(1),
                ItemList.Circuit_Parts_DiodeASMD.get(1),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Gold,4),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.Silver,4)

            )
            .itemOutputs(ItemList.Circuit_Integrated_Good.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) 24)
            .addTo(circuitAssemblerRecipes);
        //MV T3
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_CPU.get(1),
                ItemList.Circuit_Parts_Resistor.get(4),
                ItemList.Circuit_Parts_Capacitor.get(4),
                ItemList.Circuit_Parts_Transistor.get(4),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.RedAlloy,4)

            )
            .itemOutputs(ItemList.Circuit_Processor.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) TierEU.RECIPE_MV / 2)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);
        //MV T3 Advanced
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_CPU.get(1),
                ItemList.Circuit_Parts_ResistorASMD.get(1),
                ItemList.Circuit_Parts_CapacitorASMD.get(1),
                ItemList.Circuit_Parts_TransistorASMD.get(1),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.RedAlloy,4)

            )
            .itemOutputs(ItemList.Circuit_Processor.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(5 * SECONDS)
            .eut((int) TierEU.RECIPE_MV/2)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);
        //MV T3 SOC
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_SoC.get(1),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.RedAlloy,4),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.AnnealedCopper,4)

            )
            .itemOutputs(ItemList.Circuit_Processor.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(2 * SECONDS + 10 * TICKS)
            .eut((int) 2400)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);
        //HV T1
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Integrated_Good.get(1),
                ItemList.Circuit_Chip_ILC.get(2),
                ItemList.Circuit_Chip_Ram.get(2),
                ItemList.Circuit_Parts_Transistor.get(4),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Electrum,8),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.AnnealedCopper,8)
            )
            .itemOutputs(getModItem(IndustrialCraft2.ID, "itemPartCircuitAdv", 1L, 0))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(circuitAssemblerRecipes);
        //HV T1 Advanced
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Integrated_Good.get(1),
                ItemList.Circuit_Chip_ILC.get(2),
                ItemList.Circuit_Chip_Ram.get(2),
                ItemList.Circuit_Parts_TransistorASMD.get(1),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Electrum,8),
                GT_OreDictUnificator.get(OrePrefixes.bolt,Materials.AnnealedCopper,8)

            )
            .itemOutputs(getModItem(IndustrialCraft2.ID, "itemPartCircuitAdv", 1L, 0))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) TierEU.RECIPE_LV)
            .addTo(circuitAssemblerRecipes);
        //HV T2
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_CPU.get(1),
                ItemList.Circuit_Parts_Coil.get(4),
                ItemList.Circuit_Parts_Transistor.get(8),
                ItemList.Circuit_Chip_Ram.get(4),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.RedAlloy,8)
            )
            .itemOutputs(ItemList.Circuit_Advanced.get(1))
            .fluidInputs(tMat.getMolten(288L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) TierEU.RECIPE_MV)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);
        //HV T2 Advanced
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_CPU.get(1),
                ItemList.Circuit_Parts_InductorASMD.get(1),
                ItemList.Circuit_Parts_TransistorASMD.get(2),
                ItemList.Circuit_Chip_Ram.get(4),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.RedAlloy,8)
                )
            .itemOutputs(ItemList.Circuit_Advanced.get(1))
            .fluidInputs(tMat.getMolten(288L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) 96)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);
        //HV T3
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_NanoCPU.get(1),
                ItemList.Circuit_Parts_ResistorSMD.get(8),
                ItemList.Circuit_Parts_CapacitorSMD.get(8),
                ItemList.Circuit_Parts_TransistorSMD.get(8),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Electrum,8)
            )
            .itemOutputs(ItemList.Circuit_Nanoprocessor.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) 600)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);
        //HV T3 Advanced
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Board_Plastic_Advanced.get(1),
                ItemList.Circuit_Chip_NanoCPU.get(1),
                ItemList.Circuit_Parts_ResistorASMD.get(2),
                ItemList.Circuit_Parts_CapacitorASMD.get(2),
                ItemList.Circuit_Parts_TransistorASMD.get(2),
                GT_OreDictUnificator.get(OrePrefixes.wireFine,Materials.Electrum,8)
            )
            .itemOutputs(ItemList.Circuit_Nanoprocessor.get(1))
            .fluidInputs(tMat.getMolten(144L * tMultiplier / 2))
            .duration(10 * SECONDS)
            .eut((int) 600)
            .requiresCleanRoom()
            .addTo(circuitAssemblerRecipes);



    }


}
