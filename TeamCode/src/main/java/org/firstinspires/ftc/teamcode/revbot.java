package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

@TeleOp(name = "revbot")
public class revbot extends LinearOpMode {

    DcMotor leftDrive, rightDrive;
    DcMotor intakeMotor, flywheel;
    CRServo crServo;
    Servo pollenServo;

    @Override
    public void runOpMode() {

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        crServo = hardwareMap.get(CRServo.class, "crServo");
        pollenServo = hardwareMap.get(Servo.class, "pollenServo");

        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {

            // ---------------- GAMEPAD 1: DRIVE + INTAKE ----------------

            double forward = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;

            double leftPower = forward + turn;
            double rightPower = forward - turn;
            leftPower = Math.max(-1, Math.min(1, leftPower));
            rightPower = Math.max(-1, Math.min(1, rightPower));
            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            intakeMotor.setPower(
                    gamepad1.right_trigger - gamepad1.left_trigger);

            // ---------------- GAMEPAD 2: FLYWHEEL, FEEDER, POLLEN PUSHER ----------------

            if (gamepad2.a) {
                flywheel.setPower(0.7);
            } else if (gamepad2.y) {
                flywheel.setPower(0);
            }

            if (gamepad2.x) {
                pollenServo.setPosition(0.5);
            } else if (gamepad2.b) {
                pollenServo.setPosition(0);
            }

            if (gamepad2.dpad_up) {
                crServo.setPower(1.0);
            } else if (gamepad2.dpad_down) {
                crServo.setPower(-1.0);
            } else {
                crServo.setPower(0);
            }
        }
    }
}
