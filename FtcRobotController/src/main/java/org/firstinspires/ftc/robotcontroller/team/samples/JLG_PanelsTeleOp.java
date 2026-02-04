package org.firstinspires.ftc.robotcontroller.team.samples;

/*
This code performs four basic functions:  basic mecanum drive + power-based shooter control + intakes on/off + servo increments.

http://192.168.43.1:8080/panels



/
CONTROLS:

    GAMEPAD 1:
        LEFT STICK Y: Moves ROBOT forward
        LEFT STICK X: Strafes ROBOT
        RIGHT STICK X: Turns ROBOT
        RIGHT BUMPER: Sets speed to 1 (double speed)
        LEFT BUMPER: Sets speed to 0.5 (half speed)
    GAMEPAD 2:
        DPAD UP: Adds 0.1 (1/10 power) to current shooter power
        DPAD DOWN: Subtracts 0.1 (1/10 power) to current shooter power
        DPAD RIGHT: Sets shooter power to 1 (full power) or 0 (no power)
        A: Sets servo position to launching position then back to resting position
        X: Sets shooter power to 0.85
        Y: Sets shooter power to 0.7
        LEFT BUMPER: Sets outer intake power to 0 (no power) and 1 (full power)
        RIGHT BUMPER: Sets inner intake power to 0 (no power) and 1 (full power)
        LEFT TRIGGER: Sets outer intake power to 0 (no power) and -1 (reverse)
        RIGHT TRIGGER: Sets inner intake power to 0 (no power) and -1 (reverse)

TO DO:  1.  Clean up our edge detection to use FTCLib .wasJustPressed method.  Remove all timers.
                - driver.wasJustPressed(GamepadKeys.Button.A) is an example


LONGER TO DO (Things to Try Before 2nd Tournament?):
        1.  Add webcam, vision portal, apriltag processor
        2.  Automate shooting velocity based on detected distance to AprilTag.

*/


import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector; // Assuming this is correct from your last fix
import com.pedropathing.util.PoseHistory;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@TeleOp(name = "PIDF TeleOp - Panels", group = "Teleop")
@Config
@Disabled
public class JLG_PanelsTeleOp extends LinearOpMode {

    private DcMotor frontRight, frontLeft, backRight, backLeft, iIntake, oIntake;
    private DcMotorEx shooter;
    private Servo servo;

    // Follower for odometry updates
    private Follower follower;

    // Panels and Pose History variables
    private PoseHistory poseHistory;
    private TelemetryManager telemetryM;
    private FieldManager fieldManager;


    private final Style robotStyle = new Style("", "#3F51B5", 0.75); // Blue robot
    private final Style pathStyle = new Style("", "#4CAF50", 0.75); // Green path
    private final double ROBOT_RADIUS = 8.0; // Define robot radius separately


    private ElapsedTime shooterTimer = new ElapsedTime(), servoTimer = new ElapsedTime(), iIntakeTimer = new ElapsedTime(), oIntakeTimer = new ElapsedTime(), posTimer = new ElapsedTime();
    private static final double RESTING_SERVO = 0.35, LAUNCHING_SERVO = 0;
    private final double NORMAL_SPEED = 0.75, SLOW_SPEED = 0.25, TURBO_SPEED = 1.0, SERVO_DURATION = 750;
    private PIDFController shooterControl;
//    public static double kP = TB_Constants.kP;
//    public static double kI = TB_Constants.kI;
//    public static double kD = TB_Constants.kD;
//    public static double kF = TB_Constants.kF;
    private GoBildaPinpointDriver pinpoint;


    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = NORMAL_SPEED;
        servo.setPosition(RESTING_SERVO);
        double targetShooterVelocity = 0, iIntakePower = 0, oIntakePower = 0, output;
        boolean isServo = false;

//        shooterControl = new PIDFController(kP, kI, kD, kF);
//
//        // ### PANELS SETUP - START ###
//        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose());
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        fieldManager = PanelsField.INSTANCE.getField();
        fieldManager.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
        poseHistory = follower.getPoseHistory();
        // ### PANELS SETUP - END ###

        waitForStart();
        shooterTimer.reset(); servoTimer.reset(); iIntakeTimer.reset(); oIntakeTimer.reset(); posTimer.reset();

        while(opModeIsActive()) {
            follower.update();


            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(clampFull(frontLeftPower));
            frontRight.setPower(clampFull(frontRightPower));
            backLeft.setPower(clampFull(backLeftPower));
            backRight.setPower(clampFull(backRightPower));
            if (gamepad1.right_bumper) speed = TURBO_SPEED; else if(gamepad1.left_bumper) speed = SLOW_SPEED; else speed = NORMAL_SPEED;
            if(gamepad1.a && posTimer.milliseconds() > 2000) { follower.setPose(new Pose(39, 33, Math.toRadians(90))); posTimer.reset(); }
            if (gamepad2.dpad_up && shooterTimer.milliseconds() > 500) { targetShooterVelocity += 20; shooterTimer.reset(); } else if (gamepad2.dpad_down && shooterTimer.milliseconds() > 500) { targetShooterVelocity -= 20; shooterTimer.reset(); } else if(gamepad2.dpad_right && shooterTimer.milliseconds() > 500) { targetShooterVelocity = (targetShooterVelocity == 0) ? 2200 : 0; shooterTimer.reset(); }
            if(gamepad2.x) targetShooterVelocity = 1800;
            if(gamepad2.b) targetShooterVelocity = 1520;
            if(gamepad2.a && servoTimer.milliseconds() > SERVO_DURATION && !isServo) { servo.setPosition(LAUNCHING_SERVO); servoTimer.reset(); isServo = true; }
            if(servoTimer.milliseconds() > SERVO_DURATION && isServo) { servo.setPosition(RESTING_SERVO); servoTimer.reset(); isServo = false; }
            if(gamepad2.right_bumper && iIntakeTimer.milliseconds() > 250) { iIntakePower = (iIntakePower == 0) ? 1 : 0; iIntakeTimer.reset(); }
            if(gamepad2.left_bumper && oIntakeTimer.milliseconds() > 250) { oIntakePower = (oIntakePower == 0) ? 1 : 0; oIntakeTimer.reset(); }
            if(gamepad2.right_trigger > 0 && iIntakeTimer.milliseconds() > 250) { iIntakePower = (iIntakePower == 0) ? -1 : 0; iIntakeTimer.reset(); }
            if (gamepad2.left_trigger > 0 && oIntakeTimer.milliseconds() > 250) { oIntakePower = (oIntakePower == 0) ? -1 : 0; oIntakeTimer.reset(); }
            double shooterVelocity = shooter.getVelocity();
            iIntake.setPower(iIntakePower);
            oIntake.setPower(clampFull(oIntakePower));
            if(targetShooterVelocity == 0) output = 0; else output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);
            shooter.setPower(Math.abs(output));

            // ### PANELS DRAWING - START ###
            // Draw the robot's path history
            fieldManager.setStyle(pathStyle);
            int size = poseHistory.getXPositionsArray().length;
            for (int i = 0; i < size - 1; i++) {
                fieldManager.moveCursor(poseHistory.getXPositionsArray()[i], poseHistory.getYPositionsArray()[i]);
                fieldManager.line(poseHistory.getXPositionsArray()[i + 1], poseHistory.getYPositionsArray()[i + 1]);
            }

            // Draw the robot's current position, copying the logic from Tuning.java
            Pose currentPose = follower.getPose();
            fieldManager.setStyle(robotStyle);
            fieldManager.moveCursor(currentPose.getX(), currentPose.getY());
            fieldManager.circle(ROBOT_RADIUS);

            // Draw the robot's heading indicator line
            Vector v = currentPose.getHeadingAsUnitVector();
            v.setMagnitude(v.getMagnitude() * ROBOT_RADIUS);
            double x1 = currentPose.getX() + v.getXComponent() / 2, y1 = currentPose.getY() + v.getYComponent() / 2;
            double x2 = currentPose.getX() + v.getXComponent(), y2 = currentPose.getY() + v.getYComponent();
            fieldManager.moveCursor(x1, y1);
            fieldManager.line(x2, y2);

            // Send all drawing commands and telemetry to Panels
            fieldManager.update();
            telemetryM.update();
            // ### PANELS DRAWING - END ###

            // Optional: Keep driver station telemetry
            telemetry.addData("X (in)", follower.getPose().getX());
            telemetry.addData("Y (in)", follower.getPose().getY());
            telemetry.addData("Theta (deg)", Math.toDegrees(follower.getPose().getHeading()));
            telemetry.update();
        }
    }


    private void hardwareStart() {
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        shooter = hardwareMap.get(DcMotorEx.class, "SD");
        oIntake = hardwareMap.get(DcMotor.class,"OID");
        iIntake = hardwareMap.get(DcMotor.class,"IID");
        servo = hardwareMap.get(Servo.class, "servo");

        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        oIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        iIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        oIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        iIntake.setDirection(DcMotorSimple.Direction.FORWARD);

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0));

        telemetry.addData("Status","Initialized");
        telemetry.update();
    }

    private double clampFull(double val) { return Math.max(-1.0, Math.min(1.0, val)); }
}