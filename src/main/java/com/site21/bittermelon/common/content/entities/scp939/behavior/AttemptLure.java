package com.site21.bittermelon.common.content.entities.scp939.behavior;

import com.site21.bittermelon.common.content.entities.scp939.SCP939;
import com.site21.bittermelon.common.content.entities.scp939.lure.LureType;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryCondition;
import net.tslat.smartbrainlib.api.core.behaviour.base.ExtendedBehaviour;

import java.util.Set;

public class AttemptLure extends ExtendedBehaviour<SCP939> {
    @Override
    public Set<MemoryCondition<?, ?>> getMemoryRequirements() {
        return Set.of();
    }

    @Override
    protected void start(SCP939 entity) {
        entity.getLureSystem().attemptLure(entity, LureType.GENERIC);
    }
}
