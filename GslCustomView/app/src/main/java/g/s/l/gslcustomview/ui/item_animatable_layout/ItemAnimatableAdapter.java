package g.s.l.gslcustomview.ui.item_animatable_layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import g.s.l.gslcustomview.R;

/**
 * Created by Deemo on 16/9/14.
 * (～ o ～)~zZ
 */
public abstract class ItemAnimatableAdapter<ENTITY> extends BaseAdapter {

    protected Context mContext;
    protected List<ItemAnimatableLayout.ViewAnimationData> mData = new ArrayList<>();
    private List<ENTITY> mEntities = new ArrayList<>();

    public ItemAnimatableAdapter(Context context, List<ItemAnimatableLayout.ViewAnimationData> data, List<ENTITY> entities) {
        mContext = context;
        mData.addAll(data);
        mEntities.addAll(entities);
    }

    public void resetData(List<ItemAnimatableLayout.ViewAnimationData> data, List<ENTITY> entities) {
        mData.clear();
        mData.addAll(data);
        mEntities.clear();
        mEntities.addAll(entities);
        notifyDataSetChanged();
    }

    public ItemAnimatableLayout.ViewAnimationData getViewAnimationData(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mEntities.size();
    }

    @Override
    public ItemAnimatableLayout.ViewAnimationData getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected abstract int itemLayoutId();

    public ENTITY getEntity(int position) {
        return mEntities.get(position);
    }


}
