package kovsl.aimassist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

public final class AimController {
    private AimController() {}

    public static void tick(MinecraftClient client, AimConfig config) {
        if (client.player == null || client.world == null || client.currentScreen != null) return;
        if (config.strength <= 0.0) return;

        LivingEntity target = findTarget(client, config);
        if (target == null) return;

        Vec3d aimPoint = target.getEyePos();
        double dx = aimPoint.x - client.player.getX();
        double dy = aimPoint.y - client.player.getEyeY();
        double dz = aimPoint.z - client.player.getZ();

        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float wantedYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float wantedPitch = (float)(-Math.toDegrees(Math.atan2(dy, horizontal)));

        float yawDelta = MathHelper.wrapDegrees(wantedYaw - client.player.getYaw());
        float pitchDelta = wantedPitch - client.player.getPitch();

        double strength = config.strength / 100.0;

        // Smoothness: even at 100% the camera is moved progressively,
        // rather than snapping directly to the target.
        float maxYawStep = (float)(2.0 + 12.0 * strength);
        float maxPitchStep = (float)(1.5 + 9.0 * strength);

        float yawStep = clampStep(yawDelta, maxYawStep);
        float pitchStep = clampStep(pitchDelta, maxPitchStep);

        // Strength controls how much of the allowed step is applied.
        float multiplier = (float)(0.25 + 0.75 * strength);
        client.player.setYaw(client.player.getYaw() + yawStep * multiplier);
        client.player.setPitch(MathHelper.clamp(
                client.player.getPitch() + pitchStep * multiplier,
                -90.0f, 90.0f
        ));
    }

    private static float clampStep(float delta, float max) {
        return MathHelper.clamp(delta, -max, max);
    }

    private static LivingEntity findTarget(MinecraftClient client, AimConfig config) {
        var player = client.player;
        var box = KovslAimAssistClient.searchBox(client, config.range);

        return client.world.getEntitiesByClass(
                LivingEntity.class,
                box,
                entity -> isValidTarget(client, entity, config)
        ).stream()
                .filter(entity -> {
                    double angle = angleTo(player, entity);
                    return angle <= config.fov / 2.0;
                })
                .filter(entity -> client.player.canSee(entity))
                .min(Comparator.comparingDouble(entity -> score(player, entity)))
                .orElse(null);
    }

    private static boolean isValidTarget(MinecraftClient client, LivingEntity entity, AimConfig config) {
        if (entity == client.player || !entity.isAlive()) return false;
        if (entity instanceof ArmorStandEntity) return false;

        if (entity instanceof PlayerEntity player) {
            if (player.isInvisible()) {
                if (KovslAimAssistClient.isNaked(player)) {
                    return config.nakedInvisiblePlayers;
                }
                return config.invisiblePlayers;
            }
            return config.players;
        }

        if (KovslAimAssistClient.isNpc(entity)) {
            return config.npcs;
        }

        return config.mobs;
    }

    private static double score(PlayerEntity player, LivingEntity entity) {
        double distance = player.distanceTo(entity);
        double angle = angleTo(player, entity);
        // Distance is dominant, angle is a tie-breaker.
        return distance + angle * 0.02;
    }

    private static double angleTo(PlayerEntity player, LivingEntity entity) {
        Vec3d look = player.getRotationVec(1.0f).normalize();
        Vec3d direction = entity.getEyePos().subtract(player.getEyePos()).normalize();
        double dot = MathHelper.clamp(look.dotProduct(direction), -1.0, 1.0);
        return Math.toDegrees(Math.acos(dot));
    }
}