package pw.kaboom.extras.helpers;

import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class MessageInterceptingCommandRunner implements ConsoleCommandSender {
    private final ConsoleCommandSender wrappedSender;
    private final Spigot spigotWrapper;

    private class Spigot extends CommandSender.Spigot {
        /**
         * Sends this sender a chat component.
         *
         * @param component the components to send
         */
        @Override
		public final void sendMessage(final @NotNull net.md_5.bungee.api.chat.BaseComponent component) {
            wrappedSender.spigot().sendMessage();
        }

        /**
         * Sends an array of components as a single message to the sender.
         *
         * @param components the components to send
         */
        @Override
		public final void sendMessage(final @NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
            wrappedSender.spigot().sendMessage(components);
        }
    }

    public MessageInterceptingCommandRunner(final ConsoleCommandSender wrappedSenderIn) {
        this.wrappedSender = wrappedSenderIn;
        spigotWrapper = new Spigot();
    }

    @Override
    public final void sendMessage(final @NotNull String message) {
        wrappedSender.sendMessage(message.substring(0, Math.min(message.length(), 256)));
    }

    @Override
    public final void sendMessage(final @NotNull String[] messages) {
        wrappedSender.sendMessage(messages);
    }

    @Override
    public final @NotNull Server getServer() {
        return wrappedSender.getServer();
    }

    @Override
    public final @NotNull String getName() {
        return "OrderFulfiller";
    }

    @Override
    public final @NotNull CommandSender.Spigot spigot() {
        return spigotWrapper;
    }

    @Override
    public final boolean isConversing() {
        return wrappedSender.isConversing();
    }

    @Override
    public final void acceptConversationInput(final @NotNull String input) {
        wrappedSender.acceptConversationInput(input);
    }

    @Override
    public final boolean beginConversation(final @NotNull Conversation conversation) {
        return wrappedSender.beginConversation(conversation);
    }

    @Override
    public final void abandonConversation(final @NotNull Conversation conversation) {
        wrappedSender.abandonConversation(conversation);
    }

    @Override
    public final void abandonConversation(final @NotNull Conversation conversation, final @NotNull ConversationAbandonedEvent details) {
        wrappedSender.abandonConversation(conversation, details);
    }

    @Override
    public final void sendRawMessage(final @NotNull String message) {
        wrappedSender.sendRawMessage(message.substring(0, Math.min(message.length(), 256)));
    }

    @Override
    public final boolean isPermissionSet(final @NotNull String name) {
        return wrappedSender.isPermissionSet(name);
    }

    @Override
    public final boolean isPermissionSet(final @NotNull Permission perm) {
        return wrappedSender.isPermissionSet(perm);
    }

    @Override
    public final boolean hasPermission(final @NotNull String name) {
        return wrappedSender.hasPermission(name);
    }

    @Override
    public final boolean hasPermission(final @NotNull Permission perm) {
        return wrappedSender.hasPermission(perm);
    }

    @Override
    public final @NotNull PermissionAttachment addAttachment(final @NotNull Plugin plugin, final @NotNull String name, final boolean value) {
        return wrappedSender.addAttachment(plugin, name, value);
    }

    @Override
    public final @NotNull PermissionAttachment addAttachment(final @NotNull Plugin plugin) {
        return wrappedSender.addAttachment(plugin);
    }

    @Override
    public final @Nullable PermissionAttachment addAttachment(final @NotNull Plugin plugin, final @NotNull String name, final boolean value, final int ticks) {
        return wrappedSender.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public final @Nullable PermissionAttachment addAttachment(final @NotNull Plugin plugin, final int ticks) {
        return wrappedSender.addAttachment(plugin, ticks);
    }

    @Override
    public final void removeAttachment(final @NotNull PermissionAttachment attachment) {
        wrappedSender.removeAttachment(attachment);
    }

    @Override
    public final void recalculatePermissions() {
        wrappedSender.recalculatePermissions();
    }

    @Override
    public final @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return wrappedSender.getEffectivePermissions();
    }

    @Override
    public final boolean isOp() {
        return wrappedSender.isOp();
    }

    @Override
    public final void setOp(final boolean value) {
        wrappedSender.setOp(value);
    }
}