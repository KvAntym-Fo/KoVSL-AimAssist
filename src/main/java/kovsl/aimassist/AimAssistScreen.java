package kovsl.aimassist;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public final class AimAssistScreen extends Screen {
    private final Screen parent;
    private final AimConfig config;

    private ButtonWidget mobs;
    private ButtonWidget players;
    private ButtonWidget invisible;
    private ButtonWidget nakedInvisible;
    private ButtonWidget npcs;

    public AimAssistScreen(Screen parent, AimConfig config) {
        super(Text.literal("KoVSL Aim Assist"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = 55;

        mobs = addDrawableChild(ButtonWidget.builder(label("Mobs", config.mobs), b -> {
            config.mobs = !config.mobs;
            b.setMessage(label("Mobs", config.mobs));
        }).dimensions(cx - 100, y, 200, 20).build());
        y += 25;

        players = addDrawableChild(ButtonWidget.builder(label("Players", config.players), b -> {
            config.players = !config.players;
            b.setMessage(label("Players", config.players));
        }).dimensions(cx - 100, y, 200, 20).build());
        y += 25;

        invisible = addDrawableChild(ButtonWidget.builder(label("Invisible players", config.invisiblePlayers), b -> {
            config.invisiblePlayers = !config.invisiblePlayers;
            b.setMessage(label("Invisible players", config.invisiblePlayers));
        }).dimensions(cx - 100, y, 200, 20).build());
        y += 25;

        nakedInvisible = addDrawableChild(ButtonWidget.builder(label("Naked invisible", config.nakedInvisiblePlayers), b -> {
            config.nakedInvisiblePlayers = !config.nakedInvisiblePlayers;
            b.setMessage(label("Naked invisible", config.nakedInvisiblePlayers));
        }).dimensions(cx - 100, y, 200, 20).build());
        y += 25;

        npcs = addDrawableChild(ButtonWidget.builder(label("NPCs", config.npcs), b -> {
            config.npcs = !config.npcs;
            b.setMessage(label("NPCs", config.npcs));
        }).dimensions(cx - 100, y, 200, 20).build());
        y += 32;

        addDrawableChild(new DecimalSlider(cx - 100, y, 200, 20,
                "Strength", config.strength, 0.0, 100.0, 0.1,
                value -> config.strength = value));
        y += 27;

        addDrawableChild(new DecimalSlider(cx - 100, y, 200, 20,
                "Range", config.range, 1.0, 20.0, 0.1,
                value -> config.range = value));
        y += 27;

        addDrawableChild(new DecimalSlider(cx - 100, y, 200, 20,
                "FOV", config.fov, 1.0, 180.0, 1.0,
                value -> config.fov = value));
        y += 32;

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
            config.save();
            close();
        }).dimensions(cx - 100, y, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close())
                .dimensions(cx + 5, y, 95, 20).build());
    }

    private static Text label(String name, boolean value) {
        return Text.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 25, 0xFFFFFF);
        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("Toggle: Right Shift  |  Settings: Right Ctrl"),
                width / 2, 40, 0xAAAAAA
        );
        super.render(context, mouseX, mouseY, delta);
    }

    private static final class DecimalSlider extends SliderWidget {
        private final String name;
        private final double min;
        private final double max;
        private final double step;
        private final java.util.function.DoubleConsumer consumer;

        DecimalSlider(int x, int y, int width, int height, String name,
                      double value, double min, double max, double step,
                      java.util.function.DoubleConsumer consumer) {
            super(x, y, width, height, Text.empty(),
                    (value - min) / (max - min));
            this.name = name;
            this.min = min;
            this.max = max;
            this.step = step;
            this.consumer = consumer;
            updateMessage();
        }

        private double current() {
            double raw = min + value * (max - min);
            return Math.round(raw / step) * step;
        }

        @Override
        protected void updateMessage() {
            double v = current();
            int decimals = step < 1.0 ? 1 : 0;
            setMessage(Text.literal(String.format(java.util.Locale.ROOT,
                    "%s: %." + decimals + "f", name, v)));
        }

        @Override
        protected void applyValue() {
            double v = MathHelper.clamp(current(), min, max);
            consumer.accept(v);
        }
    }
}