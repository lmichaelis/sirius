package de.lmichaelis.sirius;

import de.lmichaelis.sirius.gui.ChatWindow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public class Sirius implements ClientModInitializer {
    public static final String MODID = "sirius";
    public static ChatWindow window;

    @Override
    public void onInitializeClient() {
        Window client = MinecraftClient.getInstance().getWindow();

        Config cfg = new Config();
        cfg.chatWindowX = 10;
        cfg.chatWindowY = 100;
//        cfg.chatWindowUnfocusedHeight = 200;
        cfg.chatWindowHeight = 200;
        cfg.chatWindowWidth = 300;
        cfg.chatWindowOpacity = 0.8;

        window = new ChatWindow(cfg);
    }
}
