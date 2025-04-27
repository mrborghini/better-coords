package mrborghini.bettercoords;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class BetterCoordsClient implements ClientModInitializer {
	private static final int TEXT_SPACING = 10;
	public static final String MOD_ID = "better-coords";
	private static final Identifier EXAMPLE_LAYER = Identifier.of(BetterCoordsClient.MOD_ID, "better-coords");
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer
				.attachLayerBefore(IdentifiedLayer.CHAT, EXAMPLE_LAYER, BetterCoordsClient::render));

	}

	private static String getBiome(MinecraftClient client) {
		BlockPos playerPos = client.player.getBlockPos();
		return client.world.getBiome(playerPos).getKey().get().getValue().toString().split("minecraft:")[1];
	}

	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null || client.getWindow() == null) {
			return;
		}

		int screenHeight = client.getWindow().getScaledHeight();
		int startingY = screenHeight - (TEXT_SPACING * 2);

		String dimension = client.player.getWorld().getRegistryKey().getValue().toString().split("minecraft:")[1]
				.toLowerCase()
				.trim();

		int x = (int) client.player.getX();
		int y = (int) client.player.getY();
		int z = (int) client.player.getZ();
		String biome = getBiome(client);

		String currentWorldText = String.format("%s: (%d, %d, %d) %s", dimension, x, y, z, biome);
		context.drawText(client.textRenderer, currentWorldText, 0, startingY, 0xFFFFFFFF, false);
		startingY -= TEXT_SPACING;

		if ("the_nether".equals(dimension)) {
			drawText(client, context, x * 8, y, z * 8, "overworld", startingY);
		}

		if ("overworld".equals(dimension)) {
			drawText(client, context, x / 8, y, z / 8, "the_nether", startingY);
		}
	}

	private static void drawText(MinecraftClient client, DrawContext context, int x, int y, int z, String dimension,
			int height) {
		String text = String.format("%s: (%d, %d, %d)", dimension, x, y, z);
		context.drawText(client.textRenderer, text, 0, height, 0xFFFFFFFF, false);
	}
}
