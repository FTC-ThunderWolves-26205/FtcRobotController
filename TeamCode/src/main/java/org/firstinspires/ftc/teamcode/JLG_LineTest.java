//Robot should end up with intake mostly centered on first 3 balls to the left
//Start with front/center of robot at point of far triangle



package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
@Disabled
@Autonomous(name = "JLG Line Test", group = "JLG Curve Test")
public class JLG_LineTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();
        play();
        if (isStopRequested()) return;
        while (opModeIsActive()) update();
    }
    private void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    private Follower follower;
    private Timer pathTimer;
    private int pathState;

    // Start Pose
    private final Pose startPose = new Pose(72.606, 15.769, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose path1Pose = new Pose(55.278, 35.004, Math.toRadians(180)); // Path 1
    private final Pose path2Pose = new Pose(34.31, 35.177, Math.toRadians(0)); // Path 2

    private PathChain path1Path, path2Path;

    public void buildPaths() {
        path1Path = follower.pathBuilder()
                .addPath(new BezierLine(startPose, path1Pose))
                .setLinearHeadingInterpolation(startPose.getHeading(), path1Pose.getHeading())
                .build();

        path2Path = follower.pathBuilder()
                .addPath(new BezierLine(path1Pose, path2Pose))
                .setLinearHeadingInterpolation(path1Pose.getHeading(), path2Pose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(path1Path);
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(path2Path);
                    setPathState(2);

                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.breakFollowing();


                }
                break;
        }
    }

    private void initialize() {
        pathTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
    }

    private void play() {
        setPathState(0);
    }

    private void update() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
}

