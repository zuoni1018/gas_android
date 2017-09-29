package com.yuncommunity.gas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baozi.Zxing.CaptureActivity;
import com.oldfeel.base.BaseBaseAdapter;
import com.oldfeel.utils.DensityUtil;
import com.oldfeel.utils.LogUtil;
import com.oldfeel.utils.ScreenUtil;
import com.pgyersdk.update.PgyUpdateManager;
import com.yuncommunity.gas.base.MyActivity;
import com.yuncommunity.gas.base.Net;
import com.yuncommunity.gas.conf.JsonApi;
import com.yuncommunity.gas.utils.MyUtils;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MyActivity {
    private static final int REQUEST_SAOMIAO = 123;
    @Bind(R.id.communicateNo)
    EditText communicateNo;
    @Bind(R.id.saomiao)
    ImageView saomiao;
    @Bind(R.id.search)
    Button search;
    @Bind(R.id.layout)
    RelativeLayout layout;
    @Bind(R.id.diviter)
    View diviter;
    private PopupWindow pop;
    private MyAdapter adapter;
    private boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showTitle("XX在线燃气缴费");
        List<String> noList = userInfo.getNoList();
        adapter = new MyAdapter(noList);

        if (noList.size() != 0) {
            String no = noList.get(0);
            communicateNo.setText(no);
            communicateNo.setSelection(no.length());
            saomiao.setImageResource(R.drawable.ic_down);
        }
        initView();
        PgyUpdateManager.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setList(userInfo.getNoList());
    }

    private void initView() {
        communicateNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    saomiao.setImageResource(R.drawable.saomiao);
                } else {
                    saomiao.setImageResource(R.drawable.ic_down);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        communicateNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.saomiao)
    public void saomiao() {
        if (communicateNo.getText().length() == 0) {
            openActivity(CaptureActivity.class, REQUEST_SAOMIAO);
            return;
        }
        if (isShow) {
            saomiao.setImageResource(R.drawable.ic_down);
            communicateNo.setBackgroundResource(R.drawable.white_bg_corners_6);
            diviter.setVisibility(View.GONE);
        } else {
            saomiao.setImageResource(R.drawable.ic_up);
            communicateNo.setBackgroundResource(R.drawable.white_bg_divider);
            diviter.setVisibility(View.VISIBLE);
        }
        if (pop == null) {
            int width = ScreenUtil.getScreenWidth(MainActivity.this) - DensityUtil.dip2px(MainActivity.this, 32);
            ListView listView = new ListView(MainActivity.this);
            listView.setBackgroundColor(Color.WHITE);
            listView.setPadding(DensityUtil.dip2px(MainActivity.this, 6), 0, DensityUtil.dip2px(MainActivity.this, 6), 0);
            listView.setAdapter(adapter);
            pop = new PopupWindow(listView, width, RelativeLayout.LayoutParams.WRAP_CONTENT);
            pop.showAsDropDown(communicateNo);
            isShow = true;
        } else {
            if (isShow) {
                pop.dismiss();
                isShow = false;
            } else {
                pop.showAsDropDown(communicateNo);
                isShow = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SAOMIAO) {
            String recode = data.getStringExtra("recode");
            if (recode != null && recode.length() > 10) {
                recode = recode.substring(recode.length() - 10, recode.length());
            }
            communicateNo.setText(recode);
            communicateNo.setSelection(recode.length());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.search)
    public void search() {
        if (communicateNo.length() != 10) {
            communicateNo.setError("请输入10位表号");
            return;
        }

        Net net1 = new Net(this, JsonApi.GETUSERLIST);
        net1.setParams("communicateNo", communicateNo);
        net1.sendRequest("正在查询...", new Net.Callback() {
            @Override
            public void success(SoapObject soapObject) {
                LogUtil.showLog("----------1----------" + soapObject);
                soapObject = MyUtils.getSoapObject(soapObject, "UsersList");
                LogUtil.showLog("----------1----------" + soapObject);
                if (soapObject == null) {
                    showToast("查询不到用户信息");
                } else {
                    Intent intent = new Intent(MainActivity.this, GasInfo.class);
                    intent.putExtra("no", communicateNo.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }


    class MyAdapter extends BaseBaseAdapter<String> {

        public MyAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public View getView(LayoutInflater inflater, ViewGroup parent, final int position) {
            View view = inflater.inflate(R.layout.list_row, null);
            ImageButton close = (ImageButton) view.findViewById(R.id.close_row);
            TextView content = (TextView) view.findViewById(R.id.text_row);
            content.setText(getItem(position));
            final String no = getItem(position);
            content.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    communicateNo.setText(no);
                    communicateNo.setSelection(no.length());
                    search();
                    pop.dismiss();
                    isShow = false;
                    saomiao.setImageResource(R.drawable.ic_down);
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userInfo.deleteNo(no);
                    remove(position);
                    if (list.size() == 0) {
                        pop.dismiss();
                        isShow = false;
                    }
                }
            });
            return view;
        }
    }

    @OnClick(R.id.duka)
    public void duka() {

        Intent intent = new Intent(MainActivity.this, ICGasInfo.class);
        intent.putExtra("no", "10001546");
        startActivity(intent);


//        if (communicateNo.length() != 10) {
//            communicateNo.setError("请输入10位表号");
//            return;
//        }
//
//        Net net1 = new Net(this, JsonApi.GETICUSERLIST);
//        net1.setParams("meterNo", communicateNo);
//        net1.sendRequest("正在查询...", new Net.Callback() {
//            @Override
//            public void success(SoapObject soapObject) {
//                soapObject = MyUtils.getSoapObject(soapObject, "UsersList");
//                if (soapObject == null) {
//                    showToast("查询不到用户信息");
//                } else {
//                    Intent intent = new Intent(MainActivity.this, ICGasInfo.class);
//                    intent.putExtra("no", communicateNo.getText().toString());
//                    startActivity(intent);
//                }
//            }
//        });
    }
}
