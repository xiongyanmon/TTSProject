package chexin.project.tts.chexin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chexin.project.tts.chexin.activity.goodsowner.GoodsDetailsActivity;
import chexin.project.tts.chexin.adapter.GoodsAdapter;
import chexin.project.tts.chexin.bean.GoodsBean;
import chexin.project.tts.chexin.common.ConfigMoudle;
import chexin.project.tts.chexin.widget.selectPopupWindow.SelectPopupWindow;
import tts.moudle.api.Host;
import tts.moudle.api.TTSBaseAdapterRecyclerView;
import tts.moudle.api.bean.BarBean;
import tts.moudle.api.utils.CustomUtils;
import tts.moudle.api.widget.RecyclerViewAutoRefreshUpgraded;
import tts.moudle.api.widget.TTSRadioButton;


/**
 * 查找货源(货源信息)
 */
public class GoodsFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerViewAutoRefreshUpgraded goodsList;
    private GoodsAdapter goodsAdapter;
    private List<GoodsBean> mData = new ArrayList<>();
    private SelectPopupWindow selectPopupWindow;
    private String FromProvince = "";
    private String FromCity = "";
    private String FromCountry = "";
    private String ToProvince = "";
    private String ToCity = "";
    private String ToCountry = "";
    private String SKUType = "";
    private String SKULong = "";
    private int page = 1;
    private TTSRadioButton carLongBtn, carTypeBtn, destinationBtn, originatingPlaceBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setContentView(R.layout.goods_fragment, inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(new BarBean().setMsg("货源信息").setIsRemoveBack(true));
        findAllView();
        startRequestData(getData);
        mProgressDialog.show();
    }

    private void adapterGoods() {
        goodsAdapter = new GoodsAdapter(getActivity(), mData);
        goodsList.setAdapter(goodsAdapter);
        goodsList.setOnRefreshListener(new RecyclerViewAutoRefreshUpgraded.OnRefreshListener() {
            @Override
            public void onLoadMore() {
                page++;
                startRequestData(loadMore);
            }

            @Override
            public void onRefreshData() {
                startRequestData(getData);
            }
        });
        goodsAdapter.setOnItemClickListener(new TTSBaseAdapterRecyclerView.OnItemClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                startActivityForResult(new Intent(getActivity(), GoodsDetailsActivity.class).putExtra("data", mData.get(position)), 1001);
            }

            @Override
            public void onLongClick(View itemView, int position) {

            }
        });
    }

    private void findAllView() {
        goodsList = (RecyclerViewAutoRefreshUpgraded) rootView.findViewById(R.id.goodsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        goodsList.setLayoutManager(layoutManager);
        goodsList.setLoadMore(true);
        goodsList.setHeadVisible(true);
        originatingPlaceBtn = (TTSRadioButton) rootView.findViewById(R.id.originatingPlaceBtn);
        destinationBtn = (TTSRadioButton) rootView.findViewById(R.id.destinationBtn);
        carTypeBtn = (TTSRadioButton) rootView.findViewById(R.id.carTypeBtn);
        carLongBtn = (TTSRadioButton) rootView.findViewById(R.id.carLongBtn);
        originatingPlaceBtn.setOnClickListener(this);
        destinationBtn.setOnClickListener(this);
        carTypeBtn.setOnClickListener(this);
        carLongBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.originatingPlaceBtn:
                selectPopupWindow = new SelectPopupWindow(getActivity(), ConfigMoudle.getInstance().getProvinceBeans(), 1, "选择始发地");
                selectPopupWindow.showAtLocation(carLongBtn, Gravity.CENTER, 0, 0);
                selectPopupWindow.setOnClickListener(new SelectPopupWindow.OnClickListener() {
                    @Override
                    public void doClick(String province, String city, String area, String other, int pos) {
                        FromProvince = province;
                        FromCity = city;
                        FromCountry = area;
                        startRequestData(getData);
                    }
                });
                break;
            case R.id.destinationBtn:
                selectPopupWindow = new SelectPopupWindow(getActivity(), ConfigMoudle.getInstance().getProvinceBeans(), 1, "选择目的地");
                selectPopupWindow.showAtLocation(carLongBtn, Gravity.CENTER, 0, 0);
                selectPopupWindow.setOnClickListener(new SelectPopupWindow.OnClickListener() {
                    @Override
                    public void doClick(String province, String city, String area, String other, int pos) {
                        FromProvince = province;
                        FromCity = city;
                        FromCountry = area;
                        startRequestData(getData);
                    }
                });
                break;
            case R.id.carTypeBtn:
                selectPopupWindow = new SelectPopupWindow(getActivity(), ConfigMoudle.getInstance().getCarTypeBeans(), 2, "选择车型");
                selectPopupWindow.showAtLocation(carLongBtn, Gravity.CENTER, 0, 0);
                selectPopupWindow.setOnClickListener(new SelectPopupWindow.OnClickListener() {
                    @Override
                    public void doClick(String province, String city, String area, String other, int pos) {
                        SKUType = other;
                        startRequestData(getData);
                    }
                });
                break;
            case R.id.carLongBtn:
                selectPopupWindow = new SelectPopupWindow(getActivity(), ConfigMoudle.getInstance().getCarLongBeans(), 2, "选择车长");
                selectPopupWindow.showAtLocation(carLongBtn, Gravity.CENTER, 0, 0);
                selectPopupWindow.setOnClickListener(new SelectPopupWindow.OnClickListener() {
                    @Override
                    public void doClick(String province, String city, String area, String other, int pos) {
                        SKULong = other;
                        startRequestData(getData);
                    }
                });
                break;
        }
    }

    @Override
    protected void startRequestData(int index) {
        super.startRequestData(index);
        Map<String, String> params;
        switch (index) {
            case getData:
                params = new ArrayMap<>();
                params.put("FromProvince", FromProvince);
                params.put("FromCity", FromCity);
                params.put("FromCountry", FromCountry);
                params.put("ToProvince", ToProvince);
                params.put("ToCity", ToCity);
                params.put("ToCountry", ToCountry);
                params.put("SKUType", SKUType);
                params.put("SKULong", SKULong);
                params.put("page", "1");
                getDataWithPost(getData, Host.hostUrl + "/goods.api?searchGoodsByCondition", params);
                break;
            case loadMore:
                params = new ArrayMap<>();
                params.put("FromProvince", FromProvince);
                params.put("FromCity", FromCity);
                params.put("FromCountry", FromCountry);
                params.put("ToProvince", ToProvince);
                params.put("ToCity", ToCity);
                params.put("ToCountry", ToCountry);
                params.put("SKUType", SKUType);
                params.put("SKULong", SKULong);
                params.put("page", page + "");
                getDataWithPost(loadMore, Host.hostUrl + "/goods.api?searchGoodsByCondition", params);
                break;
        }
    }

    @Override
    protected void doSuccess(int index, String response) {
        super.doSuccess(index, response);
        switch (index) {
            case getData:
                Logger.w(response);
                mData = new Gson().fromJson(response, new TypeToken<List<GoodsBean>>() {
                }.getType());
                Logger.d(mData.size() + "");
                if (mData.size() == 0) {
                    CustomUtils.showTipShort(getActivity(), "没有更多数据啦~");
                }
                adapterGoods();
                mProgressDialog.dismiss();
                break;
            case loadMore:
                List<GoodsBean> temp = new Gson().fromJson(response, new TypeToken<List<GoodsBean>>() {
                }.getType());
                if (temp.size() == 0) {
                    CustomUtils.showTipShort(getActivity(), "没有更多数据啦~");
                }
                mData.addAll(temp);
                goodsList.notifyDataSetChanged();
                Logger.d(mData.size() + "");
                break;
        }
        goodsList.setOnRefreshFinished(true);

    }

    @Override
    protected void doFailed(int what, int index, String response) {
        super.doFailed(what, index, response);
        goodsList.setOnRefreshFinished(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    startRequestData(getData);
                    break;
            }
        }
    }
}
