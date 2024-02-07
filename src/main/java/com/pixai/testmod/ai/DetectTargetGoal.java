package com.pixai.testmod.ai;

import com.pixai.testmod.entity.AlertableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.Iterator;
import java.util.List;

public class DetectTargetGoal extends TargetGoal {
    public DetectTargetGoal(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Override
    public boolean canUse() {
        return this.mob instanceof AlertableMob && ((AlertableMob) this.mob).canAlert() && !((AlertableMob) this.mob).isAlerted();
    }

    protected void alertOthers() {
        double followDistance = this.getFollowDistance();
        AABB detectArea = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(followDistance, 10.0, followDistance);
        List<? extends Mob> entitiesDetected = this.mob.level.getEntitiesOfClass(this.mob.getClass(), detectArea, EntitySelector.NO_SPECTATORS);
        Iterator iter = entitiesDetected.iterator();

        while (true) {
            Mob entity;
            do {
                do {
                    do {
                        if (!iter.hasNext()) {
                            return;
                        }

                        entity = (Mob) iter.next();
                    } while (this.mob == entity);
                } while (!(entity instanceof AlertableMob));
            }while(entity.getTarget() != null);

            this.alertOther(entity, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob p_26042_, LivingEntity p_26043_) {
        p_26042_.setTarget(p_26043_);
    }
}
