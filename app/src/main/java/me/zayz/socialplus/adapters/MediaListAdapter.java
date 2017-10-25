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

import me.zayz.socialplus.databinding.ItemMediaListBinding;
import me.zayz.socialplus.interfaces.ActivityCallback;
import me.zayz.socialplus.models.InstagramMedia;

/**
 * Created by zayz on 11/1/17.
 * <p>
 * GridViewAdapter for the MediaListFragment
 */
public class MediaListAdapter extends BaseAdapter {

    private Context mContext;
    private ActivityCallback mCallback;
    private List<InstagramMedia> mList;

    public MediaListAdapter(Context context, List<InstagramMedia> list) {

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

        ItemMediaListBinding item;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);

            item = ItemMediaListBinding.inflate(
                    inflater, parent, false);

            convertView = item.getRoot();
            convertView.setTag(item);
        } else {
            item = (ItemMediaListBinding) convertView.getTag();
        }

        InstagramMedia media = mList.get(position);
        item.setItem(media);

        downloadImage(item, media);

        item.executePendingBindings();

        return convertView;
    }

    private void downloadImage(final ItemMediaListBinding item, InstagramMedia media) {
        mCallback.getInstagram().getImage(media.image,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        item.media.setImageBitmap(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
    }
}
