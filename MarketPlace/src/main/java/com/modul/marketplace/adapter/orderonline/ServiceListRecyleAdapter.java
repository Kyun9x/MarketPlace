package com.modul.marketplace.adapter.orderonline;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.modul.marketplace.holder.orderonline.HeaderServiceListRecycleHolder;
import com.modul.marketplace.holder.orderonline.ServicelistRecycleHolder;
import com.modul.marketplace.model.orderonline.DmServiceListOrigin;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

/**
 * Created by NgocLe on 10/02/2019.
 */
public class ServiceListRecyleAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    private ArrayList<DmServiceListOrigin> data;
    private LayoutInflater inflater;
    private Activity mActivity;
    private Boolean isLoaderVisible = false;


    private ServicelistRecycleHolder.OnItemClickRycle mOnItemClickRycle;

    public ServiceListRecyleAdapter(Activity mAc, ArrayList<DmServiceListOrigin> data, ServicelistRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        this.mActivity = mAc;
        this.data = data;
        this.mOnItemClickRycle = mOnItemClickRycle;
        inflater = (LayoutInflater) mAc
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ServicelistRecycleHolder.newInstance(mActivity, inflater, mOnItemClickRycle);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ServicelistRecycleHolder mHolder = (ServicelistRecycleHolder) holder;
        mHolder.setElement(data.get(position));
    }

    @Override
    public long getHeaderId(int i) {
//        Log.i("header", "" + data.size());
        String name = data.get(i).getType();
        long hascode = name.hashCode();
        if (hascode < 0) hascode = hascode * -1;
        return hascode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
        return HeaderServiceListRecycleHolder.newInstance(mActivity, inflater);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        HeaderServiceListRecycleHolder menuHeaderRecycleHolder = (HeaderServiceListRecycleHolder) viewHolder;
        menuHeaderRecycleHolder.setElement(data.get(i).getType());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<DmServiceListOrigin> item) {
        data.addAll(item);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        data.add(new DmServiceListOrigin());
        notifyItemInserted(data.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = data.size() - 1;
        DmServiceListOrigin item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public DmServiceListOrigin getItem(int position) {
        return data.get(position);
    }

}
