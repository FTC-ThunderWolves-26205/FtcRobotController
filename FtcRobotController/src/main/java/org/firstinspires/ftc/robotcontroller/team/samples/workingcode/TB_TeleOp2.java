//package org.firstinspires.ftc.teamcode;
//
///*/
//.This code performs four basic functions:  basic mecanum drive + power-based shooter control + intakes on/off + servo increments.
///
//CONTROLS:
//
//    GAMEPAD 1:
//        LEFT STICK Y: Moves ROBOT forward
//        LEFT STICK X: Strafes ROBOT
//        RIGHT STICK X: Turns ROBOT
//        RIGHT BUMPER: Sets speed to 1 (double speed)
//        LEFT BUMPER: Sets speed to 0.5 (half speed)
//    GAMEPAD 2:
//        DPAD UP: Adds 0.1 (1/10 power) to current shooter power
//        DPAD DOWN: Subtracts 0.1 (1/10 power) to current shooter power
//        DPAD RIGHT: Sets shooter power to 1 (full power) or 0 (no power)
//        A: Sets servo position to launching position then back to resting position
//        X: Sets shooter power to 0.85
//        Y: Sets shooter power to 0.7
//        LEFT BUMPER: Sets outer intake power to 0 (no power) and 1 (full power)
//        RIGHT BUMPER: Sets inner intake power to 0 (no power) and 1 (full power)
//        LEFT TRIGGER: Sets outer intake power to 0 (no power) and -1 (reverse)
//        RIGHT TRIGGER: Sets inner intake power to 0 (no power) and -1 (reverse)
//
//TO DO:  1.  Clean up our edge detection to use FTCLib .wasJustPressed method.  Remove all timers.
//                - driver.wasJustPressed(GamepadKeys.Button.A) is an example
//
//
//LONGER TO DO (Things to Try Before 2nd Tournament?):
//        1.  Add webcam, vision portal, apriltag processor
//        2.  Automate shooting velocity based on detected distance to AprilTag.
//
//*/
//
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
//import com.arcrobotics.ftclib.controller.PIDFController;
//import com.pedropathing.geometry.Pose;
//import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.LED;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//
//@TeleOp(name = "Team Bot TeleOp", group = "Teleop")
//@Config
//
//public class TB_TeleOp extends LinearOpMode {
//    private DcMotor frontRight;
//    private DcMotor frontLeft;
//    private DcMotor backRight;
//    private DcMotor backLeft;
//    private DcMotorEx shooter;
//    private DcMotor iIntake;
//    private DcMotor oIntake;
//    private Servo servo;
//    private LED left_LED_Green;
//    private LED left_LED_Red;
//
//
//    private ElapsedTime shooterTimer = new ElapsedTime();
//    private ElapsedTime servoTimer = new ElapsedTime();
//    private ElapsedTime iIntakeTimer = new ElapsedTime();
//    private ElapsedTime intakeFast = new ElapsedTime();
//    private ElapsedTime oIntakeTimer = new ElapsedTime();
//    private ElapsedTime posTimer = new ElapsedTime();
//    private static final double RESTING_SERVO = TB_Constants.RESTING_SERVO;
//    private static final double LAUNCHING_SERVO = TB_Constants.LAUNCHING_SERVO;
//    private final double NORMAL_SPEED = 0.75;
//    private final double SLOW_SPEED = 0.25;
//    private final double TURBO_SPEED = 1.0;
//    private final double SERVO_DURATION = 750;
//    private final double TICKS_PER_REV = 28.0; // GoBilda 6k Motor has 28 Ticks per Rev per GoBilda website
//     private PIDFController shooterControl;
//    public static double kP = TB_Constants.kP;
//    public static double kI = TB_Constants.kI;
//    public static double kD = TB_Constants.kD;
//    public static double kF = TB_Constants.kF;
//    private double autoVelocity;
//    private double distance;
//    private double relativeAngle;
//    private boolean autoVelocityMode = false;
//
//
//    private Pose goalPose;
//
//    private GoBildaPinpointDriver pinpoint;
//
//
//    @Override
//    public void runOpMode() {
//        hardwareStart();
//        double speed = NORMAL_SPEED;
//        servo.setPosition(RESTING_SERVO);
//        double targetShooterVelocity = 0;
//        double iIntakePower = 0;
//        double oIntakePower = 0;
//        boolean isServo = false;
//        double output;
//
//        shooterControl = new PIDFController(kP, kI, kD, kF);
//        FtcDashboard dashboard = FtcDashboard.getInstance();
//        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, MCM_PoseStorage.poseX, MCM_PoseStorage.poseY, AngleUnit.DEGREES, MCM_PoseStorage.poseHeading));
//
//        waitForStart();
//        shooterTimer.reset();
//        servoTimer.reset();
//        iIntakeTimer.reset();
//        intakeFast.reset();
//        oIntakeTimer.reset();
//        posTimer.reset();
//
//
//        while (opModeIsActive()) {
//
//            pinpoint.update();
//
//            Pose botPose = new Pose(pinpoint.getPosX(DistanceUnit.INCH), pinpoint.getPosY(DistanceUnit.INCH), pinpoint.getHeading(AngleUnit.DEGREES));
//
//
//            if (MCM_PoseStorage.poseX <= 72) {
//                goalPose = new Pose(9, 141, 90);
//            } else {
//                goalPose = new Pose(135, 141, 90);
//            }
//
//            //y = 591.9123 + 36.61393*x - 0.6971521*x^2 + 0.005668831*x^3 - 0.00001569973*x^4
//            distance = getDistance(botPose,goalPose);
//            autoVelocity = 591.9123 + 36.61393*distance - 0.6971521*Math.pow(distance,2) + 0.005668831*Math.pow(distance,3) - 0.00001569973*Math.pow(distance,4);
//            relativeAngle = getRelAngle(botPose,goalPose);
//
//            if (gamepad2.dpad_right && shooterTimer.milliseconds() > 500) {
//                autoVelocityMode = (autoVelocityMode)? false: true;
//                shooterTimer.reset();
//            }
//            if (autoVelocityMode) {
//                targetShooterVelocity = autoVelocity;
//            } else {
//                targetShooterVelocity =0;
//            }
//
//            double forward = -gamepad1.left_stick_y;
//            double strafe = gamepad1.left_stick_x;
//            double turn = gamepad1.right_stick_x;
//
//            double frontLeftPower = (forward + strafe + turn) * speed;
//            double backLeftPower = (forward - strafe + turn) * speed;
//            double frontRightPower = (forward - strafe - turn) * speed;
//            double backRightPower = (forward + strafe - turn) * speed;
//
//            if (gamepad1.a) {
//                if (relativeAngle < -4 && relativeAngle > -180) {
//                    frontLeft.setPower(-0.4);
//                    frontRight.setPower(0.4);
//                    backLeft.setPower(-0.4);
//                    backRight.setPower(0.4);
//                } else if (relativeAngle > 4 && relativeAngle < 180) {
//                    frontLeft.setPower(0.4);
//                    frontRight.setPower(-0.4);
//                    backLeft.setPower(0.4);
//                    backRight.setPower(-0.4);
//                } else {
//                    frontLeft.setPower(0);
//                    frontRight.setPower(0);
//                    backLeft.setPower(0);
//                    backRight.setPower(0);
//                    gamepad2.rumble(100);
//                }
//            } else {
//                frontLeft.setPower(clampFull(frontLeftPower));
//                frontRight.setPower(clampFull(frontRightPower));
//                backLeft.setPower(clampFull(backLeftPower));
//                backRight.setPower(clampFull(backRightPower));
//            }
//
//            if (gamepad1.right_bumper) {
//                speed = TURBO_SPEED;
//            } else if (gamepad1.left_bumper) {
//                speed = SLOW_SPEED;
//            } else {
//                speed = NORMAL_SPEED;
//            }
//
//
//            if (gamepad1.dpad_up && servoTimer.milliseconds() > 750) {
//                servo.setPosition(servo.getPosition() + 0.05);
//                servoTimer.reset();
//            }
//            if (gamepad1.dpad_down && servoTimer.milliseconds() > 750) {
//                servo.setPosition(servo.getPosition() - 0.05);
//                servoTimer.reset();
//            }
//
//            if (gamepad2.dpad_up && shooterTimer.milliseconds() > 500) {
//                targetShooterVelocity += 20;
//                shooterTimer.reset();
//            } else if (gamepad2.dpad_down && shooterTimer.milliseconds() > 500) {
//                targetShooterVelocity -= 20;
//                shooterTimer.reset();
//            }
//
//
//            if (gamepad2.x) {
//                targetShooterVelocity = 1580;
//            }
//
//            if (gamepad2.b) {
//                targetShooterVelocity = 1300;
//            }
//
//            if (gamepad2.a && servoTimer.milliseconds() > SERVO_DURATION && !isServo) {
//                servo.setPosition(LAUNCHING_SERVO);
//                servoTimer.reset();
//                isServo = true;
//            }
//
//            if (servoTimer.milliseconds() > SERVO_DURATION && isServo) {
//                servo.setPosition(RESTING_SERVO);
//                servoTimer.reset();
//                isServo = false;
//            }
//
//
//
//            if (gamepad2.right_bumper && iIntakeTimer.milliseconds() > 250) {
//                iIntakePower = (iIntakePower == 0) ? 0.55 : 0;
//                iIntakeTimer.reset();
//            }
//            if (gamepad2.left_bumper && oIntakeTimer.milliseconds() > 250) {
//                oIntakePower = (oIntakePower == 0) ? 1 : 0;
//                oIntakeTimer.reset();
//            }
//
//            if (gamepad2.right_trigger > 0 && iIntakeTimer.milliseconds() > 250) {
//                iIntakePower = (iIntakePower == 0) ? -1 : 0;
//                iIntakeTimer.reset();
//            }
//            if (gamepad2.left_trigger > 0 && oIntakeTimer.milliseconds() > 250) {
//                oIntakePower = (oIntakePower == 0) ? -1 : 0;
//                oIntakeTimer.reset();
//            }
//
//
//            double shooterVelocity = shooter.getVelocity();
//            iIntake.setPower(iIntakePower);
//            oIntake.setPower(clampFull(oIntakePower));
//
//            if (targetShooterVelocity == 0) {
//                output = 0;
//            } else {
//                output = shooterControl.calculate(shooterVelocity, targetShooterVelocity);
//            }
//
//            shooter.setPower(output);
//
//
//
//            TelemetryPacket packet = new TelemetryPacket();
//            packet.put("Target Velocity", targetShooterVelocity);
//            packet.put("Actual Velocity", shooter.getVelocity());
//            packet.put("Output Power", output);
//            packet.put("X Position", pinpoint.getPosX(DistanceUnit.INCH));
//            packet.put("Y Position", pinpoint.getPosY(DistanceUnit.INCH));
//            packet.put("Theta Position", pinpoint.getHeading(AngleUnit.DEGREES));
//            packet.put("Distance to Goal", getDistance(botPose, goalPose));
//            packet.put("Relative Angle", getRelAngle(botPose, goalPose));
//            packet.put("Auto Velocity", autoVelocity);
//
//
//            dashboard.sendTelemetryPacket(packet);
//
//            telemetry.addData("X (in)", pinpoint.getPosX(DistanceUnit.INCH));
//            telemetry.addData("Y (in)", pinpoint.getPosY(DistanceUnit.INCH));
//            telemetry.addData("Theta", pinpoint.getHeading(AngleUnit.DEGREES));
//
//
//
//
//            if (Math.abs(getRelAngle(botPose,goalPose)) <= 4) {
//                left_LED_Red.off();
//                left_LED_Green.on();
//            } else {
//                left_LED_Green.off();
//                left_LED_Red.on();
//            }
//
//            telemetry.addData("Distance", getDistance(botPose, goalPose));
//            telemetry.addData("Relative Angle", getRelAngle(botPose, goalPose));
//
//            telemetry.addData("Target Velocity", targetShooterVelocity);
//            telemetry.addData("Shooter Velocity", shooterVelocity);
//            telemetry.update();
//
//        }
//    }
//
//    private void hardwareStart() {
//        frontRight = hardwareMap.get(DcMotor.class, "FR");
//        frontLeft = hardwareMap.get(DcMotor.class, "FL");
//        backRight = hardwareMap.get(DcMotor.class, "BR");
//        backLeft = hardwareMap.get(DcMotor.class, "BL");
//        shooter = hardwareMap.get(DcMotorEx.class, "SD");
//        oIntake = hardwareMap.get(DcMotor.class, "OID");
//        iIntake = hardwareMap.get(DcMotor.class, "IID");
//        servo = hardwareMap.get(Servo.class, "servo");
//
//        left_LED_Green = hardwareMap.get(LED.class, "LED0");
//        left_LED_Red = hardwareMap.get(LED.class, "LED1");
//
//        shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//
//        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        oIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        iIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
//        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
//        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
//        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
//        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
//        oIntake.setDirection(DcMotorSimple.Direction.FORWARD);
//        iIntake.setDirection(DcMotorSimple.Direction.FORWARD);
//
//        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
//
//        //
//        telemetry.addData("Status", "Initialized");
//        telemetry.update();
//    }
//
//    private double clampPos(double val) {
//        return Math.max(0.0, Math.min(1.0, val));
//    }
//
//    private double clampFull(double val) {
//        return Math.max(-1.0, Math.min(1.0, val));
//    }
//
//    private double clampServo(double val) {
//        return Math.max(LAUNCHING_SERVO, Math.min(RESTING_SERVO, val));
//    }
//
//    private double clampShoot(double val) {
//        return Math.max(0.0, Math.min(2200, val));
//    }
//
//    private double ticksPerSecondToRPM(double tps) {
//        return tps * 60.0 / TICKS_PER_REV;
//    }
//
//    private double getDistance(double x, double y, double x1, double y1) { // get distance between point (x,y) and (x1,y1)
//        return Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2));
//    }
//
//    private double getRelAngle(double x, double y, double h, double x1, double y1) {
//        double angle = 180 - (Math.asin((y1 - y) / getDistance(x, y, x1, y1)));
//        return Math.abs(angle - h);
//    }
//
//    private double getDistance(Pose current, Pose goal) { // get distance between point (x,y) and (x1,y1)
//        return Math.sqrt(Math.pow(goal.getX() - current.getX(), 2) + Math.pow(goal.getY() - current.getY(), 2));
//    }
//
//    private double getRelAngle(Pose current, Pose goal) {
//        double a = Math.toDegrees(Math.atan2(current.getX() - goal.getX(), goal.getY() - current.getY()));
//        telemetry.addData("A", a);
//        telemetry.addData("X difference", current.getX() - goal.getX());
//        telemetry.addData("Y difference", goal.getY() - current.getY());
//        return current.getHeading() - (90 + a); // for blue goal, 90 - a for red
//    }
//
//}