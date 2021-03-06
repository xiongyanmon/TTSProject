package tts.moudle.api;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tts.moudle.api.bean.UserBean;
import tts.moudle.api.moudle.AccountMoudle;
import tts.moudle.api.moudle.SharedPreferencesMoudle;
import tts.moudle.api.socialapi.SocialConstants;

/**
 * Created by sjb on 2016/1/6.
 */
public class TTSBaseApplication extends Application {
    public static Context appContext;
    public static boolean isVideoCalling = false;
    public static boolean isVoiceCalling = false;

    private static class InitDataApp {
        protected final static TTSBaseApplication mInstance = new TTSBaseApplication();
    }

    public static TTSBaseApplication getInstance() {
        return InitDataApp.mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        OkHttpUtils.getInstance().debug("tts=");
    }

    /**
     *
     *初始化用户信息
     */
    public void initUser() {
        String json = SharedPreferencesMoudle.getInstance().getJson(getApplicationContext(),"user_login");
        UserBean userBean = new Gson().fromJson(json, UserBean.class);
        AccountMoudle.getInstance().setUserBean(userBean);
    }

    /**
     * 初始化服务器地址
     *
     * @param url
     */
    public void initHost(String url) {
        Host.hostUrl = url;
    }

    /**
     * 初始化微博的配置信息
     *
     * @param APP_KEY
     */
    public void initWb(String APP_KEY) {
        SocialConstants.getInstance().setWbKey(APP_KEY);
        SocialConstants.getInstance().setWbREDIRECT_URL("https://api.weibo.com/oauth2/default.html");
        SocialConstants.getInstance().setWbSCOPE("email,direct_messages_read,direct_messages_write,\"\n" +
                "            + \"friendships_groups_read,friendships_groups_write,statuses_to_me_read,\" + \"follow_app_official_microblog,\"\n" +
                "            + \"invitation_write");
    }

    /**
     * 初始化微博的配置信息
     *
     * @param APP_KEY
     * @param REDIRECT_URL
     * @param SCOPE
     */
    public void initWb(String APP_KEY, String REDIRECT_URL, String SCOPE) {
        SocialConstants.getInstance().setWbKey(APP_KEY);
        SocialConstants.getInstance().setWbREDIRECT_URL(REDIRECT_URL);
        SocialConstants.getInstance().setWbSCOPE(SCOPE);
    }

    /**
     * 初始化QQ的配置信息
     *
     * @param APP_KEY
     */
    public void initQQ(String APP_KEY) {
        SocialConstants.getInstance().setQqKey(APP_KEY);
    }

    /**
     * 初始化微信的配置信息
     *
     * @param APP_KEY
     * @param Sercent
     */
    public void initWx(String APP_KEY, String Sercent) {
        SocialConstants.getInstance().setWxKey(APP_KEY);
        SocialConstants.getInstance().setWxSercent(Sercent);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(appContext.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm
                            .getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

}
