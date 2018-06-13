package com.zj.expressway.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorUtil {
    //定义3种传感器的数组,依次为加速度>旋转矢量>陀螺仪
    private static int[] mSensorTypes = {Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_ROTATION_VECTOR, Sensor.TYPE_GYROSCOPE};
    //是否使用传感器或传感器是否可以使用
    private static boolean mIsStart = false;
    // 定义长度为3的数组保存传感器上一次记录的数据，0,1,2分别对应x,y,z轴数据
    // 初始化记录数据
    private static float[] mLastValues = {0f, 0f, 0f};
    //定义阈值变化的范围
    //定义加速度限定的值
    public final static float LIMIT_ACCELEROMETER = 1.0f;
    //定义旋转矢量限定值
    public final static float LIMIT_ROTATION = 0.8f;
    //定义陀螺仪限定值
    public final static float LIMIT_GYROSCOPE = 0.8f;

    /**
     * @param sensorManager 传感器管理器
     * @param listener      A {@link SensorEventListener SensorEventListener} object
     * @return void 返回类型
     * @throws
     * @Title: restartSensor
     * @author：
     */
    public static void startSensor(SensorManager sensorManager, SensorEventListener listener) {
        // 获取当前机器支持的最优的传感器
        Sensor sensor = getBestSensor(sensorManager);
        // 表示未获取到所需的传感器
        if (null == sensor) {
            Log.d("Sensor", "系统不存在所需的传感器,开启定时器聚焦模式");
        } else {
            mIsStart = true;
            /**
             * * 注册传感器监听事件
             * * this，表示SensorEventListener
             * * sensor，表示对应的传感器
             * * SensorManager.SENSOR_DELAY_NORMAL，表示传感器的刷新频率
             * */
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
            Log.i("Sensor", "注册传感器");
        }
    }

    /**
     * 得到所需的最优的传感器，如果没有下面3个即返回null
     * 权重比较：旋转矢量>陀螺仪>加速度
     *
     * @param sensorMag,系统的传感器管理器
     * @return Sensor
     * @Title: getBestSensor
     * @Description:
     * @author
     * @date
     */
    public static Sensor getBestSensor(SensorManager sensorMag) {
        mIsStart = false;
        //遍历需要用到的3种传感器
        for (int i = 0; i < mSensorTypes.length; i++) {
            Sensor sensor = sensorMag.getDefaultSensor(mSensorTypes[i]);
            //获取到存在的传感器返回
            if (sensor != null) {
                return sensor;
            }
        }
        //为找到符合条件的传感器
        return null;
    }

    /**
     * 返回是否注册了传感器
     *
     * @return boolean
     * @Title: isSensor
     * @Description:
     * @author
     * @date
     */
    public static boolean isStart() {
        return mIsStart;
    }

    public static void setIsStart(boolean isStart) {
        mIsStart = isStart;
    }

    /**
     * 比较传感器数据变化是否在可接受的范围内
     *
     * @param event,当前传回数据的传感器事件
     * @return boolean
     * @throws
     * @Title: isOverRange
     * @Description:
     * @author
     * @date
     */
    public static boolean isOverRange(SensorEvent event) {
        boolean ok = false;
        //根据不同的传感器进行不同的判断
        switch (event.sensor.getType()) {
            //旋转矢量传感器
            case Sensor.TYPE_ROTATION_VECTOR:
                ok = compareRotaion(event.values);
                break;
            //陀螺仪传感器
            case Sensor.TYPE_GYROSCOPE:
                ok = compareGyroscope(event.values);
                break;
            //加速度传感器
            case Sensor.TYPE_ACCELEROMETER:
                ok = compareAccelerometer(event.values);
                break;
            default:
                break;
        }
        // 保存当前的值用于比对
        if (ok) {
            mLastValues[0] = event.values[0];
            mLastValues[1] = event.values[1];
            mLastValues[2] = event.values[2];
        }
        return ok;
    }

    /**
     * 旋转矢量比较
     *
     * @param values，当前的数据
     * @return boolean
     * @throws
     * @Title: compareRotaion
     * @Description:
     * @author
     * @date
     */
    private static boolean compareRotaion(float[] values) {
        //比较两次变化的差异值
        float deltaX = Math.abs(values[0] - mLastValues[0]);
        float deltaY = Math.abs(values[1] - mLastValues[1]);
        float deltaZ = Math.abs(values[2] - mLastValues[2]);
        //根据差异值判断是否超过范围
        if (deltaX > SensorUtil.LIMIT_ROTATION
                || deltaY > SensorUtil.LIMIT_ROTATION
                || deltaZ > SensorUtil.LIMIT_ROTATION) {
            return true;
        }
        return false;
    }

    /**
     * 陀螺仪比较
     *
     * @param values，当前数据
     * @return boolean
     * @throws
     * @Title: compareGyroscope
     * @Description:
     * @author
     * @date
     */
    private static boolean compareGyroscope(float[] values) {
        //比较两次变化的差异值
        float delateX = Math.abs(values[0] - mLastValues[0]);
        float delateY = Math.abs(values[1] - mLastValues[1]);
        float delateZ = Math.abs(values[2] - mLastValues[2]);
        //根据差异值判断是否在阈值范围类
        if (delateX > SensorUtil.LIMIT_GYROSCOPE
                || delateY > SensorUtil.LIMIT_GYROSCOPE
                || delateZ > SensorUtil.LIMIT_GYROSCOPE) {
            return true;
        }
        return false;
    }

    /**
     * 加速度比较
     *
     * @param values，当前数据
     * @return boolean
     * @throws
     * @Title: compareGyroscope
     * @Description:
     * @author
     * @date
     */
    private static boolean compareAccelerometer(float[] values) {
        //比较两次变化的差异值
        float delateX = Math.abs(values[0] - mLastValues[0]);
        float delateY = Math.abs(values[1] - mLastValues[1]);
        float delateZ = Math.abs(values[2] - mLastValues[2]);
        //通过差异值判断是否在阈值内
        if (delateX > SensorUtil.LIMIT_ACCELEROMETER
                || delateY > SensorUtil.LIMIT_ACCELEROMETER
                || delateZ > SensorUtil.LIMIT_ACCELEROMETER) {
            return true;
        }
        return false;
    }

}
