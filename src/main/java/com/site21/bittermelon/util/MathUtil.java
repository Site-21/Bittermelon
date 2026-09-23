package com.site21.bittermelon.util;

import com.github.stephengold.joltjni.Quat;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
    public static Quat slerp(Quat a, Quat b, float t) {
        float ax = a.getX(), ay = a.getY(), az = a.getZ(), aw = a.getW();
        float bx = b.getX(), by = b.getY(), bz = b.getZ(), bw = b.getW();
        float dot = ax * bx + ay * by + az * bz + aw * bw;
        if (dot < 0) { bx = -bx; by = -by; bz = -bz; bw = -bw; dot = -dot; }
        float s0, s1;
        if (dot > 0.9995f) {
            s0 = 1 - t; s1 = t;
        } else {
            float theta0 = (float) Math.acos(dot);
            float theta = theta0 * t;
            float sinTheta = (float) Math.sin(theta);
            float sinTheta0 = (float) Math.sin(theta0);
            s0 = (float) Math.cos(theta) - dot * sinTheta / sinTheta0;
            s1 = sinTheta / sinTheta0;
        }
        return new Quat(s0 * ax + s1 * bx, s0 * ay + s1 * by, s0 * az + s1 * bz, s0 * aw + s1 * bw);
    }

    public static Vec3 rotate(Quat q, Vec3 v) {
        float qx = q.getX(), qy = q.getY(), qz = q.getZ(), qw = q.getW();
        float vx = (float) v.x, vy = (float) v.y, vz = (float) v.z;
        float uvx = qy * vz - qz * vy;
        float uvy = qz * vx - qx * vz;
        float uvz = qx * vy - qy * vx;
        float uuvx = qy * uvz - qz * uvy;
        float uuvy = qz * uvx - qx * uvz;
        float uuvz = qx * uvy - qy * uvx;
        float w2 = qw * 2f;
        return new Vec3(
                vx + w2 * uvx + 2f * uuvx,
                vy + w2 * uvy + 2f * uuvy,
                vz + w2 * uvz + 2f * uuvz
        );
    }
}
