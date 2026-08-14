package kovsl.aimassist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class AimConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("kovsl_aimassist.json");

    public boolean mobs = true;
    public boolean players = true;
    public boolean invisiblePlayers = false;
    public boolean nakedInvisiblePlayers = false;
    public boolean npcs = false;

    public double strength = 50.0;
    public double range = 5.0;
    public double fov = 90.0;

    public static AimConfig load() {
        try {
            if (Files.exists(FILE)) {
                AimConfig result = GSON.fromJson(Files.readString(FILE), AimConfig.class);
                if (result != null) {
                    result.clamp();
                    return result;
                }
            }
        } catch (Exception ignored) {
        }
        return new AimConfig();
    }

    public void save() {
        clamp();
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (Exception ignored) {
        }
    }

    public void clamp() {
        strength = Math.max(0.0, Math.min(100.0, strength));
        range = Math.max(1.0, Math.min(20.0, range));
        fov = Math.max(1.0, Math.min(180.0, fov));
    }
}