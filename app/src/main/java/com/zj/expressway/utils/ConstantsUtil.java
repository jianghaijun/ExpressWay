package com.zj.expressway.utils;

import com.zj.expressway.bean.SameDayBean;
import com.zj.expressway.model.ButtonListModel;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 常量类
 * Created by jack on 2017/10/10.
 */

public class ConstantsUtil {
    // 中交路径
    public static String BASE_URL = "http://114.116.12.219:8012/tongren/";
    // 前缀
    public static String prefix = "";
    // accountId
    public static String ACCOUNT_ID = "tongren_qyh_app_id";
    // 参数格式
    public static SameDayBean sameDayBean;
    public static boolean isDownloadApk = false;
    public static boolean jumpPersonalInfo = false;
    public static ButtonListModel buttonModel;
    // 参数格式
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    // 用户id
    public static String USER_ID = "USER_ID";
    // token
    public static String TOKEN = "TOKEN";
    // userHead
    public static String USER_HEAD = "USER_HEAD";
    // 选中人员id
    public static String SELECT_USER_ID = "SELECT_USER_ID";
    // 工序列表类型（0：工序 1：质量 2：安全）
    public static String PROCESS_LIST_TYPE = "PROCESS_LIST_TYPE";
    // 滑动
    public static String slide = "SLIDE";
    // 长按
    public static String Long_press = "Long_press";
    // OkHttpClient
    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30000L, TimeUnit.MILLISECONDS)
            .readTimeout(30000L, TimeUnit.MILLISECONDS)
            .build();
    // 是否登录成功
    public static final String IS_LOGIN_SUCCESSFUL = "IS_LOGIN_SUCCESSFUL";
    // 通讯录地址
    public static final String Mail_Url = "http://qyh.apih5.com/personWeb/";
    // 轮播图编辑
    public static final String Scroll_Photo = "https://www.baidu.com/";

    // 登录
    public static final String LOGIN = prefix + "user/" + "login";
    // 层级列表
    public static final String getZxHwGxProjectLevelList = prefix + "getZxHwGxProjectLevelList";
    public static final String getZxHwAqProjectLevelList = prefix + "getZxHwAqProjectLevelList";
    public static final String getZxHwZlProjectLevelList = prefix + "getZxHwZlProjectLevelList";
    // 添加层级
    public static final String addGxLevel = prefix + "addZxHwGxProjectLevel";
    public static final String addZlLevel = prefix + "addZxHwZlProjectLevel";
    public static final String addAqLevel = prefix + "addZxHwAqProjectLevel";
    // 删除层级
    public static final String deleteGxLevel = prefix + "batchDeleteUpdateZxHwGxProjectLevel";
    public static final String deleteZlLevel = prefix + "batchDeleteUpdateZxHwZlProjectLevel";
    public static final String deleteAqLevel = prefix + "batchDeleteUpdateZxHwAqProjectLevel";
    // 获取首页数据
    public static final String getIndexviewHomePage = prefix + "getIndexviewHomePage";
    // 图片上传
    public static final String UP_LOAD_PHOTOS = prefix + "appUploadGxAttachment";
    public static final String upload = prefix + "appUploadCommon";
    // 删除图片
    public static final String DELETE_PHOTOS = prefix + "appBatchDeleteZxHwGxAttachment";
    // 版本检查
    public static final String CHECK_VERSION = prefix + "version/checkVersion";
    // 下载APK
    public static final String DOWNLOAD_APK = prefix + "version/downloadFile";
    // 同步工序字典
    public static final String appGetTwoinoneDictList = prefix + "appGetTwoinoneDictList";
    // 同步三级联动菜单
    public static final String getFirSecThiLevelSelect = prefix + "getFirSecThiLevelSelect";
    // 同步到服务器
    public static final String syncDataToServer = prefix + "appSyncProjectLevelAndProcess";
    // 上传用户头像
    public static final String UPLOAD_ICON = prefix + "appUploadIcon";
    // 获取消息列表
    public static final String GET_TIMER_TASK_LIST = prefix + "getSxZlTimerTaskList";
    // 修改密码
    public static final String UPDATE_PASSWORD = prefix + "user/updateUserPwd";
    // 工序报表获取首页数据
    public static final String PROCESS_REPORT_TODAY = prefix + "getProcessReportToday";
    // 按分部获取当日报表详情
    public static final String PROCESS_PROCESS_REPORT_TODAY = prefix + "getProcessReportDetailToday";
    // 按分部获取当日报表详情
    public static final String PROCESS_AND_PHOTO_LIST_TODAY = prefix + "getProcessAndPhotoListToday";
    // 获取人员结构
    public static final String PERSONNEL_LIST = prefix + "getSysDepartmentUserAllTree";
    // 待办列表
    public static final String TO_DO_LIST = prefix + "getTodoList";
    public static final String getTodoListBySenduser = prefix + "getTodoListBySenduser";
    // 已办列表
    public static final String HAS_TO_DO_LIST = prefix + "getHasTodoList";
    public static final String getHasTodoListBySenduser = prefix + "getHasTodoListBySenduser";
    // 待拍照
    public static final String getZxHwGxProcessList = prefix + "getZxHwGxProcessList";
    // 获取流程节点
    public static final String getHistory = prefix + "getHistory";
    // 新流程详情
    public static final String FLOW_DETAILS = prefix + "getSubmitFlow";
    // 新流程详情
    public static final String submitFlow = prefix + "submitFlow";
    // 发起流程
    public static final String startFlow = prefix + "startFlow";
    // 新流程详情
    public static final String openPageFlow = prefix + "openPageFlow";

    // 文件存储路径
    public static final String SAVE_PATH = "/mnt/sdcard/zjExpressway/";

    //用户手动开启GPS
    public static final int GPS_ENABLED = 0;
    //用户手动关闭GPS
    public static final int GPS_DISABLED = 1;
    //服务已停止，并且在短时间内不会改变
    public static final int GPS_OUT_OF_SERVICE = 2;
    //服务暂时停止，并且在短时间内会恢复
    public static final int GPS_TEMPORARILY_UNAVAILABLE = 3;
    //服务正常有效
    public static final int GPS_AVAILABLE = 4;
    // 数字4
    public static final int FOCUS_FRAME_WIDE = 4;
    // 数字5
    public static final int FOCUS_FRAME_FIVE = 5;
    // 数字2
    public static final int FOCUS_FRAME_HEIGHT = 2;
    // 数字3
    public static final int FOCUS_FRAME_THREE = 3;
    // 数字8
    public static final int FOCUS_FRAME_EIGHT = 8;
    // 消息状态值
    public static final int MESSAGE_ONE = 1;
    // 1000
    public static final int NUMBER_ONE_THOUSAN = 1500;
    public static final int EIGHT_HUNDRED = 800;
    public static final int TWO_THOUSAND = 2000;
    public static final int NUMBER_TWO_THOUSAND = 2000;
    // flowId
    public static final String flowId = "sxdehzl";
    public static boolean isLoading = false;

    // 项目名称
    public static String projectName = "中交一公局";
    // 工程名称
    public static String engineeringName = "工程名称：";
    // 工程名称
    public static String constructionSite = "施工部位：";
    // 现场技术员
    public static String technician = "拍照人员：";
    // 质检负责人
    public static String inspection = "    质检负责人：";
    // 拍照时间
    public static String takeTime = "拍照时间：";

    // 下载apk文件名称
    public static final String APK_NAME = "expressway.apk";
}
