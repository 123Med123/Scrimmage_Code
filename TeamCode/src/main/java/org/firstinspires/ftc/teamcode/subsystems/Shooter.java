package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Dual-flywheel shooter with an adjustable hood, for launching pollen (2.8 in) and
 * nectar (3.6 in) in a high arc into the hive's upward-facing cell.
 *
 * Hardware config names:
 *   Motors : shooterLeft, shooterRight (goBILDA 6000 rpm or 5203 1:1, same wheel diameter)
 *   Servos : feeder (continuous), hood (positional)
 *
 * Every number below is a STARTING POINT. Calibrate on the real robot.
 */
public class Shooter {

    // ---- Tunable constants ----
    public static double TARGET_VELOCITY_POLLEN = 1500; // ticks/sec, pollen (lighter, smaller)
    public static double TARGET_VELOCITY_NECTAR = 1300; // ticks/sec, nectar (bigger, heavier)
    public static double VELOCITY_TOLERANCE     = 40;   // ticks/sec window before feeding
    public static double HOOD_POLLEN            = 0.55; // servo position = launch angle
    public static double HOOD_NECTAR            = 0.65;
    public static double FEED_POWER             = 1.0;

    private final DcMotorEx left, right;
    private final CRServo feeder;
    private final Servo hood;
    private double target = 0;

    public Shooter(HardwareMap hw) {
        left   = hw.get(DcMotorEx.class, "shooterLeft");
        right  = hw.get(DcMotorEx.class, "shooterRight");
        feeder = hw.get(CRServo.class, "feeder");
        hood   = hw.get(Servo.class, "hood");

        // Wheels face each other, so one side runs reversed. Flip if the ball is pulled in.
        right.setDirection(DcMotorSimple.Direction.REVERSE);
        for (DcMotorEx m : new DcMotorEx[]{left, right}) {
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
    }

    public void setPollen() { hood.setPosition(HOOD_POLLEN); target = TARGET_VELOCITY_POLLEN; apply(); }
    public void setNectar() { hood.setPosition(HOOD_NECTAR); target = TARGET_VELOCITY_NECTAR; apply(); }

    public void stop() { target = 0; apply(); feeder.setPower(0); }

    private void apply() { left.setVelocity(target); right.setVelocity(target); }

    public double velocity() { return (Math.abs(left.getVelocity()) + Math.abs(right.getVelocity())) / 2.0; }

    public boolean atSpeed() {
        return target > 0 && Math.abs(velocity() - target) <= VELOCITY_TOLERANCE;
    }

    /** Feed only when the wheels are at speed, so every element leaves at the same speed. */
    public void feedWhenReady() { feeder.setPower(atSpeed() ? FEED_POWER : 0); }

    public void feed(double p) { feeder.setPower(p); }
}
