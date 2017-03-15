package g.s.l.gslcustomview.ui.item_animatable_layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import g.s.l.gslcustomview.R;

/**
 * Created by Deemo on 2017/3/15.
 */

public class HomeItemAnimatableAdapter extends ItemAnimatableAdapter<String> {


    public HomeItemAnimatableAdapter(Context context, List<ItemAnimatableLayout.ViewAnimationData> data, List<String> strings) {
        super(context, data, strings);
    }

    @Override
    protected int itemLayoutId() {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(itemLayoutId(), parent, false);
            viewHolder = new ViewHolder();
//            viewHolder.ivCover = (ImageView) convertView.findViewById(R.id.iv_home_item_cover);
//            viewHolder.ivPlay = (ImageView) convertView.findViewById(R.id.iv_home_item_cover);
//            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_home_item_title);
//            viewHolder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_home_item_author);
//            viewHolder.tvDuration = (TextView) convertView.findViewById(R.id.tv_home_item_duration);
//            viewHolder.tvTimes = (TextView) convertView.findViewById(R.id.tv_home_item_times);
            convertView.setTag(R.id.tag_viewHolder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.id.tag_viewHolder);
        }
        viewHolder.position = position;
        dataToView(viewHolder);
        return convertView;
    }

    protected void dataToView(ViewHolder viewHolder) {

    }

    protected static class ViewHolder {
        public ImageView ivCover;
        public ImageView ivPlay;
        public TextView tvTitle;
        public TextView tvAuthor;
        public TextView tvTimes;
        public TextView tvDuration;
        public int position;
        public String entity;
    }
}
