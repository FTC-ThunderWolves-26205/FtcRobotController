package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;



@TeleOp(name = "MCM Limelight Testing", group = "Teleop")

public class MCM_LimelightTesting extends LinearOpMode {

    //I put in the basic objects/variables for the code below
    //You need to add any specific hardware for your bot here, such as "private DcMotor leftMotor", etc.

    private Limelight3A limelight;
    private double distance;
    double tagWidth = 0.165;  // This is a standard value in meters.  AprilTag width in FTC is 165cm.  But measure yours to confirm this is accurate.  Just the solid black box width.






    @Override
    public void runOpMode() {

        hardwareStart();  // Fill out hardwareStart() at bottom for your robot.

        limelight.pipelineSwitch(0); // Select the limelight pipeline that you configured for AprilTags here

        limelight.start();


        waitForStart();



        while (opModeIsActive()) {


            //Put basic code to drive your robot here so you can drive around to test if the april tag flickers as discussed below.


            //Below is the code you need to detect distance.  It may not work great, I haven't tested it.  It's a very simple equation that doesn't rely on all the values we can get from our Limelight.
            //BUT the main thing we need is for the limelight to continue to see the AprilTag while you move it around
            //The numbers returned here don't matter for now.  The only thing that matters is that the telemetry doesn't flicker on your driver station while you move bot around (as long as its pointed towards apriltag)
            //Flickering will mean the limelight is losing the apriltag for a moment, then gaining it back.  This should never happen if the apriltag is in view
            //This is the problem we could not yet solve at home on our own.  This is solved by tuning in the limelight config.

            LLResult result = limelight.getLatestResult();  //Pull bot pose results from limelight
            if (result != null && result.isValid()){  //Only return results if they are valid and not null.  This will cause telemetry to disappear from driver station if apriltag is lost, which we want to see if it is happening.

            double tx = result.getTx();
            double ty = result.getTy();
            double ta = result.getTa();

            double txRad = Math.toRadians(tx);

            double distance = (tagWidth / 2) / Math.tan(txRad);  //Basic distance equation.  It's probably not accurate, but you can check and let me know if it is...  We will deal with accuracy later.  What we need right now is continuous detection.


                telemetry.addData("Target X", tx);
                telemetry.addData("Target Y", ty);
                telemetry.addData("Target Area", ta);
                telemetry.addData(" Distance", distance);
            }

            telemetry.update();
        }


    }

private void hardwareStart(){

        //Maddie, initialize all hardware for your bot here.
        //You need to initialize everything that is plugged into your control hub or you will get errors
        //Even if you aren't using something (e.g., servo) in your code, it needs to be initialized

    limelight = hardwareMap.get(Limelight3A.class, "limelight");

    telemetry.addData("Status","Initialized");
    telemetry.update();
}

}
