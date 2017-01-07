package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yyg365.interestbar.biz.vo.json.PawnLevelVO;
import com.yyg365.interestbar.ui.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 2017/1/7.
 */
public class PawnLevelAdapter extends BaseDataAdapter<PawnLevelVO> {


    private Map<Long, Boolean> states = new HashMap<Long, Boolean>();

    private Long pawnLevelId;

    private CheckedListener mCheckedListener;

    public PawnLevelAdapter(Context context, List<PawnLevelVO> records, CheckedListener checkedListener) {
        super(context, records);
        this.mCheckedListener = checkedListener;
    }

    public void setDefaultChecked(Long pawnId) {
        pawnLevelId = pawnId;
        states.put(pawnId, true);
    }

    public Long getCheckedId() {
        return this.pawnLevelId;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.pawn_level_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.pawn_level_desc, R.id.pawn_level_choose, R.id.pawn_level_layout};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final PawnLevelVO pawnLevelVO) {

        TextView leveDescView = (TextView) holder.get(R.id.pawn_level_desc);
        final RadioButton levelChoose = (RadioButton) holder.get(R.id.pawn_level_choose);
        final Long pawnId = pawnLevelVO.PawnLevelID;
        leveDescView.setText(pawnLevelVO.PawnLevelName);

        levelChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Long key : states.keySet()) {
                    states.put(key, false);
                }

                pawnLevelId = pawnId;
                states.put(pawnId, levelChoose.isChecked());
                if (mCheckedListener != null) {
                    mCheckedListener.onChecked(pawnLevelVO);
                }
                PawnLevelAdapter.this.notifyDataSetChanged();
            }
        });

        boolean res = false;
        if (states.get(pawnId) == null
                || !states.get(pawnId)) {
            res = false;
            states.put(pawnId, false);
        } else {
            res = true;
        }

        levelChoose.setChecked(res);
    }


    public interface CheckedListener {
        void onChecked(PawnLevelVO pawnLevelVO);
    }
}
