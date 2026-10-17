package verity.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import verity.Verity;
import verity.ai.OllamaClient;
import verity.config.VerityConfig;
import verity.entity.VerityEntity;
import verity.entity.VerityMode;

/**
 * Debug and control commands.
 * /verity mode (wander|follow|stay) targets
 * /verity status targets
 * /verity ping   (sends a test message to Ollama and prints the answer)
 * Example: /verity mode stay @e[type=verity:verity]
 */
public class VerityCommands {
	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			LiteralArgumentBuilder<CommandSourceStack> modeNode = Commands.literal("mode");
			for (VerityMode mode : VerityMode.values()) {
				modeNode.then(Commands.literal(mode.id())
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> setMode(context, mode))));
			}

			LiteralArgumentBuilder<CommandSourceStack> statusNode = Commands.literal("status")
					.then(Commands.argument("targets", EntityArgument.entities())
							.executes(VerityCommands::status));

			dispatcher.register(Commands.literal("verity")
					.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
					.then(modeNode)
					.then(statusNode)
					.then(Commands.literal("ping").executes(VerityCommands::ping)));
		});
	}

	private static int setMode(CommandContext<CommandSourceStack> context, VerityMode mode) throws CommandSyntaxException {
		int changed = 0;
		for (Entity entity : EntityArgument.getEntities(context, "targets")) {
			if (entity instanceof VerityEntity verity) {
				verity.setMode(mode);
				changed++;
			}
		}
		int total = changed;
		context.getSource().sendSuccess(() -> Component.literal("Set " + total + " Verity to " + mode.id()), true);
		return total;
	}

	private static int status(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		int found = 0;
		for (Entity entity : EntityArgument.getEntities(context, "targets")) {
			if (entity instanceof VerityEntity verity) {
				found++;
				String line = "Verity at " + verity.blockPosition().toShortString()
						+ ": mode=" + verity.getMode().id()
						+ ", moving=" + !verity.getNavigation().isDone();
				context.getSource().sendSuccess(() -> Component.literal(line), false);
			}
		}
		return found;
	}

	private static int ping(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		MinecraftServer server = source.getServer();
		source.sendSuccess(() -> Component.literal("Asking Ollama (" + VerityConfig.get().ollamaModel + ")..."), false);

		OllamaClient.chat("You are Verity, a tiny smiley cube in Minecraft. Reply with one short sentence.", "Say hello.")
				.whenComplete((reply, error) -> server.execute(() -> {
					if (error != null) {
						Throwable cause = error.getCause() != null ? error.getCause() : error;
						String detail = cause.getClass().getSimpleName() + (cause.getMessage() != null ? ": " + cause.getMessage() : "");
						Verity.LOGGER.warn("Ollama ping failed", cause);
						source.sendFailure(Component.literal("Ollama failed: " + detail));
					} else {
						String text = reply.content().replace('\n', ' ');
						if (text.length() > 300) {
							text = text.substring(0, 300) + "...";
						}
						Verity.LOGGER.info("Ollama replied in {} ms", reply.millis());
						String shown = text;
						source.sendSuccess(() -> Component.literal("Ollama replied in " + reply.millis() + " ms: " + shown), false);
					}
				}));
		return 1;
	}
}
