package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Leave and park autonomous for the "revbot".
 *
 * Scoring this targets (BIOBUZZ manual, section 10.5.4):
 *   LEAVE (3 pts): robot is no longer contacting the perimeter wall
 *   PARK  (5 pts): robot is at least partially in the LOADING ZONE at the end of AUTO
 *
 * Sequence:
 *   1. Optional start delay
 *   2. Drive out of the starting spot (leg 1)
 *   3. Optional turn
 *   4. Optional second drive (leg 2)
 *   5. Stop and hold
 *
 * The times below are starting points. Distance depends on your robot and battery,
 * so test and adjust the constants until the robot ends up inside the loading zone.
 */
@Autonomous(name = "revbot auto", group = "revbot")
public class revbotAuto extends LinearOpMode {

    // ---- Tunable constants ----
    private static final long   START_DELAY_MS = 0;      // wait before moving, e.g. 10000 to let your partner go first

    // Leg 1: drive out of the start. Negative = back away from the wall (intake faces wall).
    // If the robot goes the wrong way, flip the sign.
    private static final double DRIVE_POWER    = -0.5;
    private static final long   LEG1_TIME_MS   = 2000;

    // Optional turn after leg 1. 0 = no turn.
    // Positive TURN_POWER turns right (left wheel forward, right wheel backward).
    private static final double TURN_POWER     = 0.4;
    private static final long   TURN_TIME_MS   = 0;

    // Optional second drive after the turn, same power as leg 1. 0 = skip.
    private static final long   LEG2_TIME_MS   = 0;

    DcMotor leftDrive, rightDrive;

    @Override
    public void runOpMode() {

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        // Same directions as revbot
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addLine("Ready. Place the robot in its starting spot, then press Play.");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            // 1. Optional delay
            if (START_DELAY_MS > 0) {
                telemetry.addLine("Waiting for start delay");
                telemetry.update();
                sleep(START_DELAY_MS);
            }

            // 2. Drive out of the starting spot
            telemetry.addLine("Leg 1: leaving the start");
            telemetry.update();
            driveFor(DRIVE_POWER, DRIVE_POWER, LEG1_TIME_MS);

            // 3. Optional turn
            if (TURN_TIME_MS > 0) {
                telemetry.addLine("Turning");
                telemetry.update();
                driveFor(TURN_POWER, -TURN_POWER, TURN_TIME_MS);
            }

            // 4. Optional second drive
            if (LEG2_TIME_MS > 0) {
                telemetry.addLine("Leg 2: heading to the loading zone");
                telemetry.update();
                driveFor(DRIVE_POWER, DRIVE_POWER, LEG2_TIME_MS);
            }

            // 5. Stop and hold
            leftDrive.setPower(0);
            rightDrive.setPower(0);
            telemetry.addLine("Parked");
            telemetry.update();
        }

        // Make sure nothing keeps running if Stop is pressed mid-sequence
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }

    /** Drives both sides at the given powers for a set time, stopping early if Stop is pressed. */
    private void driveFor(double leftPower, double rightPower, long timeMs) {
        ElapsedTime timer = new ElapsedTime();
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
        while (opModeIsActive() && timer.milliseconds() < timeMs) {
            idle();
        }
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
