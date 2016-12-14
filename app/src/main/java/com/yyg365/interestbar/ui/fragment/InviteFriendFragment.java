package com.yyg365.interestbar.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.InviteVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.interestbar.ui.share.ShareManager;

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
                String title = "给你推荐一款好玩又能赚钱的购物平台苏城云购，惊喜连连，静待你的加入。";
                String content = "给你推荐一款好玩又能赚钱的购物平台苏城云购，惊喜连连，静待你的加入。";
                shareManager.setShareContent(title, content, inviteVO.url);
                weiImageView.setImageUrl(inviteVO.getQrcode(), mImageLoader);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

}
