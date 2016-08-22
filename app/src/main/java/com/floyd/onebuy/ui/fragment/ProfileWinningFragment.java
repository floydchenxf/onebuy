package com.floyd.onebuy.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.floyd.onebuy.ui.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileWinningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileWinningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileWinningFragment extends Fragment {
    private static final String USER_ID = "USER_ID";

    private Long  userId;

    private OnFragmentInteractionListener mListener;

    public ProfileWinningFragment() {
    }

    public static ProfileWinningFragment newInstance(Long userId) {
        ProfileWinningFragment fragment = new ProfileWinningFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID, 0l);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_winning, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
