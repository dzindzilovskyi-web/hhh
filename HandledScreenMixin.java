package com.example.dropall.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    // Положение кнопки относительно левого верхнего угла окна
    private static final int BTN_OFFSET_X = 0;
    private static final int BTN_OFFSET_Y = -22; // выше окна
    private static final int BTN_WIDTH = 90;
    private static final int BTN_HEIGHT = 20;

    @Shadow @Final protected T handler;
    @Shadow protected int x;
    @Shadow protected int y;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void dropall$addButton(CallbackInfo ci) {
        if ((Object) this instanceof CreativeInventoryScreen) return;
        if (!dropall$isSupported(handler)) return;

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Выкинуть всё"), b -> dropall$dropAll())
                .dimensions(this.x + BTN_OFFSET_X, this.y + BTN_OFFSET_Y, BTN_WIDTH, BTN_HEIGHT)
                .build()
        );
    }

    private static boolean dropall$isSupported(ScreenHandler h) {
        return h instanceof PlayerScreenHandler
            || h instanceof GenericContainerScreenHandler
            || h instanceof ShulkerBoxScreenHandler
            || h instanceof HopperScreenHandler
            || h instanceof Generic3x3ContainerScreenHandler;
    }

    private void dropall$dropAll() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.interactionManager == null) return;

        boolean isPlayerScreen = handler instanceof PlayerScreenHandler;
        boolean dropPlayerInv = isPlayerScreen || Screen.hasShiftDown();

        for (Slot slot : handler.slots) {
            if (!slot.hasStack()) continue;
            boolean isPlayerInvSlot = slot.inventory instanceof PlayerInventory;

            if (dropPlayerInv) {
                if (!isPlayerInvSlot || slot.getIndex() > 35) continue;
            } else {
                if (isPlayerInvSlot) continue;
            }

            mc.interactionManager.clickSlot(handler.syncId, slot.id, 1, SlotActionType.THROW, mc.player);
        }
    }
}
