package tts.moudle.api.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import tts.moudle.api.bean.BankBean;
import tts.moudle.api.bean.BannerBean;
import tts.moudle.ttsmoduleapi.R;

/**
 * Created by sjb on 2016/2/26.
 */
public class BannerAdapter extends PagerAdapter {
    private List<BannerBean> bannerBeans = null;
    private LayoutInflater inflater;
    private Context context;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        public void doClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public BannerAdapter(Context context, List<BannerBean> bannerBeans) {
        this.context = context;
        if (context != null) {
            inflater = LayoutInflater.from(context);
            this.bannerBeans = bannerBeans;
        } else {
            bannerBeans = null;
        }

    }

    @Override
    public int getCount() {
        return bannerBeans == null ? 0 : bannerBeans.size();
    }

    @Override
    public Object instantiateItem(View container, final int position) {
        View view = inflater.inflate(R.layout.banner_item, null);
        ImageView img = (ImageView) view.findViewById(R.id.AdvertImg);
        Glide.with(context)
                .load(bannerBeans.get(position).getImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.doClick(position);
                }
            }
        });
        ((ViewPager) container).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
