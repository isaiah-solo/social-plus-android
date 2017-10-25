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
import me.zayz.socialplus.adapters.UserListAdapter;
import me.zayz.socialplus.databinding.ViewUserListBinding;
import me.zayz.socialplus.interfaces.ActivityCallback;
import me.zayz.socialplus.models.InstagramPublicUser;

/**
 * Created by zayz on 11/9/17.
 * <p>
 * User list fragment
 */
public class UserListFragment extends Fragment {

    ViewUserListBinding mBinding;
    ActivityCallback mCallback;
    List<InstagramPublicUser> mUserList;
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
        mUserList = (List<InstagramPublicUser>) b.getSerializable("data");

        if (mUserList == null) {
            mUserList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = ViewUserListBinding.inflate(getActivity().getLayoutInflater(),
                container, false);
        mCallback.setNavigation(R.id.navigation_home);

        setData();

        return mBinding.layout;
    }

    /**
     * Sets data in layout.
     */
    private void setData() {

        final UserListAdapter adapter = new UserListAdapter(
                getActivity(), mUserList);

        final ListView listView = mBinding.layout;
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InstagramPublicUser user = (InstagramPublicUser) adapter.getItem(position);

                Uri uri = Uri.parse("https://www.instagram.com/_u/" + user.username.replace(
                        "@", ""));

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://instagram.com/" + user.username.replace(
                                    "@", ""))));
                }
            }
        });
    }
}
