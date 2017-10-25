package me.zayz.socialplus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import me.zayz.socialplus.R;
import me.zayz.socialplus.databinding.ItemUserListBinding;
import me.zayz.socialplus.interfaces.ActivityCallback;
import me.zayz.socialplus.models.InstagramPublicUser;
import me.zayz.socialplus.utils.DateUtil;

/**
 * Created by zayz on 11/1/17.
 * <p>
 * GridViewAdapter for the UserListFragment
 */
public class UserListAdapter extends BaseAdapter {

    private Context mContext;
    private ActivityCallback mCallback;
    private List<InstagramPublicUser> mList;

    public UserListAdapter(Context context, List<InstagramPublicUser> list) {

        this.mContext = context;
        this.mCallback = (ActivityCallback) context;
        this.mList = list;
    }

    @Override
    public int getCount() {

        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);

            final ItemUserListBinding item =
                    ItemUserListBinding.inflate(inflater, parent, false);
            InstagramPublicUser user = mList.get(position);

            item.setItem(user);
            item.executePendingBindings();

            mCallback.getInstagram().getImage(user.profilePicture,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {

                            item.profilePicture.setImageBitmap(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            item.time.setText(DateUtil.getTimeFromToday(user.time));

            if (DateUtil.isToday(user.time)) {
                item.time.setBackgroundDrawable(mContext.getResources().getDrawable(
                        R.drawable.rounded_view_positive));
            } else {
                item.time.setBackgroundDrawable(mContext.getResources().getDrawable(
                        R.drawable.rounded_view_neutral));
            }

            convertView = item.getRoot();
        }

        return convertView;
    }
}
