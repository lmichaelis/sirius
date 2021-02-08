package de.lmichaelis.sirius.mixin;

import de.lmichaelis.sirius.Sirius;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    /**
     * Prevents the original minecraft chat hud from being rendered and renders our custom
     * {@link de.lmichaelis.sirius.gui.ChatWindow} instead.
     *
     * @author Luis Michaelis
     * @reason We want to prevent the original minecraft {@link ChatHud} from being drawn, therefore we overwrite it,
     * replacing it with {@link de.lmichaelis.sirius.gui.ChatWindow#render(MatrixStack, float)} which will draw our own
     * screen on top.
     */
    @Overwrite
    public void render(MatrixStack matrices, int tickDelta) {
        Sirius.window.render(matrices, tickDelta);
    }

    /**
     * @param message
     * @author
     */
    @Overwrite
    public void addMessage(Text message) {
        Sirius.window.addMessage(message);
    }

    /**
     * @param clearHistory
     * @author
     */
    @Overwrite
    public void clear(boolean clearHistory) {
        Sirius.window.clear(clearHistory);
    }
}
