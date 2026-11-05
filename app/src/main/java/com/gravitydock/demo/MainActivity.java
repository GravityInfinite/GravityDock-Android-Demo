package com.gravitydock.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gravitydock.sdk.GravityDockPayParams;
import com.gravitydock.sdk.GravityDockSDK;
import com.gravitydock.sdk.GravityDockSDKAccount;
import com.gravitydock.sdk.GravityDockSDKAccountListener;
import com.gravitydock.sdk.GravityDockSDKPay;
import com.gravitydock.sdk.GravityDockSDKPayListener;
import com.gravitydock.sdk.GravityDockSDKTrack;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * GravityDockSDK 全功能接入示例。
 *
 * <p>初始化在 {@link App#onCreate()} 中完成，本页面演示：
 * 隐私协议、账号（登录/登出/用户中心/实名）、支付、track 事件（自定义/账号/支付/广告）、
 * 用户属性（user_set/user_setOnce/increment/max/min/append/uniqAppend/unset/delete）、
 * 公共事件属性、立即上报以及游戏生命周期（gameBegin/gameEnd）、浮标、退出等接口。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // ───────────────────────────── 隐私协议 ─────────────────────────────

    /**
     * 展示隐私协议弹窗并标记用户同意（真实项目中同意后再初始化各统计/渠道 SDK）。
     */
    public void agreePrivacyClick(View view) {
        GravityDockSDK.showUserAgree(this);
        GravityDockSDK.onUserAgree(this);
        showMsg("已同意隐私协议");
    }

    // ───────────────────────────── 账号 ─────────────────────────────

    public void loginClick(View view) {
        GravityDockSDKAccount.login(this, new GravityDockSDKAccountListener() {
            @Override
            public void onLoginSuccess(JSONObject properties) {
                showMsg("登录成功");
                // 登录成功后上报登录事件（内部自动 flush）
                GravityDockSDKTrack.trackLoginEvent();
            }

            @Override
            public void onLoginFail(String msg) {
                showMsg("登录失败: " + msg);
            }

            @Override
            public void onLoginCancel() {
                showMsg("登录取消");
            }

            @Override
            public void onLogout() {
                showMsg("登出");
            }

            @Override
            public void onRealName() {
                showMsg("实名认证完成");
            }

            @Override
            public void onAntiAddiction() {
                showMsg("防沉迷到期，需登出");
            }
        });
    }

    public void logoutClick(View view) {
        GravityDockSDKAccount.logout();
        // 登出后上报登出事件（内部自动 flush）
        GravityDockSDKTrack.trackLogoutEvent();
        showMsg("已调用登出");
    }

    public void userCenterClick(View view) {
        GravityDockSDKAccount.userCenter();
        showMsg("已打开用户中心");
    }

    public void realNameClick(View view) {
        showMsg("是否实名: " + GravityDockSDKAccount.hasRealName());
        GravityDockSDKAccount.realName();
    }

    // ───────────────────────────── 支付 ─────────────────────────────

    public void payClick(View view) {
        GravityDockPayParams params = new GravityDockPayParams.Builder(1.00)
                .paypointCode("gravity_dock_product_1")
                .productName("60钻石")
                .merchantOrderNo("cp_order_" + System.currentTimeMillis())
                .build();
        GravityDockSDKPay.pay(this, params, new GravityDockSDKPayListener() {
            @Override
            public void onPaySuccess(JSONObject properties) {
                showMsg("支付成功");
                // 支付成功后上报付费事件（payAmount 单位：分，内部自动 flush）
                GravityDockSDKTrack.trackPayEvent(100, "CNY",
                        "order_" + System.currentTimeMillis(), "60钻石", "GravityDock");
            }

            @Override
            public void onPayFail(String msg) {
                showMsg("支付失败: " + msg);
            }

            @Override
            public void onPayCancel() {
                showMsg("支付取消");
            }
        });
    }

    // ───────────────────────────── 自定义事件 ─────────────────────────────

    public void trackCustomClick(View view) {
        JSONObject properties = new JSONObject();
        try {
            properties.put("level", 10);
            properties.put("scene", "demo");
        } catch (Exception ignore) {
        }
        GravityDockSDKTrack.track("dock_demo_event", properties);
        showMsg("已上报自定义事件 dock_demo_event");
    }

    public void trackRegisterClick(View view) {
        // 上报注册事件（内部自动 flush）
        GravityDockSDKTrack.trackRegisterEvent();
        showMsg("已上报注册事件");
    }

    public void trackLoginClick(View view) {
        GravityDockSDKTrack.trackLoginEvent();
        showMsg("已上报登录事件");
    }

    public void trackLogoutClick(View view) {
        GravityDockSDKTrack.trackLogoutEvent();
        showMsg("已上报登出事件");
    }

    public void trackAdClick(View view) {
        // 上报一组广告相关事件（ecpm 单位：元）
        GravityDockSDKTrack.trackAdLoadEvent("topon", "placement_id", "ad_source_id", "reward", "csj");
        GravityDockSDKTrack.trackAdShowEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        GravityDockSDKTrack.trackAdClickEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        GravityDockSDKTrack.trackAdPlayStartEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1);
        GravityDockSDKTrack.trackAdPlayEndEvent("topon", "placement_id", "ad_source_id", "reward", "csj", 1, 50, false);
        showMsg("已上报广告事件组");
    }

    // ───────────────────────────── 用户属性 ─────────────────────────────

    public void userSetClick(View view) {
        JSONObject properties = new JSONObject();
        try {
            properties.put("$name", "dockDemoUser");
            properties.put("$gender", "男");
        } catch (Exception ignore) {
        }
        GravityDockSDKTrack.userSet(properties);
        showMsg("已调用 user_set");
    }

    public void userSetOnceClick(View view) {
        JSONObject properties = new JSONObject();
        try {
            properties.put("$first_visit_time", "2026-01-01 00:00:00");
        } catch (Exception ignore) {
        }
        GravityDockSDKTrack.userSetOnce(properties);
        showMsg("已调用 user_setOnce");
    }

    public void userIncrementClick(View view) {
        GravityDockSDKTrack.userIncrement("$age", 1);
        showMsg("已调用 user_increment");
    }

    public void userMaxClick(View view) {
        GravityDockSDKTrack.userMax("ad_ecpm_max", 300);
        showMsg("已调用 user_max");
    }

    public void userMinClick(View view) {
        GravityDockSDKTrack.userMin("ad_ecpm_min", 100);
        showMsg("已调用 user_min");
    }

    public void userAppendClick(View view) {
        try {
            JSONArray movies = new JSONArray();
            movies.put("Interstellar");
            JSONObject properties = new JSONObject();
            properties.put("Movies", movies);
            GravityDockSDKTrack.userAppend(properties);
            showMsg("已调用 user_append");
        } catch (Exception ignore) {
        }
    }

    public void userUniqAppendClick(View view) {
        try {
            JSONArray movies = new JSONArray();
            movies.put("Interstellar");
            JSONObject properties = new JSONObject();
            properties.put("Movies", movies);
            GravityDockSDKTrack.userUniqAppend(properties);
            showMsg("已调用 user_uniqAppend");
        } catch (Exception ignore) {
        }
    }

    public void userUnsetClick(View view) {
        GravityDockSDKTrack.userUnset("$age");
        showMsg("已调用 user_unset");
    }

    public void userDeleteClick(View view) {
        GravityDockSDKTrack.userDelete();
        showMsg("已调用 user_delete");
    }

    // ───────────────────────────── 公共事件属性 ─────────────────────────────

    public void setSuperPropertiesClick(View view) {
        try {
            JSONObject superProperties = new JSONObject();
            superProperties.put("channel", "developer");
            superProperties.put("age", 2);
            GravityDockSDKTrack.setSuperProperties(superProperties);
            showMsg("已设置公共事件属性");
        } catch (Exception ignore) {
        }
    }

    public void unsetSuperPropertiesClick(View view) {
        GravityDockSDKTrack.unsetSuperProperties("channel");
        showMsg("已移除公共事件属性 channel");
    }

    public void clearSuperPropertiesClick(View view) {
        GravityDockSDKTrack.clearSuperProperties();
        showMsg("已清空公共事件属性");
    }

    // ───────────────────────────── 立即上报 ─────────────────────────────

    public void flushClick(View view) {
        GravityDockSDKTrack.flush();
        showMsg("已触发 flush");
    }

    // ───────────────────────────── 游戏生命周期 / 浮标 ─────────────────────────────

    public void gameBeginClick(View view) {
        GravityDockSDK.gameBegin(this);
        showMsg("已调用 gameBegin");
    }

    public void gameEndClick(View view) {
        GravityDockSDK.gameEnd(this);
        showMsg("已调用 gameEnd");
    }

    public void showFloatWindowClick(View view) {
        GravityDockSDK.showFloatWindow(this);
        showMsg("已展示浮标（不支持的渠道自动忽略）");
    }

    public void hideFloatWindowClick(View view) {
        GravityDockSDK.hideFloatWindow(this);
        showMsg("已隐藏浮标（不支持的渠道自动忽略）");
    }

    // ───────────────────────────── 退出 ─────────────────────────────

    public void exitClick(View view) {
        GravityDockSDK.exitApp(this);
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
