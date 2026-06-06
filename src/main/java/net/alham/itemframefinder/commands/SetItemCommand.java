package net.alham.itemframefinder.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.alham.itemframefinder.ModDataKeys;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

public class SetItemCommand {

    public static void init(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        dispatcher.register(
                Commands.literal("setitem")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument(
                                        "item",
                                        ItemArgument.item(buildContext)
                                )
                                .executes(c -> {
                                    CommandSourceStack source = c.getSource();

                                    ServerPlayer player = source.getPlayer();
                                    if (player == null) {
                                        source.sendFailure(Component.literal("Players only."));
                                        return 0;
                                    }

                                    ItemInput input = ItemArgument.getItem(c, "item");
                                    Item item = input.getItem();

                                    return execute(source, player, item);
                                }))
        );
    }

    private static int execute(CommandSourceStack source, ServerPlayer player, Item requiredItem) {
        ItemStack held = player.getMainHandItem();

        if (!held.is(Items.ITEM_FRAME) && !held.is(Items.GLOW_ITEM_FRAME)) {
            source.sendFailure(Component.literal("ERROR: Hold an item frame."));
            return 0;
        }

        CustomData itemFrameData = held.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        );

        CompoundTag tag = itemFrameData.copyTag();

        tag.putString(ModDataKeys.REQUIRED_ITEM_KEY,
                BuiltInRegistries.ITEM.getKey(requiredItem).toString());

        held.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(tag)
        );

        source.sendSuccess(
                () -> Component.literal("Item frame set to: ")
                        .append(new ItemStack(requiredItem).getHoverName()),
                false
        );

        return 1;
    }
}