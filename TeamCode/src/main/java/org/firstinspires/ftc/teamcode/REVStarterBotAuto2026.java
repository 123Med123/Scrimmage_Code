package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "REV Starter Bot Auto 2026", group = "Starter Bot")
public class REVStarterBotAuto2026 extends LinearOpMode {

  // ---- Tunable constants (test on your own robot) ----
  private static final long   START_DELAY_MS     = 0;      // optional wait, REV default is 10000 when enabled
  private static final double DRIVE_POWER        = -0.25;  // negative = away from wall (intake faces wall)
  private static final int    DRIVE_TIME_MS      = 700;
  private static final int    TARGET_VELOCITY    = 1160;
  private static final int    VELOCITY_TOLERANCE = 50;     // wider than TeleOp
  private static final double LAUNCH_TIME_SEC    = 15;
  private static final double POKER_UP           = 1.0;

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

    rightDrive.setDirection(DcMotor.Direction.REVERSE);
    flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    ElapsedTime autoTimer = new ElapsedTime();

    telemetry.addLine("Ready. Intake side to the wall, then press Play.");
    telemetry.update();

    waitForStart();

    if (opModeIsActive()) {
      pollenServo.setPosition(POKER_UP);

      if (START_DELAY_MS > 0) {
        sleep(START_DELAY_MS);
      }

      // Step 1: back off the wall
      autoTimer.reset();
      while (opModeIsActive() && autoTimer.milliseconds() <= DRIVE_TIME_MS) {
        leftDrive.setPower(DRIVE_POWER);
        rightDrive.setPower(DRIVE_POWER);
      }
      leftDrive.setPower(0);
      rightDrive.setPower(0);

      // Step 2: spin up, feed only while velocity is inside the window
      DcMotorEx fw = (DcMotorEx) flywheel;
      autoTimer.reset();
      while (opModeIsActive() && autoTimer.seconds() <= LAUNCH_TIME_SEC) {
        fw.setVelocity(TARGET_VELOCITY);
        double v = fw.getVelocity();

        if (v >= TARGET_VELOCITY - VELOCITY_TOLERANCE && v <= TARGET_VELOCITY + VELOCITY_TOLERANCE) {
          crServo.setPower(1);
          intakeMotor.setPower(1);
        } else {
          crServo.setPower(0);
          intakeMotor.setPower(0);
        }

        telemetry.addData("Flywheel Velocity", v);
        telemetry.update();
      }

      // Step 3: shut everything down
      flywheel.setPower(0);
      crServo.setPower(0);
      intakeMotor.setPower(0);
    }
  }
}
