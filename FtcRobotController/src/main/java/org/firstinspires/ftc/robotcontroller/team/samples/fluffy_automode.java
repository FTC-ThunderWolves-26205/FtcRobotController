package org.firstinspires.ftc.robotcontroller.team.samples; ///* package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DigitalChannel;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.TouchSensor;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//@Autonomous(name = "AutoPIDRightSideNew (Blocks to Java)", group = "Right")
//public class AutoPIDRightSideNew extends LinearOpMode {
//
//    private SparkFunOTOS OTOS;
//    private DcMotor FL;
//    private DcMotor FR;
//    private DcMotor BL;
//    private DcMotor LiftArm;
//    private DcMotor Extender;
//    private DcMotor BR;
//    private Servo LServo;
//    private Servo RServo;
//    private TouchSensor ExtenderLimit1;
//    private DigitalChannel LiftArmLimit;
//
//    String ComputeLabel;
//    SparkFunOTOS.Pose2D RobotPose;
//    double travelTotal;
//    double TempValue;
//    double CurrentPoseX;
//    double CurrentPoseY;
//    int PickupTravelHeight;
//    int AfterHangHeight;
//    int N;
//    double C;
//    double RequestedInches;
//    double LastPoseX;
//    int SampleArmPositionHeight;
//    int PickupHeight;
//    double InitialDistance;
//    double LastPoseY;
//    int WorkingH;
//    int CurrentPosition;
//    boolean JawOpen;
//    double CurrentPoseH;
//    int axis;
//    double MaxSpeed;
//    double NewPoseX;
//    double NewPoseY;
//    double ADistance;
//    int RampUpDistance;
//    int SlowDownDistanceInches;
//    int initialStep;
//    double workingDistance;
//    int SampleArmExtenderPosition;
//    int OffsetX;
//    double LastDistance;
//    int OffsetY;
//    double NewPoseH;
//    double DeadBandInches;
//    double InitPoseX;
//    double InitPoseY;
//    double WorkingSpeed;
//    double NegSpeed;
//
//    /**
//     * Describe this function...
//     */
//    private void DoStep1() {
//        GetCurrentPose();
//        // ############################
//        // Pickup Next Specimen
//        CloseClaw();
//        GotoSampleArmPosition();
//        sleep(500);
//        DriveForward(0.35, 20);
//        ComputeLabel = "Finished 22\" move";
//        OpenClaw();
//        DriveForward(0.35, -15);
//        ComputeLabel = "Finished -15\" move";
//    }
//
//    /**
//     * This OpMode illustrates the concept of driving a path based on encoder counts.
//     * This OpMode requires that you have encoders on the wheels, otherwise you would use RobotAutoDriveByTime.
//     * This OpMode also requires that the drive Motors have been configured such that a
//     * positive power command moves them forward, and causes the encoders to count up.
//     *
//     * The desired path in this example is:
//     *   - Drive forward for 48 inches
//     *   - Spin right for 12 Inches
//     *   - Drive backward for 24 inches
//     *
//     * This OpMode has a function called encoderDrive that performs the actual movement.
//     * This function assumes that each movement is relative to the last stopping place.
//     * There are other ways to perform encoder based moves, but this method is probably the simplest.
//     * This OpMode uses the RUN_TO_POSITION mode.
//     */
//    @Override
//    public void runOpMode() {
//        SparkFunOTOS.Pose2D myPose;
//
//        OTOS = hardwareMap.get(SparkFunOTOS.class, "OTOS");
//        FL = hardwareMap.get(DcMotor.class, "FL");
//        FR = hardwareMap.get(DcMotor.class, "FR");
//        BL = hardwareMap.get(DcMotor.class, "BL");
//        LiftArm = hardwareMap.get(DcMotor.class, "LiftArm");
//        Extender = hardwareMap.get(DcMotor.class, "Extender");
//        BR = hardwareMap.get(DcMotor.class, "BR");
//        LServo = hardwareMap.get(Servo.class, "LServo");
//        RServo = hardwareMap.get(Servo.class, "RServo");
//        ExtenderLimit1 = hardwareMap.get(TouchSensor.class, "ExtenderLimit1");
//        LiftArmLimit = hardwareMap.get(DigitalChannel.class, "LiftArmLimit");
//
//        InitVars();
//        ZeroPower();
//        OpenClaw();
//        OTOS.calibrateImu();
//        myPose = new SparkFunOTOS.Pose2D(2, 6.5, 180);
//        OTOS.setOffset(myPose);
//        OTOS.setPosition(myPose);
//        OTOS.setLinearScalar(0.769);
//        OTOS.setAngularScalar(1);
//        OTOS.resetTracking();
//        myPose = new SparkFunOTOS.Pose2D(0, 0, 0);
//        RobotPose = new SparkFunOTOS.Pose2D();
//        OTOS.begin();
//        GetCurrentPose();
//        // Reset Left Arm
//        ResetLiftProcess();
//        DisplayValues();
//        GotoSpecimenPickupPosition();
//        // Wait for Team to press Start
//        waitForStart();
//        // ############################
//        DoStep1();
//        DoStep2();
//        DoStep3();
//        DoStep4();
//        GotoSpecimenPickupPosition();
//        telemetry.addData("Path", "Complete");
//        // ############################
//        // End of auto
//        DisplayValues();
//        while (opModeIsActive()) {
//            GetCurrentPose();
//            DisplayValues();
//        }
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void InitVars() {
//        double TURN_SPEED;
//        int COUNTS_PER_MOTOR_REV;
//        int DRIVE_GEAR_REDUCTION;
//        int WHEEL_DIAMETER_INCHES;
//        int COUNTS_PER_INCH;
//        double DRIVE_SPEED;
//        double Shift_Speed;
//        ElapsedTime runtime;
//        int SampleArmMiddleBasketHieght;
//        int SampleArmExtenderMiddleBasketHeight;
//        int LastTravelDistance;
//        int dx;
//        int dy;
//
//        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        LiftArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        Extender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//        COUNTS_PER_MOTOR_REV = 1440;
//        DRIVE_GEAR_REDUCTION = 1;
//        WHEEL_DIAMETER_INCHES = 4;
//        COUNTS_PER_INCH = 70;
//        DRIVE_SPEED = 0.6;
//        Shift_Speed = 0.5;
//        TURN_SPEED = 0.35;
//        runtime = new ElapsedTime();
//        JawOpen = false;
//        // Enter your comment here!
//        PickupHeight = -200;
//        // Enter your comment here!
//        SampleArmPositionHeight = PickupHeight + -2800;
//        AfterHangHeight = PickupHeight + -3000;
//        // Enter your comment here!
//        SampleArmMiddleBasketHieght = PickupHeight + -3000;
//        // Enter your comment here!
//        SampleArmExtenderPosition = 3000;
//        SampleArmExtenderMiddleBasketHeight = 3000;
//        // Enter your comment here!
//        PickupTravelHeight = -500;
//        CurrentPosition = -900;
//        CurrentPosition = 0;
//        RequestedInches = 0;
//        NewPoseX = 0;
//        NewPoseH = 0;
//        NewPoseY = 0;
//        WorkingH = 0;
//        DeadBandInches = 0.5;
//        SlowDownDistanceInches = 3;
//        RampUpDistance = 1;
//        ComputeLabel = "no value";
//        LastDistance = 0;
//        LastTravelDistance = 0;
//        dx = 0;
//        dy = 0;
//        axis = 0;
//        initialStep = 0;
//        C = 0;
//        N = 0;
//        InitPoseX = 0;
//        InitPoseY = 0;
//        OffsetX = 0;
//        OffsetY = 0;
//        LastPoseX = 0;
//        LastPoseY = 0;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void ZeroPower() {
//        BL.setPower(0);
//        BR.setPower(0);
//        FL.setPower(0);
//        FR.setPower(0);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void DoStep2() {
//        TurnInPlace(0.25, -90);
//        ComputeLabel = "Finished turn -90";
//        GotoSpecimenPickupPosition();
//        DriveForward(0.35, 31);
//        ComputeLabel = "Finished 30\" move";
//        CloseClaw();
//        GotoSampleArmPosition();
//        DriveForward(0.35, -35);
//        ComputeLabel = "Finished -30\" move";
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void OpenClaw() {
//        // LServo from front
//        LServo.setPosition(0.6);
//        RServo.setPosition(0.4);
//        JawOpen = false;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void CloseClaw() {
//        // LServo from front
//        LServo.setPosition(1);
//        RServo.setPosition(0);
//        JawOpen = true;
//        sleep(500);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void DoStep3() {
//        TurnInPlace(0.25, 0);
//        ComputeLabel = "Finished 0\" turn";
//        DriveForward(0.35, 17.5);
//        ComputeLabel = "Finished 18\" move";
//        OpenClaw();
//        LiftAfterSpecimenHang();
//        sleep(500);
//        ComputeLabel = "Finished after hang";
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void LiftSpecimenForTravel() {
//        LiftArm.setTargetPosition(PickupTravelHeight);
//        LiftArm.setPower(-0.5);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void LiftAfterSpecimenHang() {
//        LiftArm.setTargetPosition(AfterHangHeight);
//        LiftArm.setPower(-0.5);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GotoSampleArmPosition() {
//        // Set LiftAmr Position
//        LiftArm.setTargetPosition(SampleArmPositionHeight);
//        LiftArm.setPower(-1);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        // Set Extender Position
//        Extender.setTargetPosition(SampleArmExtenderPosition);
//        Extender.setPower(1);
//        Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GotoSpecimenPickupPosition() {
//        // Set LiftAmr Position
//        LiftArm.setTargetPosition(PickupHeight);
//        LiftArm.setPower(1);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        // Set Extender Position
//        Extender.setTargetPosition(0);
//        Extender.setPower(-1);
//        Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void DoStep4() {
//        DriveForward(0.35, -18);
//        ComputeLabel = "Finished -18\" back";
//        ShiftDrive(0.5, 40);
//        ComputeLabel = "Finished 40\" shift";
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GotoMiddleBasketPosition() {
//        // Set LiftAmr Position
//        LiftArm.setTargetPosition(-3300);
//        LiftArm.setPower(-1);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        // Set Extender Position
//        Extender.setTargetPosition(2700);
//        Extender.setPower(1);
//        Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void ResetLiftProcess() {
//        // This is only run once!
//        OpenClaw();
//        sleep(500);
//        DisplayValues();
//        if (!ExtenderLimit1.isPressed()) {
//            Extender.setPower(-0.75);
//            while (!ExtenderLimit1.isPressed()) {
//                // Optional pause after each move.
//            }
//            Extender.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            Extender.setTargetPosition(0);
//            Extender.setPower(0);
//            Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        }
//        DisplayValues();
//        if (LiftArmLimit.getState()) {
//            LiftArm.setPower(0.5);
//            while (LiftArmLimit.getState()) {
//                // Optional pause after each move.
//            }
//            if (!LiftArmLimit.getState()) {
//                LiftArm.setPower(-0.1);
//                // //While ! LiftArm.IsPress {}
//                while (!LiftArmLimit.getState()) {
//                    // //Do nothing
//                }
//            }
//        }
//        LiftArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        LiftArm.setPower(0);
//        LiftArm.setTargetPosition(0);
//        DisplayValues();
//        LiftArm.setTargetPosition(PickupHeight);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        LiftArm.setPower(-0.5);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void ExternderArmToPosition(int x) {
//        Extender.setTargetPosition(x);
//        Extender.setPower(-1);
//        Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GotoAfterBasketPosition() {
//        // Set LiftAmr Position
//        LiftArm.setTargetPosition(-3700);
//        LiftArm.setPower(1);
//        LiftArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        // Set Extender Position
//        Extender.setTargetPosition(0);
//        Extender.setPower(-1);
//        Extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void GetCurrentPose() {
//        RobotPose = OTOS.getPosition();
//        // Get the x value from the given SparkFunOTOS.Pose2D object.
//        CurrentPoseX = RobotPose.x;
//        // Get the x value from the given SparkFunOTOS.Pose2D object.
//        CurrentPoseY = RobotPose.y;
//        // Get the x value from the given SparkFunOTOS.Pose2D object.
//        CurrentPoseH = RobotPose.h;
//        CurrentPoseX = CurrentPoseX + OffsetX;
//        CurrentPoseY = CurrentPoseY + OffsetY;
//        // May need to convert CurrentPoseH = 360 + CurrentPoseH if < -90
//        if (CurrentPoseH < -120) {
//            DeadBandInches = 360 + CurrentPoseH;
//        }
//    }
//
//    /**
//     * Describe this function...
//     */
//    private double CalcDistance(double X1, double Y1, int X2, double Y2) {
//        double CalcDiffX;
//        double CalcDiffY;
//
//        CalcDiffX = X2 - X1;
//        CalcDiffY = Y2 - Y1;
//        CalcDiffX = CalcDiffX * CalcDiffX;
//        CalcDiffY = CalcDiffY * CalcDiffY;
//        return Math.sqrt(CalcDiffX + CalcDiffY);
//    }
//
//    /**
//     * Describe this function...
//     */
//    private double ComputeSpeed(double myDistance, double myMaxSpeed) {
//        travelTotal = InitialDistance - myDistance;
//        TempValue = myMaxSpeed < 0 ? -0.2 : 0.2;
//        if (travelTotal < 0) {
//            travelTotal = 0;
//        }
//        if (travelTotal <= RampUpDistance) {
//            ComputeLabel = "RampUp";
//            TempValue = TempValue + myMaxSpeed * (travelTotal / RampUpDistance);
//        } else if (myDistance <= SlowDownDistanceInches) {
//            ComputeLabel = "Slow-down";
//            TempValue = myMaxSpeed * (myDistance / SlowDownDistanceInches);
//        } else {
//            ComputeLabel = "Mid-Range";
//            TempValue = myMaxSpeed;
//        }
//        return TempValue;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private double CalcAxisStep() {
//        // 0-> X, 1-> Y
//        if (axis == 0) {
//            C = CurrentPoseX;
//        } else {
//            C = CurrentPoseY;
//        }
//        if (axis == 0) {
//            N = (int) NewPoseX;
//        } else {
//            N = (int) NewPoseY;
//        }
//        return N - C;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private double CalcDistanceAxis() {
//        // 0-> X, 1-> Y
//        if (axis == 0) {
//            C = CurrentPoseX;
//        } else {
//            C = CurrentPoseY;
//        }
//        if (axis == 0) {
//            N = (int) NewPoseX;
//        } else {
//            N = (int) NewPoseY;
//        }
//        if (initialStep > 0) {
//            TempValue = N - C;
//        } else {
//            TempValue = C - N;
//        }
//        return TempValue;
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void DriveForward(double speed, double inches) {
//        GetCurrentPose();
//        RequestedInches = inches;
//        if (inches < 0) {
//            speed = speed * -1;
//        }
//        MaxSpeed = speed;
//        ADistance = Math.abs(inches);
//        // Inches was the distance that needs to be travel, *-1 converts it to
//        inches = inches * -1;
//        GetCurrentPose();
//        InitPoseX = CurrentPoseX;
//        InitPoseY = CurrentPoseY;
//        // Compute new target Pose
//        NewPoseH = CurrentPoseH;
//        WorkingH = RequestedInches >= 0 ? CurrentPoseH : CurrentPoseH + 180;
//        WorkingH = WorkingH + 90;
//        NewPoseX = CurrentPoseX + ADistance * Math.cos(WorkingH / 180 * Math.PI);
//        NewPoseY = CurrentPoseY + ADistance * Math.sin(WorkingH / 180 * Math.PI);
//        // Compute distance
//        InitialDistance = CalcDistance(CurrentPoseX, CurrentPoseY, (int) NewPoseX, NewPoseY);
//        LastDistance = InitialDistance;
//        travelTotal = 0;
//        workingDistance = InitialDistance - travelTotal;
//        WorkingSpeed = ComputeSpeed(workingDistance, MaxSpeed);
//        MoveDisplay();
//        // Ensure that the OpMode is still active.
//        if (workingDistance > DeadBandInches) {
//            while (opModeIsActive() && workingDistance > DeadBandInches) {
//                // Display it for the driver.
//                LastDistance = workingDistance;
//                NegSpeed = WorkingSpeed * -1;
//                FL.setPower(WorkingSpeed);
//                BL.setPower(NegSpeed);
//                FR.setPower(WorkingSpeed);
//                BR.setPower(NegSpeed);
//                sleep(50);
//                GetCurrentPose();
//                // Compute new speed if needed
//                travelTotal = CalcDistance(InitPoseX, InitPoseY, (int) CurrentPoseX, CurrentPoseY);
//                workingDistance = InitialDistance - travelTotal;
//                if (workingDistance <= DeadBandInches) {
//                    break;
//                }
//                WorkingSpeed = ComputeSpeed(workingDistance, MaxSpeed);
//            }
//        }
//        ZeroPower();
//        // Stop all motion.
//        // Turn off RUN_TO_POSITION.
//        // Optional pause after each move.
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void ShiftDrive(double speed, int inches) {
//        GetCurrentPose();
//        RequestedInches = inches;
//        if (inches >= 0) {
//            speed = speed * -1;
//        }
//        MaxSpeed = speed;
//        ADistance = Math.abs(inches);
//        // Inches was the distance that needs to be travel, *-1 converts it to
//        inches = inches * -1;
//        GetCurrentPose();
//        InitPoseX = CurrentPoseX;
//        InitPoseY = CurrentPoseY;
//        // Compute new target Pose
//        NewPoseH = CurrentPoseH;
//        WorkingH = inches >= 0 ? CurrentPoseH + 90 : CurrentPoseH - 90;
//        // Compute distance
//        NewPoseX = CurrentPoseX + ADistance * Math.sin(WorkingH / 180 * Math.PI);
//        NewPoseY = CurrentPoseY + ADistance * Math.cos(WorkingH / 180 * Math.PI);
//        // Compute distance
//        InitialDistance = CalcDistance(CurrentPoseX, CurrentPoseY, (int) NewPoseX, NewPoseY);
//        LastDistance = InitialDistance;
//        travelTotal = 0;
//        workingDistance = InitialDistance - travelTotal;
//        WorkingSpeed = ComputeSpeed(workingDistance, MaxSpeed);
//        MoveDisplay();
//        // Ensure that the OpMode is still active.
//        if (workingDistance > DeadBandInches) {
//            while (opModeIsActive() && workingDistance > DeadBandInches) {
//                // Display it for the driver.
//                LastDistance = workingDistance;
//                NegSpeed = WorkingSpeed * -1;
//                FL.setPower(NegSpeed);
//                BR.setPower(WorkingSpeed);
//                FR.setPower(WorkingSpeed);
//                BL.setPower(NegSpeed);
//                sleep(50);
//                GetCurrentPose();
//                // Compute new speed if needed
//                travelTotal = CalcDistance(InitPoseX, InitPoseY, (int) CurrentPoseX, CurrentPoseY);
//                workingDistance = InitialDistance - travelTotal;
//                if (workingDistance <= DeadBandInches) {
//                    break;
//                }
//                WorkingSpeed = ComputeSpeed(workingDistance, MaxSpeed);
//            }
//        }
//        ZeroPower();
//        // Stop all motion.
//        // Turn off RUN_TO_POSITION.
//        // Optional pause after each move.
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void TurnInPlace(double speed, int inches) {
//        GetCurrentPose();
//        LastPoseX = CurrentPoseX;
//        LastPoseY = CurrentPoseY;
//        // Inches ->  desited new heading
//        RequestedInches = inches * 1;
//        MaxSpeed = speed;
//        // Compute new target Pose
//        NewPoseH = RequestedInches;
//        NewPoseX = CurrentPoseX;
//        NewPoseY = CurrentPoseY;
//        // Compute new heading
//        ADistance = Math.abs(CurrentPoseH - NewPoseH);
//        MoveDisplay();
//        if (ADistance > 0) {
//            if (NewPoseH >= CurrentPoseH) {
//                WorkingSpeed = MaxSpeed;
//                NegSpeed = MaxSpeed * -1;
//                FL.setPower(NegSpeed);
//                BL.setPower(WorkingSpeed);
//                FR.setPower(WorkingSpeed);
//                BR.setPower(NegSpeed);
//                sleep(100);
//                // Small angle to larger angle
//                while (opModeIsActive() && Math.abs(CurrentPoseH - NewPoseH) > 2) {
//                    // Display it for the driver.
//                    GetCurrentPose();
//                    MoveDisplay();
//                }
//            } else {
//                NegSpeed = MaxSpeed * -1;
//                WorkingSpeed = MaxSpeed;
//                FL.setPower(WorkingSpeed);
//                BL.setPower(NegSpeed);
//                FR.setPower(NegSpeed);
//                BR.setPower(WorkingSpeed);
//                sleep(100);
//                // Larger  angle to Smaller angle
//                while (opModeIsActive() && Math.abs(CurrentPoseH - NewPoseH) > 2) {
//                    // Display it for the driver.
//                    GetCurrentPose();
//                    MoveDisplay();
//                }
//            }
//            // Need to check heading
//        }
//        // Stop all motion.
//        ZeroPower();
//        OffsetX = (int) (OffsetX + (LastPoseX - CurrentPoseX));
//        OffsetY = (int) (OffsetY + (LastPoseY - CurrentPoseY));
//        GetCurrentPose();
//        MoveDisplay();
//        // Turn off RUN_TO_POSITION.
//        // Optional pause after each move.
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void MoveDisplay() {
//        telemetry.addData("Requested Inches", RequestedInches);
//        telemetry.addData("Working Heading", WorkingH);
//        telemetry.addData("Compute Label", ComputeLabel);
//        telemetry.addData("Initial Distance", InitialDistance);
//        telemetry.addData("Working Distance", workingDistance);
//        telemetry.addData("Last Distince", LastDistance);
//        telemetry.addData("travel Distince", travelTotal);
//        telemetry.addData("Current X", CurrentPoseX);
//        telemetry.addData("Current Y", CurrentPoseY);
//        telemetry.addData("Current H", CurrentPoseH);
//        telemetry.addData("New Pose X", NewPoseX);
//        telemetry.addData("New Pose Y", NewPoseY);
//        telemetry.addData("New Pose H", NewPoseH);
//        telemetry.addData("Max Speed", MaxSpeed);
//        telemetry.addData("working Speed", WorkingSpeed);
//        telemetry.addData("Neg Speed", NegSpeed);
//        telemetry.update();
//    }
//
//    /**
//     * Describe this function...
//     */
//    private void DisplayValues() {
//        double NewTargetPosition;
//
//        telemetry.addData("Compute Label", ComputeLabel);
//        telemetry.addData("CurrentPosiion", CurrentPosition);
//        telemetry.addData("New Position", NewTargetPosition);
//        telemetry.addData("ExtenderLimit", ExtenderLimit1.isPressed() ? 1 : 0);
//        telemetry.addData("LiftArmLimit", LiftArmLimit.getState() ? 1 : 0);
//        telemetry.addData("Arm Hieght", LiftArm.getCurrentPosition());
//        telemetry.addData("OTOS-X", CurrentPoseX);
//        telemetry.addData("OTOS-Y", CurrentPoseY);
//        telemetry.addData("OTOS-H", CurrentPoseH);
//        telemetry.addData("New Pose X", NewPoseX);
//        telemetry.addData("New Pose Y", NewPoseY);
//        telemetry.addData("New Pose H", NewPoseH);
//        telemetry.update();
//    }
//}
