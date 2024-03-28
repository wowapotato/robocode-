import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class StrongGrute extends AdvancedRobot {
    int moveDirection = 1; // 初始移动方向
    double previousEnergy = 100; // 敌人上一次扫描时的能量，设初值为100
    int gunDirection = 1; // 炮塔旋转方向
    double fieldWidth;
    double fieldHeight;

    public void run() {
        // 机器人颜色设置
        setBodyColor(Color.black);
        setGunColor(Color.white);
        setRadarColor(Color.red);
        //huhuohuo'qhuo'qu获取获战战场zhang'changzhang'chanzhang'chazhang'chzhang'czhangzhanzhazhz
        fieldWidth = getBattleFieldWidth();
        fieldHeight = getBattleFieldHeight();

        // 界面组件独立旋转设置
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        while(true) {
            // 持续转动雷达来搜索敌人
            turnRadarRight(360 * gunDirection);
            // 移动时避免撞墙
            if (getX() < 50 || getX() > fieldWidth - 50 || getY() < 50 || getY() > fieldHeight - 50) {
                moveDirection *= -1;
            }
            setAhead(100 * moveDirection);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // 利用能量差判断敌人是否发射子弹
        double changeInEnergy = previousEnergy - e.getEnergy();
        if (changeInEnergy > 0 && changeInEnergy <= 3) {
            // 检测到敌人发射子弹，转向后退避开
            setTurnRight(90);
            setAhead(100);
        }
        previousEnergy = e.getEnergy(); // 更新敌人能量值

        // 炮塔锁定并射击逻辑
        double radarTurn = getHeadingRadians() + e.getBearingRadians() - getRadarHeadingRadians();
        double gunTurn = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());
        setTurnRadarRightRadians(1.9 * radarTurn); // 锁定雷达
        setTurnGunRight(gunTurn); // 调整炮塔
        
        double firePower = Math.min(500 / e.getDistance(), 3); // 根据距离设置炮弹威力
        smartFire(firePower); // 聪明射击方法

        // 更改炮塔旋转方向
        gunDirection *= -1;
        setTurnRadarRight(360 * gunDirection);
    }

    private void smartFire(double power) {
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) { // 确保炮塔准备好并且瞄准敌人
            fire(power);
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // 如果被子弹击中时采用随机的转向和前进距离避开再次被击
        setTurnRight(90);
        setAhead(100);
    }

    public void onHitWall(HitWallEvent e) {
        // 撞墙时顺时针转向
        setTurnRight(90);
    }


}