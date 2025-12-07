//This code was derived from the youtube video here:  https://www.youtube.com/watch?v=Ap1lBywv00M
//We will follow the steps in that video to tune our distance sensing with our Limelight
//We will them move on to learning Pose3D of the april tags
//We will use these concepts to (1) auto align for shots and (2) automate shooter speed at any distance



package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "JLG_Mecanum", group = "Teleop")
@Disabled
public class JLG_LL3A_Testing extends LinearOpMode{
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    private Limelight3A limelight;
    private double distance;
    private double orientation;

    private GoBildaPinpointDriver pinpoint;

    private final double NORMAL_SPEED = 0.75;
    private final double SLOW_SPEED = 0.5;
    private final double TURBO_SPEED = 1.0;
    // Servo servo;
    //DistanceSensor distanceSensor;

    @Override
    public void runOpMode() {
        hardwareStart();
        double speed = NORMAL_SPEED;
        //double speed = 0.5;
        //double servoPosition = 0;
        //boolean isPosition = false;
        limelight.start();

        waitForStart();


        while (opModeIsActive()) {
            pinpoint.update();
            limelight.updateRobotOrientation(pinpoint.getHeading(AngleUnit.DEGREES));

            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()){
                Pose3D pose = result.getBotpose_MT2();
                distance = getDistance(result.getTa());

                telemetry.addData("Distance", distance);
                telemetry.addData("Target X", result.getTx());
                telemetry.addData("Target Y", result.getTy());
                telemetry.addData("Target Area", result.getTa());
            }


                telemetry.addData("Bot Pose X", pinpoint.getPosX(DistanceUnit.INCH));
                telemetry.addData("Bot Pose Y", pinpoint.getPosY(DistanceUnit.INCH));
                telemetry.addData("Bot Heading", pinpoint.getHeading(AngleUnit.DEGREES));




            double forward = gamepad1.left_stick_y;
            double strafe = -gamepad1.right_stick_x;
            double turn = -gamepad1.left_stick_x;

            double frontLeftPower = (forward + strafe + turn) * speed;
            double backLeftPower = (forward - strafe + turn) * speed;
            double frontRightPower = (forward - strafe - turn) * speed;
            double backRightPower = (forward + strafe - turn) * speed;

            frontLeft.setPower(clampFull(frontLeftPower));
            frontRight.setPower(clampFull(frontRightPower));
            backLeft.setPower(clampFull(backLeftPower));
            backRight.setPower(clampFull(backRightPower));


            if (gamepad1.right_bumper) {
                speed = TURBO_SPEED;
            } else if(gamepad1.left_bumper) {
                speed = SLOW_SPEED;
            } else {
                speed = NORMAL_SPEED;
            }
            if (gamepad1.a) {
                frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }



            telemetry.addData("Front Left Encoder", frontLeft.getCurrentPosition());
            telemetry.addData("Front Right Encoder", frontRight.getCurrentPosition());
            telemetry.addData("Back Left Encoder", backLeft.getCurrentPosition());
            telemetry.addData("Back Right Encoder", backRight.getCurrentPosition());




            telemetry.update();

        }
    }

    private double clampFull(double val) {
        return Math.max(-1.0, Math.min(1.0, val));
    }

    private double getDistance(double ta){
        double scale = 1;
        double distance = (scale / ta);
        return distance;
    }
    private void hardwareStart() {
        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight  = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight  = hardwareMap.get(DcMotor.class, "BR");
        //servo = hardwareMap.get(Servo.class, "servo");

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(8);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);


        telemetry.addData("Status","Initialized");
        telemetry.update();
    }
}
