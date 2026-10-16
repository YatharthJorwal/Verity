package verity.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import verity.entity.VerityEntity;
import verity.entity.VerityMode;

/**
 * Debug and control commands.
 * /verity mode <wander|follow|stay> <targets>
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

			dispatcher.register(Commands.literal("verity")
					.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
					.then(modeNode));
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
}
