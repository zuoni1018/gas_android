package com.yuncommunity.gas.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.yuncommunity.gas.R;
import com.yuncommunity.gas.base.MyFragment;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.conf.JsonApi;
import com.yuncommunity.gas.utils.MyUtils;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mini on 2017/4/11.
 */

public class PayRecordingList extends MyFragment {
    @Bind(R.id.expandable_list_view)
    ExpandableListView expandableListView;
    MyAdapter adapter;

    String communicateNo;

    //    List<SoapObject> groupList = new ArrayList<>();
    List<String> groupList = new ArrayList<>();
    //    List<SoapObject> childList = new ArrayList<>();
    List<List<SoapObject>> childList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pay_recording_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MyAdapter();
        expandableListView.setAdapter(adapter);

        for (int i = 0; i < 3; i++) {
            expandableListView.expandGroup(i);
        }

        getData();
    }

    private void getData() {
        for (int i = 0; i < 3; i++) {
            getData(i);
        }
    }

    /**
     * 获取几个月前的数据
     *
     * @param before
     */
    private void getData(final int before) {
        Net net = new Net(getActivity(), JsonApi.GETPAYBYCOMMUNICATENO);
        net.setParams("communicateNo", communicateNo);
        net.setParams("startMonth", MyUtils.getMonthBefore(before));
        net.setParams("endMonth", MyUtils.getMonthBefore(before));
        net.sendRequest("", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                if (soapObject == null) {
                    return;
                }
                int count = soapObject.getPropertyCount();
                List<SoapObject> list = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    SoapObject temp = (SoapObject) soapObject.getProperty(i);
                    list.add(temp);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -before);
                if (list.size() != 0) {
                    groupList.add(calendar.get(Calendar.MONTH) + 1 + "月");
                    childList.add(list);
                }
                adapter.notifyDataSetChanged();
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    expandableListView.expandGroup(i);
                }
            }
        });
    }

    public static Fragment newInstance(String communicateNo) {
        PayRecordingList list = new PayRecordingList();
        list.communicateNo = communicateNo;
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            if (childList.size() == 0)
                return 0;
            return childList.get(i).size();
        }

        @Override
        public String getGroup(int i) {
            return groupList.get(i);
        }

        @Override
        public SoapObject getChild(int i, int i1) {
            return childList.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = View.inflate(getActivity(), R.layout.group, null);
            TextView group_text = (TextView) view.findViewById(R.id.group_text);
//            SoapObject temp = getGroup(groupPosition);
//            String payTime = MyUtils.getString(temp, "payTime");
            group_text.setText(getGroup(groupPosition));
            return view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getActivity(), R.layout.child, null);
            TextView week = (TextView) view.findViewById(R.id.week);
            TextView date = (TextView) view.findViewById(R.id.date);
            TextView money = (TextView) view.findViewById(R.id.money);
            TextView payNum = (TextView) view.findViewById(R.id.pay_num);
            TextView pay = (TextView) view.findViewById(R.id.pay);

            SoapObject temp = getChild(groupPosition, childPosition);
            String payTime = MyUtils.getString(temp, "payTime");
            week.setText(MyUtils.getWeek(payTime));
            date.setText(MyUtils.getMonthAndDay(payTime));
            money.setText("¥" + MyUtils.getString(temp, "actualAmount"));
            payNum.setText("交易流水号 : " + MyUtils.getString(temp, "payId"));
            pay.setText("50");
            pay.setVisibility(View.GONE);

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}
