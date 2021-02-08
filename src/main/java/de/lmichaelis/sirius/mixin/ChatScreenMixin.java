package de.lmichaelis.sirius.mixin;

import de.lmichaelis.sirius.Sirius;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    /**
     * Prevents the original minecraft chat screen from opening and opens a
     * {@link de.lmichaelis.sirius.gui.ChatWindow.ChatScreen} instead.
     *
     * @author Luis Michaelis
     * @reason We want to prevent the original Minecraft screen from opening, therefore we can hook on the {@link ChatScreen#init()}
     * method and open our own chat screen (in this case {@link de.lmichaelis.sirius.gui.ChatWindow.ChatScreen}) which
     * will close the original screen.
     */
    @Overwrite
    public void init() {
        Sirius.window.openChat();
    }
}
