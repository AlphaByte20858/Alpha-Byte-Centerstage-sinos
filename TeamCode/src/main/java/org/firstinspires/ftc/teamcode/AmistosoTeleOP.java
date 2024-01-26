/*esta programação será utilizada no amistoso da UnderCtrl;
nesta programação não haverá incluso sistema de climb, pois estará sendo trabalhada melhor após o
evento para sua melhor funcionalidade*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp (name = "AmistosoTeleOP", group = "OpMode")

public class AmistosoTeleOP extends OpMode {

    DcMotorEx Motor0, Motor1, Motor2, Motor3, MCL, InTake, Linear;
    IMU imu;
    Servo garra, SD, GCL;
    public ElapsedTime viao = new ElapsedTime();

    public void init() {

        Motor0 = hardwareMap.get(DcMotorEx.class, "MEF");
        Motor1 = hardwareMap.get(DcMotorEx.class, "MDF");
        Motor2 = hardwareMap.get(DcMotorEx.class, "MET");
        Motor3 = hardwareMap.get(DcMotorEx.class, "MDT");
        Linear = hardwareMap.get(DcMotorEx.class, "Linear");
        MCL = hardwareMap.get(DcMotorEx.class, "MCL");
        InTake = hardwareMap.get(DcMotorEx.class, "Intake");
        garra = hardwareMap.get(Servo.class, "garra");
        SD = hardwareMap.get(Servo.class, "SD");
        GCL = hardwareMap.get(Servo.class, "GCL");

        Motor0.setDirection(DcMotorEx.Direction.REVERSE);
        Motor1.setDirection(DcMotorEx.Direction.FORWARD);
        Motor2.setDirection(DcMotorEx.Direction.REVERSE);
        Motor3.setDirection(DcMotorEx.Direction.FORWARD);
        Linear.setDirection(DcMotorEx.Direction.REVERSE);
        MCL.setDirection(DcMotorEx.Direction.REVERSE);
        MCL.setDirection(DcMotorEx.Direction.REVERSE);
        garra.setDirection(ServoImplEx.Direction.REVERSE);
        SD.setDirection(ServoImplEx.Direction.REVERSE);
        GCL.setDirection(ServoImplEx.Direction.REVERSE);

        modemoto(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        modemoto(DcMotorEx.RunMode.RUN_USING_ENCODER);

        imu = hardwareMap.get(IMU.class, "IMU");

        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        imu.initialize(new IMU.Parameters(orientationOnRobot));

    }

    @Override
    public void loop() {
        move();
        RAW();
        Flinear();
        drone();
        ITK();
    }

    public void move() {

        // Criação das váriaveis de força para a movimentação

        double axial, lateral, yaw;

        axial = gamepad1.right_trigger - gamepad1.left_trigger;
        lateral = gamepad1.left_stick_x;
        yaw = gamepad1.right_stick_x;

        // Criação de váriaveis par8 a movimentação em 8 direções
        double denominador = Math.max(Math.abs(axial) + Math.abs(lateral) + Math.abs(yaw), 1);
        YawPitchRollAngles XYZangles = imu.getRobotYawPitchRollAngles();
        AngularVelocity XYZvelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);


        // Calculos para a movimentação das rodas omnidirectionais mecanum
        double MEFp = (axial + lateral + yaw / denominador);
        double MDFp = (axial - lateral - yaw / denominador);
        double METp = (axial - lateral + yaw / denominador);
        double MDTp = (axial + lateral - yaw / denominador);

        allMotorsPower(MEFp * 0.7, MDFp * 0.7, METp * 0.7, MDTp * 0.7);
    }

    public void allMotorsPower(double paMEF, double paMDF, double paMET, double paMDT) {
        Motor0.setPower(paMEF);
        Motor1.setPower(paMDF);
        Motor2.setPower(paMET);
        Motor3.setPower(paMDT);
    }

    public void modemoto(DcMotor.RunMode mode) {
        Motor0.setMode(mode);
        Motor1.setMode(mode);
        Motor2.setMode(mode);
        Motor3.setMode(mode);
    }
    //sistema linear
    public void Flinear() {
        double linearD = gamepad2.right_trigger;
        double linearE = gamepad2.left_trigger;

        if (gamepad2.right_trigger > 0.1) {
            Linear.setPower(linearD);
        } else if (gamepad2.left_trigger > 0.1) {
            Linear.setPower(linearE);
        }
        else {
            Linear.setPower(0);
        }
    }

    //garra
    public void RAW(){
        double gara = garra.getPosition();
        if (gamepad2.a && gara > 0.5){
            garra.setPosition(0.19);
        }
        else if (gamepad2.a && gara < 0.5){
            garra.setPosition(0.84);
        }
    }

    //sistema de lançamento do avião
    public void drone(){
        if (gamepad2.left_bumper){
            SD.setPosition(0);
            viao.startTime();
        }
        if (viao.seconds() > 5){
            SD.setPosition(0.5);
        }
    }

    public void ITK() {
        if(gamepad1.a){
            InTake.setPower(1);
        }
        else{
            InTake.setPower(0);
        }
    }
}