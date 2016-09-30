package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.InviteVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.share.ShareManager;

/**
 * Created by chenxiaofeng on 16/9/30.
 */
public class InviteFriendFragment extends Fragment {

    private static final String USER_ID = "USER_ID";

    private Long userId;

    private NetworkImageView weiImageView;//二维码图片

    private TextView inviteFriendView;//立即邀请按钮

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;

    private ShareManager shareManager;

    public InviteFriendFragment() {
    }

    public static InviteFriendFragment newInstance(Long userId) {
        InviteFriendFragment fragment = new InviteFriendFragment();
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

        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        shareManager = ShareManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_invite_friend, container, false);
        dataLoadingView.initView(view, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        inviteFriendView = (TextView) view.findViewById(R.id.invite_friend_button);
        inviteFriendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareManager.show(false);
            }
        });
        weiImageView = (NetworkImageView) view.findViewById(R.id.mark_view);
        loadData();
        return view;
    }

    private void loadData() {
        dataLoadingView.startLoading();
        LoginManager.fetchInviteInfo(userId).startUI(new ApiCallback<InviteVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(InviteVO inviteVO) {
                dataLoadingView.loadSuccess();
                String title = "苏城云购";
                String content = "对发送发送到发送到发送地方";
                shareManager.setShareContent(title, content, inviteVO.url);
                weiImageView.setImageUrl(inviteVO.getQrcode(), mImageLoader);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

}
