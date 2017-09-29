package com.yuncommunity.gas.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.oldfeel.base.BaseBaseAdapter;
import com.yuncommunity.gas.ICGasInfo;
import com.yuncommunity.gas.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by oldfeel on 17-4-18.
 */

public class BTDiscovery extends DialogFragment {
    @Bind(R.id.list_view)
    ListView listView;
    MyAdapter adapter;
    @Bind(R.id.test)
    Button test;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bt_discovery, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 连接蓝牙设备,并读取表号
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void add(String text) {
        adapter.add(text);
    }

    class MyAdapter extends BaseBaseAdapter<String> {

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent, int position) {
            View view = inflater.inflate(R.layout.single_text_light, parent, false);
            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(getItem(position));
            return view;
        }
    }

    @OnClick(R.id.test)
    public void test() {
        ((ICGasInfo) getActivity()).showPay();
        dismiss();
    }
}
