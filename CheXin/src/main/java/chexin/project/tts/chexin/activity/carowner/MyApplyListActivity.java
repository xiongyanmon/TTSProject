package chexin.project.tts.chexin.activity.carowner;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import chexin.project.tts.chexin.BaseActivity;
import chexin.project.tts.chexin.R;
import chexin.project.tts.chexin.adapter.carowner.MyApplyListAdapter;
import chexin.project.tts.chexin.bean.ApplyBean;
import chexin.project.tts.chexin.common.MyAccountMoudle;
import tts.moudle.api.Host;
import tts.moudle.api.bean.BarBean;
import tts.moudle.api.utils.CustomUtils;
import tts.moudle.api.widget.RecyclerViewAutoRefreshUpgraded;

/**
 * 已申请条目
 */
public class MyApplyListActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerViewAutoRefreshUpgraded applyList;
    private MyApplyListAdapter applyListAdapter;
    private String relation_id;
    private List<ApplyBean> mData;
    private int currentPage = 1;
    private int pos;
    private final int doAction = 1001;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_apply_list);
        setTitle(new BarBean().setMsg("申请条目"));
        relation_id = getIntent().getStringExtra("relation_id");
        findAllView();
        startRequestData(getData);

    }

    private void findAllView() {
        applyList = (RecyclerViewAutoRefreshUpgraded) findViewById(R.id.applyList);

    }

    private void adapterMyApply() {
        applyList.setLayoutManager(new LinearLayoutManager(this));
        applyListAdapter = new MyApplyListAdapter(this, mData, 1);
        applyList.setAdapter(applyListAdapter);
        applyList.setLoadMore(true);
        applyList.setHeadVisible(true);
        applyList.setOnRefreshListener(new RecyclerViewAutoRefreshUpgraded.OnRefreshListener() {
            @Override
            public void onLoadMore() {
                currentPage++;
                startRequestData(loadMore);
            }

            @Override
            public void onRefreshData() {
                startRequestData(getData);
            }
        });
        applyListAdapter.setRedActionOnClickListener(new MyApplyListAdapter.RedActionOnClickListener() {
            @Override
            public void redAction(int postion) {
                pos = postion;
                state = "2";
                startRequestData(doAction);
            }
        });
        applyListAdapter.setBlueActionOnClickListener(new MyApplyListAdapter.BlueActionOnClickListener() {
            @Override
            public void blueAction(int postion) {
                pos = postion;
                state = "1";
                startRequestData(doAction);
            }
        });
    }

    @Override
    protected void startRequestData(int index) {
        super.startRequestData(index);
        Map<String, String> params;
        switch (index) {
            case getData:
                currentPage = 1;
                params = new ArrayMap<>();
                params.put("UserId", MyAccountMoudle.getInstance().getUserInfo().getId() + "");
                params.put("token", MyAccountMoudle.getInstance().getUserInfo().getToken());
                params.put("relation_id", relation_id + "");
                params.put("page", "1");
                getDataWithPost(getData, Host.hostUrl + "car.api?getApplyBeansByCarId", params);
                break;
            case loadMore:
                params = new ArrayMap<>();
                params.put("UserId", MyAccountMoudle.getInstance().getUserInfo().getId() + "");
                params.put("token", MyAccountMoudle.getInstance().getUserInfo().getToken());
                params.put("relation_id", relation_id + "");
                params.put("page", currentPage + "");
                getDataWithPost(loadMore, Host.hostUrl + "car.api?getApplyBeansByCarId", params);
                break;
            case doAction:
                params = new ArrayMap<>();
                params.put("UserId", MyAccountMoudle.getInstance().getUserInfo().getId() + "");
                params.put("token", MyAccountMoudle.getInstance().getUserInfo().getToken());
                params.put("apply_id", mData.get(pos).getApply_id() + "");
                params.put("state", state);
                getDataWithPost(doAction, Host.hostUrl + "car.api?setCarApplyState", params);
                break;
        }
    }

    @Override
    protected void doSuccess(int index, String response) {
        super.doSuccess(index, response);
        switch (index) {
            case getData:
                Logger.d(response);
                mData = new Gson().fromJson(response, new TypeToken<List<ApplyBean>>() {
                }.getType());
                if (mData.size() == 0) {
                    CustomUtils.showTipShort(this, "暂无数据");
                }
                adapterMyApply();
                break;
            case loadMore:
                List<ApplyBean> temp = new Gson().fromJson(response, new TypeToken<List<ApplyBean>>() {
                }.getType());
                if (temp.size() == 0) {
                    CustomUtils.showTipShort(this, "暂无更多数据");
                    return;
                }
                mData.addAll(temp);
                applyList.notifyDataSetChanged();
                break;
            case doAction:
                CustomUtils.showTipShort(this, "操作成功");
                finish();
                break;
        }
        applyList.setOnRefreshFinished(true);
    }

    @Override
    protected void doFailed(int what, int index, String response) {
        super.doFailed(what, index, response);
        applyList.setOnRefreshFinished(true);
    }

    @Override
    public void onClick(View v) {

    }
}
