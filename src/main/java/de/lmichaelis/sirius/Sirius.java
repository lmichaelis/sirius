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
//        cfg.chatWindowUnfocusedHeight = 200; //FIXME: read or disable minecraft chat's "unfocused height" setting
        cfg.chatWindowHeight = 200;  //FIXME: read or disable minecraft chat's "focused height" setting
        cfg.chatWindowWidth = 300; //FIXME: read or disable minecraft chat's "width" setting
//        cfg.chatScale = 1.0F; //FIXME: read or disable minecraft chat's "scale" setting
        cfg.chatWindowOpacity = 0.8; //FIXME: read or disable minecraft chat's "text background opacity" setting
        cfg.chatWindowFocusedBackColor = 0x00111111;
        cfg.lineSpacing = 0; //FIXME: read or disable minecraft chat's "line spacing" setting

        window = new ChatWindow(cfg);
    }
}
