package me.zayz.socialplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import me.zayz.socialplus.R;
import me.zayz.socialplus.databinding.ItemEngagementBinding;
import me.zayz.socialplus.models.EngagementItem;
import me.zayz.socialplus.models.InstagramEngagement;
import me.zayz.socialplus.models.InstagramMedia;

/**
 * Created by zayz on 11/1/17.
 * <p>
 * GridViewAdapter for the EngagementFragment
 */
public class EngagementListAdapter extends BaseAdapter {

    public static final int[] TITLE_IDS = {
            R.string.item_engagement_best_photo,
            R.string.item_engagement_best_video,
            R.string.item_engagement_least_comments
    };

    private Context mContext;
    private InstagramEngagement mEngagement;

    public EngagementListAdapter(Context context, InstagramEngagement engagement) {

        this.mContext = context;
        this.mEngagement = engagement;
    }

    @Override
    public int getCount() {

        return TITLE_IDS.length;
    }

    @Override
    public List<InstagramMedia> getItem(int position) {

        int item = TITLE_IDS[position];

        switch (item) {
            default:
            case R.string.item_engagement_best_photo: {
                return mEngagement.bestPhoto;
            }
            case R.string.item_engagement_best_video: {
                return mEngagement.bestVideo;
            }
            case R.string.item_engagement_least_comments: {
                return mEngagement.leastComments;
            }
        }
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemEngagementBinding item;

        int stringId = TITLE_IDS[position];
        String title = mContext.getString(stringId);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);

            item = ItemEngagementBinding.inflate(
                    inflater, parent, false);

            convertView = item.getRoot();
            convertView.setTag(item);
        } else {
            item = (ItemEngagementBinding) convertView.getTag();
        }

        item.setItem(new EngagementItem(title));
        item.executePendingBindings();

        return convertView;
    }
}
