package com.igorlink.stanleygpt.mixin;

import com.igorlink.stanleygpt.client.gui.screens.ModSettingsScreen;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for the OptionsScreen class.
 */
@Slf4j
@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    // Constructor
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    // Field for the button
    private ButtonWidget myButton;

    /**
     * Injects code to position the button.
     *
     * @param ci The callback info.
     */
    @Inject(method = "refreshWidgetPositions", at = @At(value = "TAIL"))
    private void onRefreshWidgetPositions(CallbackInfo ci) {
        // Find the "Skin Customisation" button
        ButtonWidget skinCustomizationButton = (ButtonWidget) this.children().get(3);
        for (int i = 0; i < this.children().size(); i++) {
            if (this.children().get(i) instanceof ButtonWidget && ((ButtonWidget) this.children().get(i)).getMessage().contains(Text.translatable("options.skinCustomisation"))) {
                skinCustomizationButton = (ButtonWidget) this.children().get(i);
            }
        }

        // Position the button
        int x = skinCustomizationButton.getX();
        int y = skinCustomizationButton.getY() - 24;
        int buttonWidth = skinCustomizationButton.getWidth() * 2 + 8;
        int buttonHeight = skinCustomizationButton.getHeight();

        // Create the button if it doesn't exist
        if (myButton == null) {
            myButton = ButtonWidget.builder(Text.literal("StanleyGPT"), button -> {
                // Open the ModSettingsScreen
                MinecraftClient.getInstance().setScreen(new ModSettingsScreen(this));
            }).dimensions(x, y, buttonWidth, buttonHeight).build();
            this.addDrawableChild(myButton);
        } else {
            // Update the button's position and size
            myButton.setX(x);
            myButton.setY(y);
            myButton.setWidth(buttonWidth);
            myButton.setHeight(buttonHeight);
        }
    }

}