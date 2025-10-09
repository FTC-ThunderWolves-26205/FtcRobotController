package org.firstinspires.ftc.robotcontroller.external.samples.samples; ///* package org.firstinspires.ftc.teamcode;
//
//
//import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.DigitalChannel;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.TouchSensor;
//
//@TeleOp(name = "TeamBot_PID_New (Blocks to Java)")
//public class TeamBot_PID_New extends LinearOpMode {
//
//    private DcMotor Extender;
//    private DcMotor LiftArm;
//    private DigitalChannel LiftArmLimit;
//    private DcMotor FL;
//    private DcMotor FR;
//    private DcMotor BL;
//    private DcMotor BR;
//    private SparkFunOTOS OTOS;
//    private TouchSensor ExtenderLimit1;
//    private Servo LServo;
//    private Servo RServo;
//
//    int RestArmState;
//    int LiftArmPosition;
//    int ExtenderPosition;
//    int SampleArmState;
//    double RServo2;
//    int ExtenderLimitValue;
//    double LServo2;
//    int PickupOffset;
//    int PickupHeight;
//    int LiftArmLimitVal;
//    float X;
//    SparkFunOTOS.Pose2D RobotPose;
//    boolean JawOpen;
//    float LiftArm2;
//    float Extender2;
//    double T;
//    double Speed;
//    int FL2;
//    int FR2;
//    int BR2;
//    int BL2;
//    int MaxExtenderPosition;
//    int MaxLiftArmPosition;
//
//    /**
//     * Describe this function...
//     */
//   /* private void InitVars() {
//        int SampleArmPositionHeight;
//        int SampleExternderPosition;
//        int Button;
//        int ExtenderPower;
//        int Count;
//
//        RestArmState = 0;
//        SampleArmState = 0;
//        SampleArmPositionHeight = 0;
//        SampleExternderPosition = 0;
//        LiftArmLimitVal = 0;
//        Button = 0;
//        RServo2 = 0;
//        LServo2 = 0;
//        LiftArm2 = 0;
//        Extender2 = 0;
//        T = 0;
//        X = 0;
//        FL2 = 0;
//        FR2 = 0;
//        BR2 = 0;
//        BL2 = 0;
//        ExtenderPosition = 0;
//        ExtenderPower = 0;
//        MaxExtenderPosition = 4191;
//        LiftArmPosition = 0;
//        MaxLiftArmPosition = -5500;
//        Count = 0;
//        // was -300
//        PickupHeight = -200;
//        JawOpen = false;
//        PickupOffset = 0;
//    }
//
//    /**
//     * Describe this function...
//     */
//    /* private void Key_Proces() {
//        float RX;
//        float RY;
//        float X2;
//        double CosT;
//        double SinT;
//        double r;
//
//        RX = gamepad1.left_stick_x;
//        if (Math.abs(RX) <= 0.1) {
//            RX = 0;
//        }
//        RY = -gamepad1.left_stick_y;
//        if (Math.abs(RY) <= 0.1) {
//            RY = 0;
//        }
//        X = gamepad1.right_stick_x;
//        if (Math.abs(X) <= 0.1) {
//            X = 0;
//        }
//        X2 = gamepad2.left_stick_x;
//        if (Math.abs(X2) <= 0.1) {
//            X2 = 0;
//        } else {
//            RX = (float) (RX + X2 * 0.2);
//        }
//        Extender2 = gamepad2.right_stick_y * -1;
//        // Process PID For Extender
//        if (Math.abs(Extender2) > 0.1) {
//            ExtenderPosition = Extender.getCurrentPosition();
//            ExtenderPosition = (int) (ExtenderPosition + Extender2 * 400);
//            if (ExtenderPosition <= 0) {
//                ExtenderPosition = 0;
//                Extender2 = 0;
//            }
//            if (ExtenderPosition >= MaxExtenderPosition) {
//                ExtenderPosition = MaxExtenderPosition;
//                Extender2 = 0;
//            }
//            Extender.setTargetPosition(ExtenderPosition);
//            Extender.setPower(Extender2);
//        }
//        LiftArm2 = gamepad2.left_stick_y;
//        // Process PID For Lift Arm
//        if (Math.abs(LiftArm2) > 0.1) {
//            LiftArmPosition = LiftArm.getCurrentPosition();
//            LiftArmPosition = (int) (LiftArmPosition + LiftArm2 * 400);
//            if (LiftArmPosition >= 0) {
//                LiftArmPosition = 0;
//                LiftArm2 = 0;
//            }
//            if (LiftArmPosition <= MaxLiftArmPosition) {
//                LiftArmPosition = MaxLiftArmPosition;
//                LiftArm2 = 0;
//            }
//            LiftArm.setTargetPosition(LiftArmPosition);
//            LiftArm.setPower(LiftArm2);
//        }
//        // Get values
//        ExtenderLimitValue = LiftArmLimit.getState() ? 1 : 0;
//        LiftArmLimitVal = LiftArmLimit.getState() ? 1 : 0;
//        if (gamepad2.left_bumper) {
//            if (JawOpen) {
//                CloseClaw();
//            } else {
//                OpenClaw();
//            }
//            sleep(100);
//        }
//        if (gamepad1.right_bumper) {
//            if (gamepad1.left_bumper) {
//                Speed = 0.1;
//            } else {
//                Speed = 1;
//            }
//        } else {
//            if (gamepad1.left_bumper) {
//                Speed = 0.25;
//            } else {
//                Speed = 0.5;
//            }
//        }
//        // Check min/max Extender
//        // Check min/max LiftArm
//        // Set moror values
//        T = Math.atan2(RY, RX) / Math.PI * 180;
//        CosT = r * Math.cos(T / 180 * Math.PI);
//        SinT = r * Math.sin(T / 180 * Math.PI);
//        r = Math.sqrt(RX * RX + RY * RY);
//        FL2 = (int) (X + SinT + CosT);
//        FR2 = (int) -(X + -(SinT - CosT));
//        BL2 = (int) -(X + (SinT - CosT));
//        BR2 = (int) (X + -(SinT + CosT));
//        // Put loop blocks here.
//        ((DcMotorEx) FL).setVelocity(Speed * FL2 * 2400);
//        ((DcMotorEx) FR).setVelocity(Speed * FR2 * 2400);
//        ((DcMotorEx) BL).setVelocity(Speed * BL2 * 2400);
//        ((DcMotorEx) BR).setVelocity(Speed * BR2 * 2400);
//    }
//
//    /**
//     * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
//     * Comment Blocks show where to place Initialization code (runs once, after touching the
//     * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
//     * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
//     * Stopped).
//     */
//    @Override
//    public void runOpMode() {
//        Extender = hardwareMap.get(DcMotor.class, "Extender");
//        LiftArm = hardwareMap.get(DcMotor.class, "LiftArm");
//        LiftArmLimit = hardwareMap.get(DigitalChannel.class, "LiftArmLimit");
//        FL = hardwareMap.get(DcMotor.class, "FL");
//        FR = hardwareMap.get(DcMotor.class, "FR");
//        BL = hardwareMap.get(DcMotor.class, "BL");
//        BR = hardwareMap.get(DcMotor.class, "BR");
//        OTOS = hardwareMap.get(SparkFunOTOS.class, "OTOS");
//        ExtenderLimit1 = hardwareMap.get(TouchSensor.class, "ExtenderLimit1");
//        LServo = hardwareMap.get(Servo.class, "LServo");
//        RServo = hardwareMap.get(Servo.class, "RServo");
//
//        // Put initialization blocks here.
//        InitVars();
//        OpenClaw();
//        OTOS.calibrateImu();
//        OTOS.resetTracking();
//        RobotPose = new SparkFunOTOS.Pose2D();
//        Extender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        LiftArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        // //Reset the Extender to position 0
//        if (!ExtenderLimit1.isPressed()) {
//            // //Set Extender power to -0.25
//            Extender.setPower(-0.35);
//            // //While ! ExtenderLimit1.IsPress {}
//            while (!ExtenderLimit1.isPressed()) {
//                // //Do nothing
//            }
//            ExtenderPosition = 0;
//        }
//        Extender.setTargetPosition(0);
//        Extender.setPower(0);
//        Extender.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        Extender.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        if (LiftArmLimit.getState()) {
//            LiftArm.setPower(0.25);
//            // //While ! LiftArm.IsPress {}
//            while (LiftArmLimit.getState()) {
//                // //Do nothing
//            }
//            if (!LiftArmLimit.getState()) {
//                LiftArm.setPower(-0.1);
//                // //While ! LiftArm.IsPress {}
//                while (!LiftArmLimit.getState()) {
//                    // //Do nothing
//                }
//            }
//        }
//        // //Reset the LiftArm to position 0
//        // Init Lift Arm PID
//        LiftArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        LiftArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        LiftArm.setPower(0);
//        LiftArmPosition = LiftArm.getCurrentPosition();
//        LiftArm.setTargetPosition(LiftArmPosition);
//        LiftArm.setPower(1);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        // Init Extender PID
//        Extender.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        ExtenderPosition = Extender.getCurrentPosition();
//        Extender.setTargetPosition(LiftArmPosition);
//        Extender.setPower(1);
//        Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        // Init OTOS
//        OTOS.begin();
//        telemetry.addData("Extender position", ExtenderPosition);
//        telemetry.addData("LiftArm Position", LiftArmPosition);
//        telemetry.addData("LiftArmLimit", LiftArmLimitVal);
//        telemetry.update();
//        GoToPickupSpecimen();
//        // Wait for Start
//        waitForStart();
//        if (opModeIsActive()) {
//            // Put run blocks here.
//            while (opModeIsActive()) {
//                RobotPose = OTOS.getPosition();
//                Key_Proces();
//                if (gamepad2.b) {
//                    LiftArmPosition = PickupOffset + PickupHeight + -2870;
//                    LiftArm.setTargetPosition(LiftArmPosition);
//                    LiftArm.setPower(-1);
//                    // Check if 1622 witll work
//                    ExtenderPosition = 1622;
//                    Extender.setTargetPosition(ExtenderPosition);
//                    Extender.setPower(1);
//                }
//                if (gamepad2.y) {
//                    GoToPickupSample();
//                }
//                // Is X  Drop liftArn to-400
//                if (gamepad2.x) {
//                    GoToPickupSpecimen();
//                }
//                if (gamepad2.a) {
//                    LiftArmPosition = LiftArm.getCurrentPosition();
//                    ExtenderPosition = Extender.getCurrentPosition();
//                    LiftArm.setTargetPosition(LiftArmPosition);
//                    Extender.setTargetPosition(ExtenderPosition);
//                    Extender.setPower(0);
//                    LiftArm.setPower(0);
//                }
//                if (gamepad2.dpad_up) {
//                    PickupOffset = PickupOffset + -25;
//                    sleep(100);
//                }
//                if (gamepad2.dpad_down) {
//                    PickupOffset = PickupOffset + 25;
//                    if (PickupOffset > 0) {
//                        PickupOffset = 0;
//                    }
//                    sleep(100);
//                }
//                if (gamepad2.dpad_right) {
//                    PickupOffset = 0;
//                }
//                MainOutputData();
//            }
//            StopExternder();
//            StopLiftArm();
//        }
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void CloseClaw() {
//        // LServo from front
//        RServo2 = 0;
//        LServo2 = 1;
//        LServo.setPosition(LServo2);
//        RServo.setPosition(RServo2);
//        JawOpen = false;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void OpenClaw() {
//        // LServo from front
//        RServo2 = 0.4;
//        LServo2 = 0.6;
//        LServo.setPosition(LServo2);
//        RServo.setPosition(RServo2);
//        JawOpen = true;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void StopLiftArm() {
//        LiftArmPosition = LiftArm.getCurrentPosition();
//        LiftArm.setTargetPosition(LiftArmPosition);
//        LiftArm.setPower(0);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void StopExternder() {
//        ExtenderPosition = Extender.getCurrentPosition();
//        Extender.setTargetPosition(ExtenderPosition);
//        Extender.setPower(0);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GoToPickupSample() {
//        OpenClaw();
//        ExtenderPosition = 0;
//        Extender.setTargetPosition(ExtenderPosition);
//        Extender.setPower(-1);
//        LiftArmPosition = PickupOffset + 0;
//        LiftArm.setTargetPosition(LiftArmPosition);
//        LiftArm.setPower(1);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GoToPickupSpecimen() {
//        OpenClaw();
//        LiftArmPosition = PickupOffset + PickupHeight;
//        LiftArm.setTargetPosition(LiftArmPosition);
//        LiftArm.setPower(1);
//        ExtenderPosition = 0;
//        Extender.setTargetPosition(ExtenderPosition);
//        Extender.setPower(-1);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void MainOutputData() {
//        if (ExtenderLimit1.isPressed()) {
//            ExtenderLimitValue = 1;
//        } else {
//            ExtenderLimitValue = 0;
//        }
//        telemetry.addData("PickupOffset", PickupOffset);
//        telemetry.addData("ExtenderLimitVal", ExtenderLimitValue);
//        telemetry.addData("LiftArmLimit", LiftArmLimitVal);
//        telemetry.addData("LiftArm", LiftArm2);
//        telemetry.addData("Extender Y", Extender2);
//        telemetry.addData("ExtenderPosition", ExtenderPosition);
//        telemetry.addData("ExtenderPower", Extender.getPower());
//        telemetry.addData("LiftArmPosition", LiftArmPosition);
//        telemetry.addData("Speed", Speed);
//        // Get the x value from the given SparkFunOTOS.Pose2D object.
//        telemetry.addData("Robot X", RobotPose.x);
//        // Get the x value from the given SparkFunOTOS.Pose2D object.
//        telemetry.addData("Robot y", RobotPose.y);
//        // Get the x value from the given SparkFunOTOS.Pose2D object.
//        telemetry.addData("Robot h", RobotPose.h);
//        telemetry.update();
//    }
//}
