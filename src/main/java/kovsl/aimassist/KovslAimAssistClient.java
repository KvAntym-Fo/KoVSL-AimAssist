package kovsl.aimassist;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

public final class KovslAimAssistClient implements ClientModInitializer {
    public static final String MOD_ID = "kovsl_aimassist";

    private static KeyBinding toggleKey;
    private static KeyBinding configKey;

    private static boolean enabled = false;
    private static AimConfig config;

    @Override
    public void onInitializeClient() {
        config = AimConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kovsl_aimassist.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.kovsl_aimassist"
        ));

        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kovsl_aimassist.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "category.kovsl_aimassist"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("Aim Assist: " + (enabled ? "ON" : "OFF")),
                            true
                    );
                }
            }

            while (configKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new AimAssistScreen(null, config));
                }
            }

            if (enabled) {
                AimController.tick(client, config);
            }
        });
    }

    public static boolean isNpc(LivingEntity entity) {
        return entity instanceof VillagerEntity
                || entity instanceof WanderingTraderEntity
                || entity instanceof IronGolemEntity;
    }

    public static boolean isNaked(PlayerEntity player) {
        for (var stack : player.getArmorItems()) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public static Box searchBox(MinecraftClient client, double range) {
        var p = client.player;
        return new Box(
                p.getX() - range, p.getY() - range, p.getZ() - range,
                p.getX() + range, p.getY() + range, p.getZ() + range
        );
    }

    public static boolean isEnabled() {
        return enabled;
    }
}