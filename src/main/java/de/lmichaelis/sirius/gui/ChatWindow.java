package de.lmichaelis.sirius.gui;

import de.lmichaelis.sirius.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

@Environment(EnvType.CLIENT)
public class ChatWindow extends DrawableHelper {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final LinkedList<OrderedText> messages = new LinkedList<>();
    private final Config config;
    private int maxMessageCount = 0;
    private int x;
    private int y;
    private int width;
    private int height;
    private double opacity;
    float scale;// = 1.0F;
    private int backgroundColor; // ARGB color, but alpha must be 0 to add opacity later
    private int scrollOffset = 0; // number of messages to scroll up by

    public ChatWindow(final Config config) {
        x = config.chatWindowX;
        y = config.chatWindowY;

        width = config.chatWindowWidth;
        height = config.chatWindowHeight;
        opacity = config.chatWindowOpacity;
        backgroundColor = config.chatWindowFocusedBackColor;
//        scale = config.chatScale; //TODO: grab from config rather than updating in beginRender() (using Minecraft's config setting)

        this.config = config; // FIXME: What does this do?
    }

    public void addMessage(Text text) {
        List<OrderedText> brokenMessages = ChatMessages.breakRenderedChatMessageLines(text, width - 8, client.textRenderer);

        if (scrollOffset != 0) {
            // if scroll position is not at the bottom of the chat,
            // then update scrollOffset so messages appear to not move
            scrollOffset += brokenMessages.size();
        }

        messages.addAll(brokenMessages);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void fixAlignment() {
        x = Math.max(0, x);
        x = Math.min(x, (int) (client.getWindow().getScaledWidth() - width * scale));

        y = Math.max(0, y);
        y = Math.min(y, (int) (client.getWindow().getScaledHeight() - height * scale));
    }

    protected void beginRender(MatrixStack matrices) {
        scale = (float) client.options.chatScale; //FIXME: maybe this should be updated somewhere else

        matrices.translate(x, y, 0.0D);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate(-x, -y, 0.0D);
    }

    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        matrices.translate(0.0D, -(double) (this.client.getWindow().getScaledHeight() - 48), 0.0D); //TODO before or after beginRender()?
        beginRender(matrices);

        int actualOpacity = Math.min((int) (opacity * 255.0D), 255) << 24; // FIXME config validation should be handled by Config

        fill(matrices, x, y, (x + width), (y + height), (backgroundColor | actualOpacity));

        maxMessageCount = ((int) Math.floor((float) (height - (client.textRenderer.fontHeight + config.lineSpacing) - 6) / (client.textRenderer.fontHeight + config.lineSpacing)));

        int indexLast = messages.size() - scrollOffset - 1;
        int indexFirst = messages.size() - scrollOffset - maxMessageCount;
        indexFirst = Math.max(indexFirst, 0);

        int actualMessageCount = Math.min(messages.size(), indexLast - indexFirst);
        int y = this.y + (maxMessageCount - actualMessageCount) * (client.textRenderer.fontHeight + config.lineSpacing) - 1;

        for (int i = indexFirst; i <= indexLast; i++) {
            client.textRenderer.drawWithShadow(matrices, messages.get(i), x + 4, y, -1);
            y += (client.textRenderer.fontHeight + config.lineSpacing);
        }

        matrices.pop();
    }

    public void clear(boolean clearHistory) {
        //TODO if clearHistory then actually clear the history
        messages.clear();
    }

    public void openChat() {
        client.openScreen(new ChatScreen());
    }


    public class ChatScreen extends Screen {
        protected TextFieldWidget chatField;
        private CommandSuggestor commandSuggestor;
//        private int scaledMouseX = 2, scaledMouseY = 2; // For debugging

        protected ChatScreen() {
            super(NarratorManager.EMPTY);
        }

        @Override
        protected void init() {
            this.client.keyboard.setRepeatEvents(true);

            this.chatField = new TextFieldWidget(
                    this.textRenderer,
                    ChatWindow.this.x + 4,
                    ChatWindow.this.y + ChatWindow.this.height - 12,
                    ChatWindow.this.width - client.textRenderer.getWidth("_") - 6,
                    12,
                    new TranslatableText("chat.editBox")) {
                protected MutableText getNarrationMessage() {
                    return super.getNarrationMessage().append(ChatScreen.this.commandSuggestor.method_23958());
                }
            };

            this.chatField.setMaxLength(256);
            this.chatField.setHasBorder(false);
            this.chatField.setText("");
            this.chatField.setChangedListener(this::onChatFieldUpdate);
            this.children.add(this.chatField);

            this.commandSuggestor = new CommandSuggestor(
                    this.client,
                    this,
                    this.chatField,
                    this.textRenderer,
                    false,
                    false,
                    1,
                    10,
                    true,
                    -805306368
            );

            this.commandSuggestor.refresh();
            this.setInitialFocus(this.chatField);
        }

        private void onChatFieldUpdate(String chatText) {
            String string = this.chatField.getText();
            this.commandSuggestor.setWindowActive(!string.equals(""));
            this.commandSuggestor.refresh();
        }

        @Override
        public void resize(MinecraftClient client, int width, int height) {
            String string = this.chatField.getText();
            this.init(client, width, height);
            this.chatField.setText(string);
            this.commandSuggestor.refresh();
        }

        @Override
        public void removed() {
            this.client.keyboard.setRepeatEvents(false);
            scrollOffset = 0;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            scrollOffset += amount;
            scrollOffset = Math.min(scrollOffset, messages.size() - maxMessageCount);
            scrollOffset = Math.max(0, scrollOffset);
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            } else if (keyCode == GLFW_KEY_ESCAPE) {
                this.client.openScreen(null);
                return true;
            } else if (keyCode != GLFW_KEY_ENTER && keyCode != GLFW_KEY_KP_ENTER) {
                return true;
            } else {
                String string = this.chatField.getText().trim();
                if (!string.isEmpty()) {
                    this.sendMessage(string);
                }

                this.client.openScreen(null);
                return true;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            int scaledMouseX = (int) mouseX - x;
            scaledMouseX /= scale;
            scaledMouseX += x;

            int scaledMouseY = (int) mouseY - y;
            scaledMouseY /= scale;
            scaledMouseY += y;

            if (this.commandSuggestor.mouseClicked((int) mouseX, (int) mouseY, button)) {
                return true;
            } else {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
//                    ChatHud chatHud = this.client.inGameHud.getChatHud();
//                    if (chatHud.mouseClicked(mouseX, mouseY)) {
//                        return true;
//                    }
//
//                    Style style = chatHud.getText(mouseX, mouseY);
//                    if (style != null && this.handleTextClick(style)) {
//                        return true;
//                    }
                }

                return this.chatField.mouseClicked(scaledMouseX, scaledMouseY, button) || super.mouseClicked(mouseX, mouseY, button);
            }
        }

        @Override
        protected void insertText(String text, boolean override) {
            if (override) {
                this.chatField.setText(text);
            } else {
                this.chatField.write(text);
            }
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            beginRender(matrices);


            this.setFocused(this.chatField);
            this.chatField.setSelected(true);

            this.chatField.render(matrices, mouseX, mouseY, delta);
            this.commandSuggestor.render(matrices, mouseX, mouseY);


            super.render(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            //TODO try manually keeping track of start click position and update our own delta from it
            // maybe "drag-drift" and other dragging-related bugs are caused by deltaX and deltaY being inaccurate;
            x += deltaX;
            y += deltaY;

            fixAlignment();
            String string = this.chatField.getText(); //FIXME: what does this do? is it necessary?
            this.init(client, width, height);

            this.chatField.x = ChatWindow.this.x + 4;
            this.chatField.y = ChatWindow.this.y + ChatWindow.this.height - 12;

            this.chatField.setText(string); //FIXME: what does this do? is it necessary?
            this.commandSuggestor.refresh();
            return true;
        }
    }
}
