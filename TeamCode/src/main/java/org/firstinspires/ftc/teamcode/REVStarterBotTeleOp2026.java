package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Gamepad:
 *   Left stick Y  Drive forward / back
 *   Right stick X Turn
 *   Left bumper   Flywheel to speed, feeder runs once velocity is in range
 *   Right bumper  Intake in
 *   Right trigger Intake out
 *   Dpad up/down  Feeder servo forward / reverse
 *   Triangle/Y    Flywheel only, to target velocity
 *   Square/X      Flywheel reverse
 *   Cross/A       Pollen poker down
 *   Circle/B      Pollen poker up
 */
@TeleOp(name = "REV Starter Bot TeleOp 2026", group = "Starter Bot")
public class REVStarterBotTeleOp2026 extends LinearOpMode {

  // ---- Tunable constants ----
  private static final int    TARGET_VELOCITY      = 1160;  // flywheel ticks/sec
  private static final int    VELOCITY_TOLERANCE   = 20;    // +/- window before feeder runs
  private static final double FLYWHEEL_REVERSE_PWR = -0.55;
  private static final double POKER_UP             = 1.0;
  private static final double POKER_DOWN           = 0.0;

  private DcMotor leftDrive;
  private DcMotor rightDrive;
  private DcMotor intakeMotor;
  private DcMotor flywheel;
  private CRServo crServo;
  private Servo pollenServo;

  @Override
  public void runOpMode() {
    leftDrive   = hardwareMap.get(DcMotor.class, "leftDrive");
    rightDrive  = hardwareMap.get(DcMotor.class, "rightDrive");
    intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
    flywheel    = hardwareMap.get(DcMotor.class, "flywheel");
    crServo     = hardwareMap.get(CRServo.class, "crServo");
    pollenServo = hardwareMap.get(Servo.class, "pollenServo");

    // One side of the drivetrain is mirrored
    rightDrive.setDirection(DcMotor.Direction.REVERSE);
    flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    telemetry.addLine("Ready. Press Play.");
    telemetry.update();

    waitForStart();

    if (opModeIsActive()) {
      // Make sure the poker starts up
      pollenServo.setPosition(POKER_UP);

      while (opModeIsActive()) {
        intakeControls();
        pollenPoker();
        drive();
        flywheelControls();

        telemetry.addData("Flywheel Velocity", ((DcMotorEx) flywheel).getVelocity());
        telemetry.addData("Target Velocity", TARGET_VELOCITY);
        telemetry.update();
      }
    }
  }

  /** Intake: right bumper in, right trigger out. */
  private void intakeControls() {
    if (gamepad1.right_bumper) {
      intakeMotor.setPower(1);
    } else if (gamepad1.right_trigger_pressed) {
      intakeMotor.setPower(-1);
    } else {
      intakeMotor.setPower(0);
    }
  }

  /** Pollen poker: cross = down, circle = up. WasPressed means a tap is enough. */
  private void pollenPoker() {
    if (gamepad1.crossWasPressed()) {
      pollenServo.setPosition(POKER_DOWN);
    } else if (gamepad1.circleWasPressed()) {
      pollenServo.setPosition(POKER_UP);
    }
  }

  /** Split arcade drive: left stick forward/back, right stick turn. */
  private void drive() {
    double y = -gamepad1.left_stick_y;  // stick up is negative, so flip it
    double x = gamepad1.right_stick_x;  // turn

    double left  = y + x;
    double right = y - x;

    // Keep both sides within [-1, 1] without changing the turn ratio
    double max = Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));
    leftDrive.setPower(left / max);
    rightDrive.setPower(right / max);
  }

  /** Flywheel and feeder: manual controls plus the auto sequence. */
  private void flywheelControls() {
    DcMotorEx fw = (DcMotorEx) flywheel;

    if (gamepad1.triangle) {
      // Flywheel only
      fw.setVelocity(TARGET_VELOCITY);
      crServo.setPower(0);
    } else if (gamepad1.square) {
      // Reverse flywheel to clear a jam
      flywheel.setPower(FLYWHEEL_REVERSE_PWR);
      crServo.setPower(0);
    } else if (gamepad1.left_bumper) {
      // Auto sequence: spin up, feed only once velocity is inside the window
      fw.setVelocity(TARGET_VELOCITY);
      double v = fw.getVelocity();
      if (v >= TARGET_VELOCITY - VELOCITY_TOLERANCE && v <= TARGET_VELOCITY + VELOCITY_TOLERANCE) {
        crServo.setPower(1);
      } else {
        crServo.setPower(0);
      }
    } else if (gamepad1.dpad_up) {
      // Manual feeder forward
      flywheel.setPower(0);
      crServo.setPower(1);
    } else if (gamepad1.dpad_down) {
      // Manual feeder reverse
      flywheel.setPower(0);
      crServo.setPower(-1);
    } else {
      flywheel.setPower(0);
      crServo.setPower(0);
    }
  }
}
