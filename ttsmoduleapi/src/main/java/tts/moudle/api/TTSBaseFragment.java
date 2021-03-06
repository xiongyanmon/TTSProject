package tts.moudle.api;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import okhttp3.Request;
import tts.moudle.api.bean.BarBean;
import tts.moudle.api.bean.MenuItemBean;
import tts.moudle.api.utils.CustomUtils;
import tts.moudle.api.utils.SystemUtils;
import tts.moudle.ttsmoduleapi.R;

/**
 * Created by sjb on 2016/1/7.
 */
public class TTSBaseFragment extends Fragment {
    protected boolean isLoad;
    protected View rootView;
    protected ProgressDialog mProgressDialog;

    protected Toolbar toolbar;
    private TextView titleTxt;
    private LinearLayout menuList;
    private TextView subTitleTxt;
    private RelativeLayout RLBtn;
    private TextView rightTxt;
    private TextView iconImg;

    protected View setContentView(int res, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(res, null);
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        // mProgressDialog.setCancelable(false);//除了自己以外的所有地方 包含返回键
        mProgressDialog.setCanceledOnTouchOutside(false);// 点击自己以外的地方 不允许放弃 //
        // 不包含返回键
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // Toast.makeText(BaseActivity.this, "放弃加载..",
                // Toast.LENGTH_SHORT).show();
                OkHttpUtils.getInstance().cancelTag(this);
                getActivity().finish();
            }
        });
    }

    /**
     * 设置标题（推荐）
     *
     * @param barBean
     */
    protected void setTitle(BarBean barBean) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (barBean.getColorid() != 0) {
            toolbar.setBackgroundColor(barBean.getColorid());
        }
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        titleTxt.setText(barBean.getMsg() == null ? "" : barBean.getMsg());
        if (barBean.getTextColorid() != 0) {
            titleTxt.setTextColor(barBean.getTextColorid());
        }
        iconImg = (TextView) toolbar.findViewById(R.id.iconImg);
        if (barBean.getIconId() != 0) {
            iconImg.setBackgroundResource(barBean.getIconId());
        } else {
            iconImg.setBackgroundResource(R.drawable.back);
        }
        if (barBean.getSubTitle() == null || barBean.getSubTitle().equals("")) {
            subTitleTxt = (TextView) toolbar.findViewById(R.id.subTitle);
            subTitleTxt.setVisibility(View.GONE);
        } else {
            subTitleTxt = (TextView) toolbar.findViewById(R.id.subTitle);
            subTitleTxt.setVisibility(View.VISIBLE);
            subTitleTxt.setText(barBean.getSubTitle());
            if (barBean.getSubTextColorid() != 0) {
                subTitleTxt.setTextColor(barBean.getSubTextColorid());
            }
        }

        RLBtn = (RelativeLayout) toolbar.findViewById(R.id.RLBtn);
        RLBtn.setVisibility(barBean.isRemoveBack() ? View.GONE : View.VISIBLE);
        RLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doIcon();
            }
        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    /**
     * 图标点击事件 回调
     */
    public void doIcon() {
        getActivity().setResult(getActivity().RESULT_CANCELED);
        getActivity().finish();
    }

    /**
     * 不带返回键的 toolbar
     *
     * @param msg
     * @param IsRemoveBack
     * @Deprecated {@link #setTitle(BarBean barBean)}
     */
    protected void setTitle(String msg, boolean IsRemoveBack) {
        setTitle(new BarBean().setMsg(msg).setIsRemoveBack(IsRemoveBack));
        /*toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        titleTxt.setText(msg);
        RLBtn = (RelativeLayout) toolbar.findViewById(R.id.RLBtn);
        RLBtn.setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
    }

    /**
     * 可以设置默认toolbar背景色
     *
     * @param msg
     * @param colorId
     * @Deprecated {@link #setTitle(BarBean barBean)}
     */
    protected void setTitle(String msg, int colorId) {
        setTitle(new BarBean().setMsg(msg).setColorid(colorId));
        /*toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(colorId);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        titleTxt.setText(msg);
        RLBtn = (RelativeLayout) toolbar.findViewById(R.id.RLBtn);
        RLBtn.setVisibility(View.VISIBLE);
        RLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doIcon();
            }
        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
    }

    /**
     * @param subTitle
     * @param msg
     * @Deprecated {@link #setTitle(BarBean barBean)}
     */
    protected void setTitle(String subTitle, String msg) {
        setTitle(new BarBean().setMsg(msg).setSubTitle(subTitle));
        /*toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        titleTxt.setText(msg);
        subTitleTxt = (TextView) toolbar.findViewById(R.id.subTitle);
        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(subTitle);
        RLBtn = (RelativeLayout) toolbar.findViewById(R.id.RLBtn);
        RLBtn.setVisibility(View.VISIBLE);
        RLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doIcon();
            }
        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
    }

    /**
     * @param msg
     * @Deprecated {@link #setTitle(BarBean barBean)}
     */
    protected void setTitle(String msg) {
        setTitle(new BarBean().setMsg(msg));
        /*toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        titleTxt = (TextView) toolbar.findViewById(R.id.titleTxt);
        titleTxt.setText(msg);
        RLBtn = (RelativeLayout) toolbar.findViewById(R.id.RLBtn);
        RLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doIcon();
            }
        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);*/
    }


    /**
     * {@link # addMenu(final MenuItemBean menuItemBean) }
     * 已过时
     */
    @Deprecated
    protected void setRightTitle(String msg) {
        rightTxt = (TextView) toolbar.findViewById(R.id.rightTxt);
        rightTxt.setText(msg);
        menuList = (LinearLayout) getActivity().findViewById(R.id.menuList);
        menuList.setVisibility(View.VISIBLE);
        menuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doMenu(null);
            }
        });
    }


    protected void addMenu(final MenuItemBean menuItemBean) {
        if (menuList == null) {
            menuList = (LinearLayout) rootView.findViewById(R.id.menuList);
            menuList.setVisibility(View.VISIBLE);
        }
        View view = View.inflate(getActivity(), R.layout.menu_item, null);

        TextView menuItem = (TextView) view.findViewById(R.id.menuItem);
        menuItem.setVisibility(View.VISIBLE);
        menuItem.setText(menuItemBean.getTitle());
        menuItem.setBackgroundResource(menuItemBean.getIcon());
        if (menuItemBean.getTextSise() != 0) {
            menuItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuItemBean.getTextSise());
        }
        if (menuItemBean.getTextColor() != 0) {
            menuItem.setTextColor(menuItemBean.getTextColor());
        }
        menuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doMenu(menuItemBean);
            }
        });

        menuList.addView(view);
    }

    protected void removeMenu() {
        if (toolbar == null) {
            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        }
        if (menuList == null) {
            menuList = (LinearLayout) rootView.findViewById(R.id.menuList);
        }
        menuList.removeAllViews();
    }

    /**
     * 自定义菜单处理事件
     *
     * @param menuItemBean
     */
    protected void doMenu(MenuItemBean menuItemBean) {

    }


    /**
     * 展示提示信息
     *
     * @param Msg 提示内容
     */
    protected void showTipMsg(String Msg) {
        mProgressDialog.setMessage(Msg);
        mProgressDialog.show();
    }

    /**
     * get方式取网络数据
     *
     * @param index 访问标识
     * @param url   接口地址
     */
    protected void getDataWithGet(final int index, String url) {
        getDataWithGet(index, url, false);
    }

    protected void getDataWithGet(final int index, String url, final boolean dataOnly) {
        if (!SystemUtils.isNetAvailable(getActivity())) {
            doFailed(2, index, "");
        }
        OkHttpUtils
                .get()
                .url(url).tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        doFailed(3, index, "");
                    }

                    @Override
                    public void onResponse(String response) {
                        if (dataOnly) {
                            doSuccess(index, response);
                        } else {
                            try {
                                JsonAnalysis(index, new JSONObject(response));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                doFailed(3, index, e.getMessage());
                            }
                        }
                    }
                });
    }


    /**
     * post方式取数据
     *
     * @param index  访问标识
     * @param url    接口地址
     * @param params 参数
     */

    protected void getDataWithPost(final int index, String url, Map<String, String> params) {
        getDataWithPost(index, url, params, false);
    }

    protected void getDataWithPost(final int index, String url, Map<String, String> params, final boolean dataOnly) {
        if (!SystemUtils.isNetAvailable(getActivity())) {
            doFailed(2, index, "");
        }
        OkHttpUtils
                .post()
                .url(url)
                .params(params).tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        doFailed(3, index, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        if (dataOnly) {
                            doSuccess(index, response);
                        } else {
                            try {
                                JsonAnalysis(index, new JSONObject(response));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                doFailed(3, index, e.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * 上传文件
     *
     * @param index  访问标识
     * @param url    接口地址
     * @param params 参数
     * @param files  上传的文件
     */
    protected void uploadFile(final int index, String url, Map<String, String> params, List<PostFormBuilder.FileInput> files) {
        uploadFile(index, url, params, files, false);
    }

    protected void uploadFile(final int index, String url, Map<String, String> params, List<PostFormBuilder.FileInput> files, final boolean dataOnly) {
        if (!SystemUtils.isNetAvailable(getActivity())) {
            doFailed(2, index, "");
        }
        OkHttpUtils
                .post().tag(this)
                .url(url)
                .File(files)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        doFailed(3, index, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        if (dataOnly) {
                            doSuccess(index, response);
                        } else {
                            try {
                                JsonAnalysis(index, new JSONObject(response));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                doFailed(3, index, e.getMessage());
                            }
                        }
                    }
                });
    }

    private void JsonAnalysis(int index, JSONObject jsonObject) {
        try {
            String status = jsonObject.getString("status");
            if (status.equals("ok")) {
                doSuccess(index, jsonObject.getString("data"));
            } else if (status.equals("error")) {
                //老项目
                /*JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                doFailed(1, index, jsonObject1.getString("ErrMsg"));*/
                doFailed(1, index, jsonObject.getString("error"));
            } else if (status.equals("pending")) {
                doFailed(4, index, jsonObject.getString("error"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            doFailed(1, index, e.getMessage());
        }

    }

    /**
     * 下载文件
     *
     * @param index    访问标识
     * @param url      接口地址
     * @param path     文件保存路径
     * @param fileName 文件名
     */
    protected void loadFile(final int index, String url, String path, String fileName, final ProgressBar mProgressBar) {
        if (!SystemUtils.isNetAvailable(getActivity())) {
            doFailed(2, index, "");
        }
        OkHttpUtils
                .get()//
                .tag(this)
                .url(url)//
                .build()//
                .execute(new FileCallBack(path, fileName)//
                {
                    @Override
                    public void inProgress(float progress) {
                        if (mProgressBar != null) {
                            mProgressBar.setProgress((int) (100 * progress));
                        }
                    }

                    @Override
                    public void onError(Request request, Exception e) {
                        doFailed(3, index, e.getMessage());
                    }

                    @Override
                    public void onResponse(File file) {
                        doSuccess(index, file);
                    }
                });
    }

    /**
     * 访问接口成功之后处理
     */
    protected void doSuccess(int index, String response) {
        mProgressDialog.dismiss();
    }

    /**
     * 下载文件成功
     */
    protected void doSuccess(int index, File file) {
        mProgressDialog.dismiss();
    }

    protected void doFailed(int what, int index, String response) {
        mProgressDialog.dismiss();
        switch (what) {
            case 1:
                doDataFailed(index, response);
                break;
            case 2:
                doNetFailed(index, response);
                break;
            case 3:
                doErrFailed(index, response);
                break;
            case 4:
                doPendingFailed(index, response);
                break;
        }
    }

    /**
     * 一些错误需要预处理
     *
     * @param error
     */
    protected void doPendingFailed(int index, String error) {

    }

    /**
     * 返回数据错误方法
     *
     * @param index
     * @param response
     */
    protected void doDataFailed(int index, String response) {
        CustomUtils.showTipShort(getActivity(), response);
    }

    /**
     * 无网络连接错误返回
     *
     * @param index
     * @param response
     */
    protected void doNetFailed(int index, String response) {
        CustomUtils.showTipShort(getActivity(), "无网络连接");
    }

    /**
     * 传参出现错误返回
     *
     * @param index
     * @param response
     */
    protected void doErrFailed(int index, String response) {
        Log.i("", "=====================" + response);
        CustomUtils.showTipShort(getActivity(), "网络连接异常");
    }

    /**
     * 开始请求数据
     *
     * @param index
     */
    protected void startRequestData(int index) {

    }

    /**
     * @param index      必须在八个字节以内
     * @param permission
     */
    protected void applyPermission(int index, @NonNull String permission) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{permission},
                    index);
        } else {
            startApplyPermissions(index);
        }
    }

    /**
     * 权限成功后 执行的方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startApplyPermissions(requestCode);
        } else {
            applyPermissionsFailed();
        }
    }

    protected void startApplyPermissions(int index) {

    }

    protected void applyPermissionsFailed() {
        CustomUtils.showTipShort(getActivity(), "权限申请失败");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
