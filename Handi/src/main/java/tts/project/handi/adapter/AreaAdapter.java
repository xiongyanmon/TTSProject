package tts.project.handi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import tts.project.handi.R;
import tts.project.handi.myInterface.OnItemClickLitener;


/**
 * Created by lenove on 2016/3/23.
 */
public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaAdapterViewHolder> {
    private Context mContext;
    private List<String> mData;
    private OnItemClickLitener mOnItemClickLitener;

    public AreaAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public AreaAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AreaAdapterViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_area, null, false));
    }

    @Override
    public void onBindViewHolder(final AreaAdapterViewHolder holder, final int position) {
        holder.tvArea.setText(mData.get(position) + "");
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class AreaAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvArea;
        private ImageView iconRight;

        public AreaAdapterViewHolder(View itemView) {
            super(itemView);
            tvArea = (TextView) itemView.findViewById(R.id.tv_area);
            iconRight = (ImageView) itemView.findViewById(R.id.icon_right);
        }
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
