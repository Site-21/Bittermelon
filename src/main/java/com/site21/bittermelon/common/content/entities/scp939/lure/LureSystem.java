package com.site21.bittermelon.common.content.entities.scp939.lure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterUtil;
import com.site21.bittermelon.util.LocalMessageUtil;
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
            UUID uuid = characters.get(random.nextInt(characters.size()));
            lines.add(new LureDialogue(uuid, new ArrayList<>(Arrays.asList(pool.dialogue()[i]))));
        }
        return new LureScene(type, lines, pool);
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

        Character character = CharacterUtil.getCharacter(entity.level(), dialogue.character());
        if (character == null) {
            activeScene = null;
            return;
        }

        Component component = Component.literal(character.getName() + " " + message);
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
                        new String[][] {
                                {"Hello there!", "How are you doing?", "Nice to meet you!"},
                                {"I hope you're having a good day.", "Stay safe out there!", "Take care!"}
                        },
                        new SoundEvent[] {
                        },
                        100,
                        50
                )
        ));
    }
}
