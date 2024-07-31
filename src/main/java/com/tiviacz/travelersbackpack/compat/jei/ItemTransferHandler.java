package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.SettingsManager;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.network.ServerboundSettingsPacket;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.library.transfer.BasicRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ItemTransferHandler extends BasicRecipeTransferHandler<TravelersBackpackItemMenu, RecipeHolder<CraftingRecipe>>
{
    public ItemTransferHandler(IConnectionToServer serverConnection, IStackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<TravelersBackpackItemMenu, RecipeHolder<CraftingRecipe>> transferInfo)
    {
        super(serverConnection, stackHelper, handlerHelper, transferInfo);
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(TravelersBackpackItemMenu container, RecipeHolder<CraftingRecipe> recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer)
    {
        if(doTransfer)
        {
            container.container.getSettingsManager().set(SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1);

            PacketDistributor.sendToServer(new ServerboundSettingsPacket(container.container.getScreenID(), SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1));
            //TravelersBackpack.NETWORK.send(new ServerboundSettingsPacket(container.container.getScreenID(), SettingsManager.CRAFTING, SettingsManager.SHOW_CRAFTING_GRID, (byte)1), PacketDistributor.SERVER.noArg());
        }
        return super.transferRecipe(container, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }
}