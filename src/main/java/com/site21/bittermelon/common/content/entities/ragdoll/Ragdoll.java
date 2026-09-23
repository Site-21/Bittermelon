package com.site21.bittermelon.common.content.entities.ragdoll;

import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.github.stephengold.joltjni.enumerate.EConstraintSpace;
import com.github.stephengold.joltjni.enumerate.EMotionType;
import com.github.stephengold.joltjni.enumerate.ESwingType;
import com.github.stephengold.joltjni.operator.Op;
import com.github.stephengold.joltjni.readonly.ConstShape;

import java.util.ArrayList;
import java.util.List;

import static com.site21.bittermelon.common.physics.PhysicsManager.MOVING;

public class Ragdoll {
    private final PhysicsSystem physicsSystem;
    private final List<Body> parts = new ArrayList<>();
    private final List<Constraint> joints = new ArrayList<>();

    public Ragdoll(PhysicsSystem physicsSystem, Vec3 pos, float yRot) {
        this.physicsSystem = physicsSystem;
        Quat spawnRot = Quat.sRotation(Vec3.sAxisY(), (float) -Math.toRadians(yRot));
        BodyCreationSettings bcs = new BodyCreationSettings();
        bcs.setMotionType(EMotionType.Dynamic);
        bcs.setObjectLayer(MOVING);
        bcs.setFriction(0.5f);
        bcs.setRestitution(0.3f);

        makePart(bcs, 0.25f, 0.25f, 0.25f, pos, new Vec3(0, 0.375f, 0), new Vec3(0.0f, -0.125f, 0.0f), spawnRot, 1, 1);
        makeTorso(bcs, pos, new Vec3(), new Vec3(), spawnRot);
        makeLimb(bcs, pos, new Vec3(0.25f, 0.375f, 0.0f), new Vec3(-0.125f, 0.375f, 0.0f), spawnRot);
        makeLimb(bcs, pos, new Vec3(-0.25f, 0.375f, 0.0f), new Vec3(0.125f, 0.375f, 0.0f), spawnRot);
        makeLimb(bcs, pos, new Vec3(0.125f, -0.375f, 0.0f), new Vec3(0.0f, 0.375f, 0.0f), spawnRot);
        makeLimb(bcs, pos, new Vec3(-0.125f, -0.375f, 0.0f), new Vec3(0.0f, 0.375f, 0.0f), spawnRot);

        connectJoints();
    }

    private void makeTorso(BodyCreationSettings bcs, Vec3 origin, Vec3 attachPoint, Vec3 offset, Quat rot) {
        Shape shape = new BoxShape(new Vec3(0.25f, 0.375f, 0.125f), 0.1f);
        makeBody(bcs, shape, origin, attachPoint, offset, rot);
    }

    private void makeLimb(BodyCreationSettings bcs, Vec3 origin, Vec3 attachPoint, Vec3 offset, Quat rot) {
        makePart(bcs, (float) 0.125, (float) 0.375, (float) 0.125, origin, attachPoint, offset, rot, 1.1f, 0.9f);
    }

    private void makePart(BodyCreationSettings bcs, float xHalfExtent, float yHalfExtent, float zHalfExtent,
                          Vec3 origin, Vec3 attachPoint, Vec3 offset, Quat rot, float topTaper, float bottomTaper) {
        float radius = (xHalfExtent + zHalfExtent) / 2.0f;
        float topRadius = radius * topTaper;
        float bottomRadius = radius * bottomTaper;
        float halfHeight = Math.max(0.1f, yHalfExtent - Math.max(topRadius, bottomRadius));
        TaperedCapsuleShapeSettings settings = new TaperedCapsuleShapeSettings(halfHeight, topRadius, bottomRadius);
        makeBody(bcs, settings.create().get(), origin, attachPoint, offset, rot);
    }

    private void makeBody(BodyCreationSettings bcs, ConstShape shape, Vec3 origin, Vec3 attachPoint, Vec3 offset, Quat rot) {
        bcs.setShape(shape);
        bcs.setPosition(
                origin.getX() + offset.getX() + attachPoint.getX(),
                origin.getY() + offset.getY() + attachPoint.getY(),
                origin.getZ() + offset.getZ() + attachPoint.getZ()
        );
        bcs.setRotation(rot);

        BodyInterface bi = physicsSystem.getBodyInterface();
        Body body = bi.createBody(bcs);
        bi.addBody(body, EActivation.Activate);
        parts.add(body);
    }

    private void connectJoints() {
        SwingTwistConstraintSettings settings = new SwingTwistConstraintSettings();

        // neck
        addJoint(settings, 0, new Vec3(0, 0.375f, 0), new Vec3(0.0f, -0.125f, 0.0f), 80f, 120f, 80f);

        // shoulders
        addJoint(settings, 2, new Vec3(0.25f, 0.375f, 0.0f), new Vec3(-0.125f, 0.375f, 0.0f), 120f, 175f, 175f);
        addJoint(settings, 3, new Vec3(-0.25f, 0.375f, 0.0f), new Vec3(0.125f, 0.375f, 0.0f), 120f, 175f, 175f);

        // hips
        addJoint(settings, 4, new Vec3(0.125f, -0.375f, 0.0f), new Vec3(0.0f, 0.375f, 0.0f), 80f, 140f, 100f);
        addJoint(settings, 5, new Vec3(-0.125f, -0.375f, 0.0f), new Vec3(0.0f, 0.375f, 0.0f), 80f, 140f, 100f);
    }

    private void addJoint(SwingTwistConstraintSettings settings, int i, Vec3 attachmentPoint, Vec3 pivot, float twist, float swingY, float swingZ) {
        settings.setSpace(EConstraintSpace.LocalToBodyCom);
        settings.setSwingType(ESwingType.Pyramid);
        settings.setPosition1(attachmentPoint.toRVec3());
        settings.setPosition2(pivot.toRVec3());

        Vec3 primaryAxis = new com.github.stephengold.joltjni.Vec3(0, 1, 0);
        Vec3 secondaryAxis = new com.github.stephengold.joltjni.Vec3(1, 0, 0);
        settings.setTwistAxis1(primaryAxis);
        settings.setPlaneAxis1(secondaryAxis);
        settings.setTwistAxis2(primaryAxis);
        settings.setPlaneAxis2(secondaryAxis);

        settings.setTwistMinAngle((float) Math.toRadians(-twist));
        settings.setTwistMaxAngle((float) Math.toRadians(twist));
        settings.setPlaneHalfConeAngle((float) Math.toRadians(swingY));
        settings.setNormalHalfConeAngle((float) Math.toRadians(swingZ));

        Body torso = parts.get(1);
        Body body = parts.get(i);
        TwoBodyConstraint constraint = settings.create(torso, body);
        joints.add(constraint);
        physicsSystem.addConstraint(constraint);
    }

    public Body getPart(int i) {
        return parts.get(i);
    }

    public void addUniformVelocity(Vec3 delta) {
        BodyInterface bi = physicsSystem.getBodyInterface();
        for (Body body : parts) {
            bi.setLinearVelocity(body.getId(), Op.plus(bi.getLinearVelocity(body.getId()), delta));
            bi.activateBody(body.getId());
        }
    }

    public void addVelocity(int partIndex, Vec3 delta) {
        BodyInterface bi = physicsSystem.getBodyInterface();
        Body body = parts.get(partIndex);
        bi.setAngularVelocity(body.getId(), Op.plus(bi.getAngularVelocity(body.getId()), delta));
        bi.activateBody(body.getId());
    }

    public void destroy() {
        BodyInterface bi = physicsSystem.getBodyInterface();
        for (Body body : parts) {
            bi.removeBody(body.getId());
            bi.destroyBody(body.getId());
        }
        for (Constraint joint : joints) {
            physicsSystem.removeConstraint(joint);
        }
    }
}
