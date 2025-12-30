package org.firstinspires.ftc.robotcontroller.team.samples;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Disabled
@Autonomous
public class MCM_PedroAutoTest extends LinearOpMode {
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
    private final Pose startPose = new Pose(72.516, 22.845, Math.toRadians(90)); // Start position

    // Trajectory Poses
    private final Pose intakePose = new Pose(38.616, 36.258, Math.toRadians(180)); // Intake
    private final Pose waitPose = new Pose(38.616, 36.258, Math.toRadians(0)); // Wait
    private final Pose goToGoalPose = new Pose(42.596, 101.846, Math.toRadians(0)); // Go to goal
    private final Pose shootPose = new Pose(42.596, 101.846, Math.toRadians(0)); // Shoot

    private PathChain intakePath, waitPath, goToGoalPath, shootPath;

    public void buildPaths() {
        intakePath = follower.pathBuilder()
                .addPath(new BezierCurve(
                        startPose,
                        new Pose(61.462, 41.269), // Control point
                        intakePose
                ))
                .setLinearHeadingInterpolation(startPose.getHeading(), intakePose.getHeading())
                .build();

        waitPath = follower.pathBuilder()
                .addPath(new BezierLine(intakePose, waitPose))
                .setConstantHeadingInterpolation(waitPose.getHeading())
                .build();

        goToGoalPath = follower.pathBuilder()
                .addPath(new BezierCurve(
                        waitPose,
                        new Pose(82.391, 68.831), // Control point
                        goToGoalPose
                ))
                .setLinearHeadingInterpolation(waitPose.getHeading(), goToGoalPose.getHeading())
                .build();

        shootPath = follower.pathBuilder()
                .addPath(new BezierLine(goToGoalPose, shootPose))
                .setConstantHeadingInterpolation(shootPose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(intakePath);
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(waitPath);
                    setPathState(2);
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(goToGoalPath);
                    setPathState(3);
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(shootPath);
                    setPathState(4);
                }
                break;

            case 4:
                // Done
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

