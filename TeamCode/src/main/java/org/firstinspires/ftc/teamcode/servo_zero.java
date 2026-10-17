package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Pollen poker servo zeroing tool.
 *
 * Holds pollenServo at a set position so you can attach the poker arm.
 * The servo goes to 0 as soon as you press INIT, so you can attach it
 * before pressing Play.
 *
 *   Cross/A     Go to 0 (poker DOWN, the "zero" position)
 *   Circle/B    Go to 1 (poker UP)
 *   Square/X    Go to 0.5 (middle, to check the range)
 *   Dpad up     Nudge position +0.01
 *   Dpad down   Nudge position -0.01
 */
@TeleOp(name = "Pollen Servo Zero", group = "Test")
public class servo_zero extends LinearOpMode {

    private Servo pollenServo;
    private double position = 0.0;

    @Override
    public void runOpMode() {
        pollenServo = hardwareMap.get(Servo.class, "pollenServo");

        // Move to zero during INIT so the arm can be attached before Play
        pollenServo.setPosition(position);

        telemetry.addLine("Servo going to 0. Attach the poker arm now.");
        telemetry.addLine("Press Play to use the buttons.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.crossWasPressed()) {
                position = 0.0;
            } else if (gamepad1.circleWasPressed()) {
                position = 1.0;
            } else if (gamepad1.squareWasPressed()) {
                position = 0.5;
            } else if (gamepad1.dpadUpWasPressed()) {
                position += 0.01;
            } else if (gamepad1.dpadDownWasPressed()) {
                position -= 0.01;
            }

            // Keep within the servo's valid range
            position = Math.max(0.0, Math.min(1.0, position));
            pollenServo.setPosition(position);

            telemetry.addData("Servo position", "%.2f", position);
            telemetry.addLine("A = 0 (down) | B = 1 (up) | X = 0.5 | Dpad = nudge");
            telemetry.update();
        }
    }
}