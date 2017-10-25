package me.zayz.socialplus.views;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.zayz.socialplus.R;
import me.zayz.socialplus.adapters.MediaListAdapter;
import me.zayz.socialplus.databinding.ViewMediaListBinding;
import me.zayz.socialplus.interfaces.ActivityCallback;
import me.zayz.socialplus.models.InstagramMedia;

/**
 * Created by zayz on 11/9/17.
 * <p>
 * Media list fragment
 */
public class MediaListFragment extends Fragment {

    ViewMediaListBinding mBinding;
    String mTitle;
    ActivityCallback mCallback;
    List<InstagramMedia> mMediaList;
    Context mContext;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        this.mCallback = (ActivityCallback) context;
        this.mContext = context;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mMediaList = (List<InstagramMedia>) b.getSerializable("data");
        mTitle = b.getString("title");

        if (mMediaList == null) {
            mMediaList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = ViewMediaListBinding.inflate(getActivity().getLayoutInflater(),
                container, false);
        mCallback.setNavigation(R.id.navigation_engagement);

        setData();

        return mBinding.layout;
    }

    /**
     * Sets data in layout.
     */
    private void setData() {

        final MediaListAdapter adapter = new MediaListAdapter(
                getActivity(), mMediaList);

        ListView listView = mBinding.layout;
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InstagramMedia media = (InstagramMedia) adapter.getItem(position);

                Uri uri = Uri.parse(media.link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                intent.setPackage("com.instagram.android");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(media.link)));
                }
            }
        });
    }
}
