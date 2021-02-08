package de.lmichaelis.sirius.mixin;

import de.lmichaelis.sirius.Sirius;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(CommandSuggestor.class)
public class CommandSuggestorMixin {
    @Shadow
    private CommandSuggestor.SuggestionWindow window;

    @Shadow
    @Final
    private List<OrderedText> messages;

    @Shadow
    @Final
    private int color;

    @Shadow
    @Final
    private TextRenderer textRenderer;

    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * @param matrixStack
     * @param i
     * @param j
     * @author
     */
    @Overwrite
    public void render(MatrixStack matrixStack, int i, int j) {
        if (this.window != null) {
            this.window.render(matrixStack, i, j);
        } else {
            int k = 0;

            for (Iterator<OrderedText> var5 = this.messages.iterator(); var5.hasNext(); ++k) {
                OrderedText orderedText = var5.next();

                int y = Sirius.window.getHeight() + Sirius.window.getY() - 27 - 12 * k;
                int x = Sirius.window.getX();
                int x2 = x + client.textRenderer.getWidth(orderedText);

                if (x2 > client.getWindow().getScaledWidth()) {
                    int diff = x2 - client.getWindow().getScaledWidth();
                    x -= diff;
                    x2 -= diff;
                }

                DrawableHelper.fill(matrixStack, x, y, x2, y + 12, this.color);
                this.textRenderer.drawWithShadow(matrixStack, orderedText, x, y + 2, -1);
            }
        }

    }
}
