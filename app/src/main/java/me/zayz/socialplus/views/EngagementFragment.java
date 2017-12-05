package me.zayz.socialplus.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;

import me.zayz.socialplus.R;
import me.zayz.socialplus.adapters.EngagementListAdapter;
import me.zayz.socialplus.databinding.ViewEngagementBinding;
import me.zayz.socialplus.interfaces.ActivityCallback;

import static me.zayz.socialplus.adapters.EngagementListAdapter.TITLE_IDS;

/**
 * Created by zayz on 10/25/17.
 * <p>
 * Fragment view for the engagement screen
 */
public class EngagementFragment extends Fragment {

    ViewEngagementBinding mBinding;
    ActivityCallback mCallback;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        this.mCallback = (ActivityCallback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = ViewEngagementBinding.inflate(this.getActivity().getLayoutInflater(),
                container, false);
        mCallback.setNavigation(R.id.navigation_engagement);

        setData();

        return mBinding.layout;
    }

    /**
     * Sets data in layout.
     */
    private void setData() {

        ListView listView = mBinding.list;

        final EngagementListAdapter adapter = new EngagementListAdapter(getActivity(),
                mCallback.getInstagram().user.engagement);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int item = TITLE_IDS[position];

                switch (item) {
                    default:
                    case R.string.item_engagement_best_photo:
                        setChildFragment(mBinding.layout.getId(),
                                new MediaListFragment(), "best_photo");
                        break;
                    case R.string.item_engagement_best_video:
                        setChildFragment(mBinding.layout.getId(),
                                new MediaListFragment(), "best_video");
                        break;
                    case R.string.item_engagement_least_comments:
                        setChildFragment(mBinding.layout.getId(),
                                new MediaListFragment(), "least_comments");
                        break;
                }
            }
        });
    }

    /**
     * Sets child fragment given a resource id and fragment
     *
     * @param containerId Resource id of container
     * @param fragment    Fragment to set
     */
    protected void setChildFragment(int containerId, Fragment fragment,
                                    String type) {

        Bundle b = new Bundle();

        switch (type) {
            case "best_photo":
                b.putString("title", "Best Photos");
                b.putSerializable("data",
                        (Serializable) mCallback.getInstagram().user.engagement.bestPhoto);
                break;
            case "best_video":
                b.putString("title", "Best Videos");
                b.putSerializable("data",
                        (Serializable) mCallback.getInstagram().user.engagement.bestVideo);
                break;
            case "least_comments":
                b.putString("title", "Least Commented Posts");
                b.putSerializable("data",
                        (Serializable) mCallback.getInstagram().user.engagement.leastComments);
                break;
        }

        fragment.setArguments(b);

        FragmentManager fragmentManager = this.getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(containerId, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
