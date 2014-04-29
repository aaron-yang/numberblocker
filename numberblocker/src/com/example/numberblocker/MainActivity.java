package com.example.numberblocker;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.numberblocker.db.BlockNumberDao;
import com.example.numberblocker.entity.BlockNumber;

public class MainActivity extends Activity {
	private final int LOAD_DATA_FINISH = 40;
	private ListView blockNumberList;
	private LinearLayout loading;
	private BlockNumberAdapter adapter;
	private List<BlockNumber> numbers;
	private BlockNumberDao dao;
	private Button add;
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case LOAD_DATA_FINISH:
				loading.setVisibility(View.INVISIBLE);
				adapter = new BlockNumberAdapter();
				adapter.getCount();
				blockNumberList.setAdapter(adapter);
				break;
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loading = (LinearLayout) findViewById(R.id.loading);
		blockNumberList = (ListView) findViewById(R.id.blockNumberList);
		add = (Button) findViewById(R.id.add);
		loading.setVisibility(View.VISIBLE);
		dao = new BlockNumberDao(this);
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				numbers = dao.findAll();
				Message msg = Message.obtain();
				msg.what = LOAD_DATA_FINISH;
				handler.sendMessage(msg);
			}
			
		}.start();
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAddDialog();
			}
		});
	}
	
	private class BlockNumberAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return numbers.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return numbers.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(convertView == null){
				convertView = View.inflate(MainActivity.this, R.layout.number_item, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView)convertView.findViewById(R.id.number);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.mode);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			BlockNumber blockNumber = numbers.get(position);
			holder.tv_number.setText(blockNumber.getBlocknumber());
			int mode = blockNumber.getMode();
			if(mode == 0){
				holder.tv_mode.setText("电话拦截");
			}else if(mode == 1){
				holder.tv_mode.setText("短信拦截");
			}else{
				holder.tv_mode.setText("全部拦截");
			}
			return convertView;
		}
		
	}

	private static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
	}
	
	private void showAddDialog(){
		AlertDialog.Builder builder = new Builder(this);
		View dialogview = LayoutInflater.from(this).inflate(R.layout.add_block_number, null);
		final EditText numberET = (EditText) dialogview
				.findViewById(R.id.add_number);
		final CheckBox cb_phone = (CheckBox) dialogview
				.findViewById(R.id.cb_block_phone);
		final CheckBox cb_sms = (CheckBox) dialogview
				.findViewById(R.id.cb_block_sms);
		builder.setView(dialogview);
		builder.setPositiveButton("添加", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String numberText = numberET.getText().toString().trim();
				BlockNumber blockNumber = new BlockNumber();
				if(TextUtils.isEmpty(numberText) || (!cb_phone.isChecked() && !cb_sms.isChecked()) ){
					Toast.makeText(getApplicationContext(), "号码或者拦截模式不能为空", Toast.LENGTH_LONG).show();
					return;
				}else{
					boolean result = false;
					blockNumber.setBlocknumber(numberText);
					if(dao.find(numberText)){
						Toast.makeText(getApplicationContext(), "该号码已在黑名单中,无法重复添加", Toast.LENGTH_LONG).show();
						return;
					}else{
						if (cb_phone.isChecked() && cb_sms.isChecked()) {
							blockNumber.setMode(2);
							result = dao.add(numberText, "2");
						}else if(cb_phone.isChecked()){
							blockNumber.setMode(1);
							result = dao.add(numberText, "1");
						}else{
							blockNumber.setMode(0);
							result = dao.add(numberText, "0");
						}
					}
					if(result){
						numbers.add(blockNumber);
						adapter.notifyDataSetChanged();
					}
				}
				
			}
			
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

}
