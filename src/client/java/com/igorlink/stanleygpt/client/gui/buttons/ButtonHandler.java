package com.igorlink.stanleygpt.client.gui.buttons;

import com.igorlink.stanleygpt.client.commentmaker.CommentMaker;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Slf4j
public class ButtonHandler {
    private final KeyBinding skipCommentKey;

    public ButtonHandler(CommentMaker commentMaker) {

        // Создаем новую кнопку
        skipCommentKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Пропустить комментарий", // Идентификатор
                InputUtil.Type.KEYSYM, // Тип ввода (клавиатура, мышь и т.д.)
                GLFW.GLFW_KEY_GRAVE_ACCENT, // Стандартная кнопка (K)
                "StanleyGPT" // Категория
        ));

        // Слушаем событие нажатия клавиши
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (skipCommentKey.wasPressed()) {
                commentMaker.skipComment();
            }
        });
    }

}
