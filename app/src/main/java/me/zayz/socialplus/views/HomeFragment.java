package me.zayz.socialplus.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.Serializable;
import java.util.List;

import me.zayz.socialplus.R;
import me.zayz.socialplus.databinding.ViewHomeBinding;
import me.zayz.socialplus.instagram.Instagram;
import me.zayz.socialplus.interfaces.ActivityCallback;
import me.zayz.socialplus.models.InstagramPublicUser;
import me.zayz.socialplus.models.InstagramStats;

/**
 * Created by zayz on 10/25/17.
 * <p>
 * Fragment view for the home screen
 */
public class HomeFragment extends Fragment {

    ViewHomeBinding mBinding;
    ActivityCallback mCallback;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        this.mCallback = (ActivityCallback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = ViewHomeBinding.inflate(this.getActivity().getLayoutInflater(),
                container, false);
        mCallback.setNavigation(R.id.navigation_home);

        setData();

        return mBinding.layout;
    }

    /**
     * Sets data in layout.
     */
    private void setData() {

        Instagram instagram = mCallback.getInstagram();

        mBinding.setUser(instagram.user);

        mCallback.getInstagram().getImage(instagram.user.profile.profilePicture,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        mBinding.profilePicture.setImageBitmap(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        mBinding.unfollowers.itemTitle.setText(R.string.item_followings_unfollowers);
        mBinding.followers.itemTitle.setText(R.string.item_followings_followers);
        mBinding.notFollowingMe.itemTitle.setText(R.string.item_followings_not_following_me);
        mBinding.imNotFollowingBack.itemTitle.setText(R.string.item_followings_im_not_following_back);
        mBinding.mutual.itemTitle.setText(R.string.item_followings_mutual);
        //mBinding.blocked.itemTitle.setText(R.string.item_followings_blocked);

        final InstagramStats stats = mCallback.getInstagram().stats;

        mBinding.unfollowers.itemNewValue.setText(
                String.valueOf(stats.newUnfollowers.size()));
        mBinding.unfollowers.itemTotalValue.setText(
                String.valueOf(stats.unfollowers.size()));
        mBinding.followers.itemNewValue.setText(
                String.valueOf(stats.newFollowers.size()));
        mBinding.followers.itemTotalValue.setText(
                String.valueOf(stats.followers.size()));
        mBinding.notFollowingMe.itemTotalValue.setText(
                String.valueOf(stats.idols.size()));
        mBinding.imNotFollowingBack.itemTotalValue.setText(
                String.valueOf(stats.fans.size()));
        mBinding.mutual.itemTotalValue.setText(
                String.valueOf(stats.mutual.size()));
        /*
        mBinding.blocked.itemTotalValue.setText(
                String.valueOf(stats.blocked.size()));
        */

        if (stats.newUnfollowers.size() > 0) {
            mBinding.unfollowers.itemNew.setTextColor(getActivity().getResources()
                    .getColor(R.color.colorNegative));
            mBinding.unfollowers.itemNewValue.setTextColor(getActivity().getResources()
                    .getColor(R.color.colorNegative));
        }

        if (stats.newFollowers.size() > 0) {
            mBinding.followers.itemNew.setTextColor(getActivity().getResources()
                    .getColor(R.color.colorPositive));
            mBinding.followers.itemNewValue.setTextColor(getActivity().getResources()
                    .getColor(R.color.colorPositive));
        }

        mBinding.unfollowers.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setChildFragment(mBinding.layout.getId(), new UserListFragment(),
                        stats.unfollowers);
            }
        });

        mBinding.followers.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setChildFragment(mBinding.layout.getId(), new UserListFragment(),
                        stats.followers);
            }
        });

        mBinding.notFollowingMe.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setChildFragment(mBinding.layout.getId(), new UserListFragment(),
                        stats.idols);
            }
        });

        mBinding.imNotFollowingBack.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setChildFragment(mBinding.layout.getId(), new UserListFragment(),
                        stats.fans);
            }
        });

        mBinding.mutual.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setChildFragment(mBinding.layout.getId(), new UserListFragment(),
                        stats.mutual);
            }
        });

        /*
        mBinding.blocked.layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setChildFragment(mBinding.content.getId(), new UserListFragment(),
                        stats.blocked);
            }
        });
        */
    }

    /**
     * Sets child fragment given a resource id and fragment
     *
     * @param containerId Resource id of container
     * @param fragment    Fragment to set
     */
    protected void setChildFragment(int containerId, Fragment fragment,
                                    List<InstagramPublicUser> data) {

        Bundle b = new Bundle();
        b.putSerializable("data", (Serializable) data);
        fragment.setArguments(b);

        FragmentManager fragmentManager = this.getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(containerId, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}