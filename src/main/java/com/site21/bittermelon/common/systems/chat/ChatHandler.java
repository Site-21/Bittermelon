package com.site21.bittermelon.common.systems.chat;

import com.site21.bittermelon.Bittermelon;
import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterManager;
import com.site21.bittermelon.init.custom.VerbSets;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.ACTIVE_CHANNEL;
import static com.site21.bittermelon.init.neoforge.BitterAttachmentTypes.ENRAGED;
import static com.site21.bittermelon.init.neoforge.BitterMobEffects.*;
import static java.lang.Character.isLetter;

@EventBusSubscriber(modid = Bittermelon.MOD_ID)
public class ChatHandler {
    private static final Pattern EMOTE_PATTERN = Pattern.compile("\\*(.*?)\\*|([^*]+)");
    private static final int DEFAULT_GRAY = 0xFF808080;
    private static final List<ChatFilter> FILTERS = List.of();

    public static final int WHISPER_RANGE = 5;
    public static final int NORMAL_RANGE = 16;
    public static final int SHOUT_RANGE = 32;

    @SubscribeEvent
    public static void onChatMessage(@NotNull ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        Character character = CharacterManager.get(player.level()).getActiveCharacter(player);
        String message = event.getMessage().getString();

        event.setCanceled(true);

        if (character == null) {
            player.sendSystemMessage(Component.literal("You must select a valid character before sending a chat message.").withColor(0xFFFF0000));
            return;
        }

        int channel = player.getData(ACTIVE_CHANNEL.get());

        switch (channel) {
            case 0 -> sendRPMessage(character, player, message, NORMAL_RANGE, VerbSets.HUMAN.get());
            case 1 -> sendOOCMessage(player, character, message);
            case 2 -> sendLOOCMessage(player, character, message);
        }
    }

    public static void sendRPMessage(@NotNull Character character, ServerPlayer player, String message, int range, @NotNull VerbSet verbSet) {
        sendRPMessage(character, player, message, range, verbSet.getVerb(message));
    }

    public static void sendOOCMessage(@NotNull ServerPlayer player, @NotNull Character character, String message) {
        String nameFormat = "(OOC) (" + character.getName() + ") " + player.getName().getString() + ": ";
        Component messageComponent = Component.literal(nameFormat + message).withColor(DEFAULT_GRAY);
        sendMessage(messageComponent, player);
    }

    public static void sendLOOCMessage(@NotNull ServerPlayer player, @NotNull Character character, String message) {
        String nameFormat = "(LOOC) (" + character.getName() + ") " + player.getName().getString() + ": ";
        Component messageComponent = Component.literal(nameFormat + message).withColor(DEFAULT_GRAY);
        sendMessage(messageComponent, player, 16);
    }

    public static void sendMessage(Component message, @NotNull ServerPlayer player, int range) {
        for (ServerPlayer serverPlayer : player.level().players()) {
            if (player.distanceTo(serverPlayer) <= range) {
                serverPlayer.sendSystemMessage(message);
            }
        }
    }

    public static void sendMessage(Component message, @NotNull ServerPlayer player) {
        for (ServerPlayer serverPlayer : player.level().players()) {
            serverPlayer.sendSystemMessage(message);
        }
    }

    public static void sendRPMessage(@NotNull Character character, ServerPlayer player, @NotNull String message, int range, String verb) {
        if (player.hasEffect(ELECTROCUTED) || player.hasEffect(TASERED)) {
            message = formatElectrocuted(message, player.getRandom());
        }

        if (player.getData(ENRAGED)) {
            message = formatRage(message);
            verb = VerbSets.HUMAN.get().shoutingVerb();
            range = SHOUT_RANGE;
        }

        RPMessage msg = new RPMessage(character, player, verb, parse(message), range);
        dispatch(msg);
    }

    private static List<ChatSegment> parse(String message) {
        List<ChatSegment> segments = new ArrayList<>();
        Matcher matcher = EMOTE_PATTERN.matcher(message);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                segments.add(new ChatSegment.Emote(matcher.group(1)));
            } else {
                String dialogue = matcher.group(2).trim();
                if (!dialogue.isEmpty()) segments.add(new ChatSegment.Dialogue(dialogue));
            }
        }
        return segments;
    }

    public static void dispatch(RPMessage msg) {
        if (!(msg.source.level() instanceof ServerLevel level)) return;

        if (msg.range() > WHISPER_RANGE && msg.hasDialogue()) {
            level.gameEvent(GameEvent.ENTITY_ACTION, msg.source().position(), GameEvent.Context.of(msg.source()));
        }

        for (ServerPlayer recipient : level.players()) {
            double distance = msg.source().distanceTo(recipient);
            if (distance > msg.range()) continue;

            RPMessage personal = msg;
            for (ChatFilter filter : FILTERS) {
                personal = filter.apply(personal, recipient);
                if (personal == null) break;
            }
            if (personal == null || personal.segments().isEmpty()) continue;

            float alpha = (float) ((msg.range() - distance) / msg.range());
            recipient.sendSystemMessage(render(personal, recipient, alpha));
        }
    }

    public static Component render(RPMessage msg, ServerPlayer recipient, float alpha) {
        int color = emoteColor(msg, recipient);
        String name = displayName(msg, recipient);

        boolean hasEmote = msg.segments().stream().anyMatch(s -> s instanceof ChatSegment.Emote);
        MutableComponent out = Component.empty();
        out.append(hasEmote
                ? Component.literal("(" + name + ") ").withColor(color)
                : Component.literal(name + " " + msg.verb() + ", ").withColor(color));

        for (ChatSegment segment : msg.segments()) {
            if (segment instanceof ChatSegment.Emote(String text)) {
                out.append(Component.literal(text).withColor(color)).append(" ");
            } else if (segment instanceof ChatSegment.Dialogue(String text)) {
                out.append(Component.literal("\"" + text + "\" ").withColor(0xFFFFFF));
            }
        }

        AlphaContainer alphaContainer = (AlphaContainer) out.getStyle();
        out.setStyle(alphaContainer.bittermelon$withAlpha(alpha));
        return out;
    }

    public static String displayName(RPMessage msg, ServerPlayer recipient) {
        return recipient.hasEffect(AMNESIA) ? "???" : msg.character.getName();
    }

    public static int emoteColor(RPMessage msg, ServerPlayer recipient) {
        return recipient.hasEffect(AMNESIA) ? 0xFFFFFF : msg.character.getEmoteColor();
    }

    private static @NotNull String formatElectrocuted(@NotNull String text, RandomSource random) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (isLetter(c)) {
                if (random.nextFloat() > 0.5f) {
                    result.append(applyElectrocutedEffect(c, random));
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    @Contract(pure = true)
    private static @NotNull String applyElectrocutedEffect(char c, @NotNull RandomSource random) {
        int effect = random.nextIntBetweenInclusive(0, 2);

        switch (effect) {
            case 0 -> {
                return String.valueOf(c).repeat(random.nextIntBetweenInclusive(1, 4));
            }
            case 1 -> {
                return c + "-";
            }
            default -> {
                return c + "--";
            }
        }
    }

    private static String formatRage(String text) {
        return text.toUpperCase();
    }

    public sealed interface ChatSegment {
        record Emote(String text) implements ChatSegment {}
        record Dialogue(String text) implements ChatSegment {}
    }

    public record RPMessage(
            Character character,
            Entity source,
            String verb,
            List<ChatSegment> segments,
            int range
    ) {
        public boolean hasDialogue() {
            return segments.stream().anyMatch(s -> s instanceof ChatSegment.Dialogue);
        }
    }

    @FunctionalInterface
    public interface ChatFilter {
        /** Return modified message, or null to drop it */
        @Nullable RPMessage apply(RPMessage message, ServerPlayer recipient);
    }
}
