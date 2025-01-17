package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {

    // The motors on the left side of the drive.
    private final TalonSRX     leftPrimaryMotor   = new TalonSRX(DriveConstants.LEFT_MOTOR_PORT);
    private final TalonSRX     leftFollowerMotor  = new TalonSRX(DriveConstants.LEFT_MOTOR_PORT + 1);

    // The motors on the right side of the drive.
    private final TalonSRX     rightPrimaryMotor  = new TalonSRX(DriveConstants.RIGHT_MOTOR_PORT);
    private final VictorSPX    rightFollowerMotor = new VictorSPX(DriveConstants.RIGHT_MOTOR_PORT + 1);

    private final DigitalInput targetSensor       = new DigitalInput(1);

    private double             leftSpeed          = 0;
    private double             rightSpeed         = 0;

    private AnalogGyro         analogGyro         = new AnalogGyro(DriveConstants.ANALOG_GYRO_PORT) {
                                                      @Override
                                                      public double getAngle() {
                                                          // Invert the gyro
                                                          return -super.getAngle();
                                                      }
                                                  };

    private Solenoid           shifter            = new Solenoid(PneumaticsModuleType.CTREPCM,
        DriveConstants.SHIFTER_PNEUMATIC_PORT);

    /** Creates a new DriveSubsystem. */
    public DriveSubsystem() {

        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        leftPrimaryMotor.setInverted(DriveConstants.LEFT_MOTOR_REVERSED);
        leftFollowerMotor.setInverted(DriveConstants.LEFT_MOTOR_REVERSED);

        leftPrimaryMotor.setNeutralMode(NeutralMode.Brake);
        leftFollowerMotor.setNeutralMode(NeutralMode.Brake);

        leftFollowerMotor.follow(leftPrimaryMotor);

        rightPrimaryMotor.setInverted(DriveConstants.RIGHT_MOTOR_REVERSED);
        rightFollowerMotor.setInverted(DriveConstants.RIGHT_MOTOR_REVERSED);

        rightPrimaryMotor.setNeutralMode(NeutralMode.Brake);
        rightFollowerMotor.setNeutralMode(NeutralMode.Brake);

        rightFollowerMotor.follow(rightPrimaryMotor);

        analogGyro.initGyro();
        analogGyro.setSensitivity(0.00165 * (360.0 / 350.0));
    }

    /**
     * Set the left and right speed of the primary and follower motors
     *
     * @param leftSpeed
     * @param rightSpeed
     */
    public void setMotorSpeeds(double leftSpeed, double rightSpeed) {

        this.leftSpeed  = leftSpeed;
        this.rightSpeed = rightSpeed;

        leftPrimaryMotor.set(ControlMode.PercentOutput, leftSpeed);
        rightPrimaryMotor.set(ControlMode.PercentOutput, rightSpeed);

        // NOTE: The follower motors are set to follow the primary
        // motors
    }

    public double getLeftEncoder() {
        return leftPrimaryMotor.getSelectedSensorPosition();
    }

    public double getRightEncoder() {
        return rightPrimaryMotor.getSelectedSensorPosition();
    }

    /** Safely stop the subsystem from moving */
    public void stop() {
        setMotorSpeeds(0, 0);
    }

    public void setShift(boolean shift) {
        shifter.set(shift);
    }

    public boolean isTargetDetected() {
        return !targetSensor.get();
    }

    @Override
    public void periodic() {

        SmartDashboard.putNumber("Right Motor", rightSpeed);
        SmartDashboard.putNumber("Left  Motor", leftSpeed);

        SmartDashboard.putNumber("Left  Encoder", getLeftEncoder());
        SmartDashboard.putNumber("Right Encoder", getRightEncoder());

        SmartDashboard.putData("Gyro", analogGyro);

        SmartDashboard.putBoolean("Shifter", shifter.get());

        SmartDashboard.putBoolean("Target", isTargetDetected());
    }

}
