package net.alham.itemframefinder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ItemFrameTooltips {

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.is(Items.ITEM_FRAME) && !stack.is(Items.GLOW_ITEM_FRAME)) {
            return;
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        CompoundTag tag = customData.copyTag();

        if (!tag.contains(ModDataKeys.REQUIRED_ITEM_KEY)) {
            return;
        }

        String itemId = tag.getString(ModDataKeys.REQUIRED_ITEM_KEY)
                .orElse("");

        if (itemId.isEmpty()) {
            return;
        }

        ResourceLocation id = ResourceLocation.tryParse(itemId);
        if (id == null) {
            return;
        }

        BuiltInRegistries.ITEM.get(id).ifPresent(requiredItem ->
                event.getToolTip().add(
                        Component.literal("Item set to: ")
                                .withStyle(ChatFormatting.AQUA)
                                .append(new ItemStack(requiredItem).getHoverName())
                )
        );
    }
}