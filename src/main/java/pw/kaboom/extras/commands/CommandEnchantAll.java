package pw.kaboom.extras.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CommandEnchantAll implements BrigadierCommand {
    private static final SimpleCommandExceptionType EX_NOT_PLAYER =
            new SimpleCommandExceptionType(
                    new LiteralMessage("This command must be called by a player"));
    private static final SimpleCommandExceptionType EX_NO_ITEM =
            new SimpleCommandExceptionType(
                    new LiteralMessage("Please hold an item in your hand to enchant it"));

    @Override
    public String getLabel() {
        return "enchantall";
    }

    @Override
    public String getDescription() {
        return "Adds every enchantment to a held item";
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .requires(src ->
                        src.getSender() instanceof Player
                                && src.getSender().hasPermission("extras.enchantall")
                )
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof final Player player)) {
                        // should be impossible, see above
                        throw EX_NOT_PLAYER.create();
                    }

                    final ItemStack item = player.getInventory().getItemInMainHand();

                    if (Material.AIR.equals(item.getType())) {
                        throw EX_NO_ITEM.create();
                    }

                    final Registry<Enchantment> registry = RegistryAccess.registryAccess()
                            .getRegistry(RegistryKey.ENCHANTMENT);
                    for (final Enchantment enchantment : registry) {
                        item.addUnsafeEnchantment(enchantment, Short.MAX_VALUE);
                    }
                    player.sendMessage(Component.text("I killed Martin."));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
