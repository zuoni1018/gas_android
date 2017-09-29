package com.yuncommunity.gas.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.yuncommunity.gas.R;
import com.yuncommunity.gas.ReaderResult;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by oldfeel on 17-5-10.
 */

public class NoWriteRecord extends DialogFragment {
    @Bind(R.id.close)
    ImageView close;
    @Bind(R.id.write)
    ImageButton write;
    private String id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_write_record, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.close)
    public void close() {
        dismiss();
    }

    @OnClick(R.id.write)
    public void write() {
        Intent intent = new Intent(getActivity(), ReaderResult.class);
        intent.putExtra("id", id);
        startActivity(intent);
        dismiss();
    }

    public static NoWriteRecord newInstance(String id) {
        NoWriteRecord noWriteRecord = new NoWriteRecord();
        noWriteRecord.id = id;
        return noWriteRecord;
    }
}
