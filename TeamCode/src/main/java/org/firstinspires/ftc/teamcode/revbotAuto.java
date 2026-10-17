package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;

/**
 * Autonomous companion to the "revbot" TeleOp.
 *
 * Uses the same hardware setup and the same shooting values as revbot:
 * flywheel at 0.7 power, feeder crServo at full power, pollenServo at 0.
 *
 * Sequence:
 *   1. Optional start delay
 *   2. Back off the wall
 *   3. Spin up the flywheel (fixed time, since revbot has no velocity feedback)
 *   4. Feed with crServo and intake for a set time
 *   5. Stop everything
 *
 * All timings and the drive direction below are starting points. Test on the
 * robot and adjust the constants.
 */
@Autonomous(name = "revbot auto", group = "revbot")
public class revbotAuto extends LinearOpMode {

    // ---- Tunable constants ----
    private static final long   START_DELAY_MS   = 0;     // set to e.g. 10000 to wait for alliance partner
    private static final double DRIVE_POWER      = -0.25; // negative = back away from the wall (intake faces wall)
    private static final long   DRIVE_TIME_MS    = 700;
    private static final double FLYWHEEL_POWER   = 0.7;   // same as revbot
    private static final long   SPINUP_TIME_MS   = 2500;  // time for flywheel to reach speed
    private static final double FEED_POWER       = 1.0;   // same as revbot dpad up
    private static final double INTAKE_POWER     = 1.0;
    private static final long   FEED_TIME_MS     = 8000;  // how long to feed pollen
    private static final double POLLEN_START_POS = 0.0;   // same as revbot B button

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

        // Same directions as revbot
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addLine("Ready. Intake side to the wall, then press Play.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            pollenServo.setPosition(POLLEN_START_POS);

            // 1. Optional delay
            if (START_DELAY_MS > 0) {
                telemetry.addLine("Waiting for start delay");
                telemetry.update();
                sleep(START_DELAY_MS);
            }

            // 2. Back off the wall
            telemetry.addLine("Driving off the wall");
            telemetry.update();
            leftDrive.setPower(DRIVE_POWER);
            rightDrive.setPower(DRIVE_POWER);
            sleep(DRIVE_TIME_MS);
            leftDrive.setPower(0);
            rightDrive.setPower(0);

            // 3. Spin up the flywheel
            telemetry.addLine("Spinning up flywheel");
            telemetry.update();
            flywheel.setPower(FLYWHEEL_POWER);
            sleep(SPINUP_TIME_MS);

            // 4. Feed pollen into the flywheel
            telemetry.addLine("Feeding");
            telemetry.update();
            crServo.setPower(FEED_POWER);
            intakeMotor.setPower(INTAKE_POWER);
            sleep(FEED_TIME_MS);

            // 5. Stop everything
            flywheel.setPower(0);
            crServo.setPower(0);
            intakeMotor.setPower(0);

            telemetry.addLine("Done");
            telemetry.update();
        }

        // Make sure nothing keeps running if Stop is pressed mid-sequence
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        flywheel.setPower(0);
        crServo.setPower(0);
        intakeMotor.setPower(0);
    }
}
