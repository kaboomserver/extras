package pw.kaboom.extras.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public final class CommandGetJSONMM implements CommandExecutor {
    public boolean onCommand(final @Nonnull CommandSender sender,
                             final @Nonnull Command command,
                             final @Nonnull String label,
                             final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("Usage: /" + label + " <message ..>", NamedTextColor.RED));
            return true;
        }

        final String message = String.join(" ", args);
        Component createdComponent = MiniMessage.miniMessage()
                .deserialize(message);

        String asJson = GsonComponentSerializer.gson()
                .serialize(createdComponent);

        Component feedback = Component.empty()
                .append(Component.text("Your component as JSON (click to copy): "))
                .append(Component.text(asJson, NamedTextColor.GREEN))
                .clickEvent(ClickEvent.copyToClipboard(asJson));

        sender.sendMessage(feedback);
        return true;
    }
}
