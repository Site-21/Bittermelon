package com.site21.bittermelon.common.systems.ai.behavior.attack;

import com.site21.bittermelon.common.content.entities.ragdoll.RagdollEntity;
import com.site21.bittermelon.common.systems.character.Character;
import com.site21.bittermelon.common.systems.character.CharacterManager;
import com.site21.bittermelon.common.systems.stumble.StumbleHandler;
import com.site21.bittermelon.util.LocalMessageUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.util.BrainUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToDoubleBiFunction;

import static com.site21.bittermelon.init.neoforge.BitterSounds.DRAG;

public class Pull<E extends Mob> extends AnimatableMeleeAttack<E> {
    private ToDoubleBiFunction<E, LivingEntity> dragStrength = (_, _) -> 5.0;

    public Pull(int delayTicks) {
        super(delayTicks);
    }

    public void dragStrength(ToDoubleBiFunction<E, LivingEntity> dragStrength) {
        this.dragStrength = dragStrength;
    }

    public void dragStrength(double strength) {
        this.dragStrength = (_, _) -> strength;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        super.checkExtraStartConditions(level, entity);

        assert target != null;
        return StumbleHandler.isStumbled(target) && !StumbleHandler.isStumbled(entity);
    }

    @Override
    protected void doDelayedAction(@NotNull E entity) {
        BrainUtil.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, attackInterval.applyAsInt(entity, target));

        if (target == null || !(target.getVehicle() instanceof RagdollEntity ragdoll)) return;
        if (!entity.getSensing().hasLineOfSight(ragdoll)) return;

        double strength = dragStrength.applyAsDouble(entity, target);
        Vec3 pullDirection = entity.getLookAngle().multiply(-strength, 1, -strength);
        ragdoll.addMotion(pullDirection);

        entity.level().playSound(null, entity.getOnPos(), DRAG.value(), SoundSource.AMBIENT);
        sendPullMessage(entity, target);
    }

    private void sendPullMessage(E entity, Entity target) {
        CharacterManager characterManager = CharacterManager.get(entity.level());
        Character entityCharacter = characterManager.getActiveCharacter(entity);
        Character targetCharacter = characterManager.getActiveCharacter(target);

        if (entityCharacter != null && targetCharacter != null) {
            int textColor = entityCharacter.getEmoteColor();

            LocalMessageUtil.sendLocalMessage(entity, 10, Component.literal(
                    entityCharacter.getName() + " pulls " + targetCharacter.getName() + ".").withColor(textColor));
        }
    }
}
