package com.site21.bittermelon.common.content.entities.scp939.lure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterUtil;
import com.site21.bittermelon.util.LocalMessageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LureSystem {
    public static final Codec<LureSystem> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(UUIDUtil.CODEC)
                            .fieldOf("characters")
                            .forGetter(LureSystem::getCharacters)
            ).apply(instance, LureSystem::new)
    );

    private static final List<FakeCharacter> FALLBACK_CHARACTERS = List.of(
            new FakeCharacter("John", 0xFF0000),
            new FakeCharacter("Bob", 0x00FF00),
            new FakeCharacter("Jim", 0x0000FF),
            new FakeCharacter("Tim", 0xFFFF00)
    );

    private static final Map<LureType, List<LurePool>> pools;
    private List<UUID> characters;
    private @Nullable LureScene activeScene;
    private long lastLure = 0;
    private int interval = 0;

    public LureSystem(List<UUID> characters) {
        this.characters = characters;
    }

    public LureSystem() {
        characters = new ArrayList<>();
    }

    public LureScene createScene(Entity entity, LureType type) {
        RandomSource random = entity.getRandom();
        List<LurePool> poolList = pools.get(type);
        LurePool pool = poolList.get(random.nextInt(poolList.size()));
        List<LureDialogue> lines = new ArrayList<>();
        for (int i = 0; i < pool.dialogue().length; i++) {
            LureDialogue dialogue;
            List<String> messages = new ArrayList<>(Arrays.asList(pool.dialogue()[i]));
            if (characters.isEmpty()) {
                dialogue = getFallbackDialogue(random, messages);
            } else {
                UUID uuid = characters.get(random.nextInt(characters.size()));
                Character character = CharacterUtil.getCharacter(entity.level(), uuid);
                if (character == null) {
                    dialogue = getFallbackDialogue(random, messages);
                } else {
                    dialogue = new LureDialogue(character.getName(), character.getEmoteColor(), messages);
                }
            }
            lines.add(dialogue);
        }
        return new LureScene(type, lines, pool);
    }

    private LureDialogue getFallbackDialogue(RandomSource random, List<String> messages) {
        FakeCharacter character = FALLBACK_CHARACTERS.get(random.nextInt(FALLBACK_CHARACTERS.size()));
        return new LureDialogue(character.name(), character.color(), messages);
    }

    public void attemptLure(Entity entity, LureType type) {
        if (activeScene == null || activeScene.type() != type) {
            activeScene = createScene(entity, LureType.GENERIC);
        }

        Level level = entity.level();
        assert activeScene != null;
        LurePool pool = activeScene.pool();
        if (level.getGameTime() - lastLure < interval) return;

        RandomSource random = entity.getRandom();
        if (random.nextFloat() < 0.1 && pool.sounds().length > 0) {
            playRandomSound(entity, random, pool);
        } else {
            makeRandomLure(entity, random);
        }

        lastLure = level.getGameTime();
        interval = pool.interval() + random.nextInt(pool.additionalRandomInterval());
    }

    private static void playRandomSound(Entity entity, RandomSource random, LurePool pool) {
        SoundEvent[] sounds = pool.sounds();
        SoundEvent sound = sounds[random.nextInt(sounds.length)];
        entity.playSound(sound);
    }

    private void makeRandomLure(Entity entity, RandomSource random) {
        assert activeScene != null;
        int i = random.nextInt(activeScene.lines().size());
        LureDialogue dialogue = activeScene.lines().get(i);
        String message = dialogue.messages().remove(random.nextInt(dialogue.messages().size()));

        Component component = Component.literal(dialogue.characterName() + " says, ")
                .withColor(dialogue.emoteColor())
                .append(Component.literal("\"" + message + "\"").withStyle(ChatFormatting.WHITE));
        LocalMessageUtil.sendLocalMessage(entity, 16, component);

        if (dialogue.messages().isEmpty()) {
            activeScene.lines().remove(i);
        }

        if (activeScene.lines().isEmpty()) {
            activeScene = null;
        }
    }

    public List<UUID> getCharacters() {
        return characters;
    }

    static {
        pools = new HashMap<>();
        pools.put(LureType.GENERIC, List.of(
                new LurePool(
                        new String[][]{
                                {"Hello there!", "How are you doing?", "Nice to meet you!"},
                                {"I hope you're having a good day.", "Stay safe out there!", "Take care!"}
                        },
                        new SoundEvent[]{
                        },
                        100,
                        50
                )
        ));
    }

    private record FakeCharacter(String name, int color) {
    }
}
