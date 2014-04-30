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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
		registerForContextMenu(blockNumberList);
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
				holder.tv_mode.setText("block SMS");
			}else if(mode == 1){
				holder.tv_mode.setText("block phone");
			}else{
				holder.tv_mode.setText("block SMS and phone");
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
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String numberText = numberET.getText().toString().trim();
				BlockNumber blockNumber = new BlockNumber();
				if(TextUtils.isEmpty(numberText) || (!cb_phone.isChecked() && !cb_sms.isChecked()) ){
					Toast.makeText(getApplicationContext(), "Number or mode should no be empty!", Toast.LENGTH_LONG).show();
					return;
				}else{
					boolean result = false;
					blockNumber.setBlocknumber(numberText);
					if(dao.find(numberText)){
						Toast.makeText(getApplicationContext(), "Number already in block number list!", Toast.LENGTH_LONG).show();
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
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.block_number_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = (int) info.id; 
		int menuId = item.getItemId();
		switch(menuId){
		case R.id.item_delete:
			deletBlockNumber(position);
			return true;
		case R.id.item_update:
			updateBlockNumber(position);
			return true;
		default:
				return super.onContextItemSelected(item);
		}
	}
	
	private void deletBlockNumber(int position){
		String blockNumber = numbers.get(position).getBlocknumber();
		dao.delete(blockNumber);
		numbers.remove(numbers.get(position));
		adapter.notifyDataSetChanged();
	}
	
	private void updateBlockNumber(final int position){
		AlertDialog.Builder builder = new Builder(this);
		View dialogview = LayoutInflater.from(this).inflate(R.layout.add_block_number, null);
		final EditText numberET = (EditText) dialogview
				.findViewById(R.id.add_number);
		final CheckBox cb_phone = (CheckBox) dialogview
				.findViewById(R.id.cb_block_phone);
		final CheckBox cb_sms = (CheckBox) dialogview
				.findViewById(R.id.cb_block_sms);
		final TextView titleTv = (TextView) dialogview.findViewById(R.id.title);
		titleTv.setText("Edit");
		numberET.setText(numbers.get(position).getBlocknumber());
		if(numbers.get(position).getMode() == 2){
			cb_phone.setChecked(true);
			cb_sms.setChecked(true);
		}else if(numbers.get(position).getMode() == 1){
			cb_phone.setChecked(true);
		}else{
			cb_sms.setChecked(true);
		}
		builder.setView(dialogview);
		builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String numberText = numberET.getText().toString().trim();
				if(TextUtils.isEmpty(numberText) || (!cb_phone.isChecked() && !cb_sms.isChecked()) ){
					Toast.makeText(getApplicationContext(), "Number or mode should no be empty!", Toast.LENGTH_LONG).show();
					return;
				}else{
					if(!dao.find(numberText)){
						Toast.makeText(getApplicationContext(), "The number you want to update is not exsiting", Toast.LENGTH_LONG).show();
						return;
					}else{
						if (cb_phone.isChecked() && cb_sms.isChecked()) {
							dao.update(numberText, numberText, "2");
							BlockNumber blockNumber = (BlockNumber) blockNumberList
									.getItemAtPosition(position);
							blockNumber.setMode(2);
							blockNumber.setBlocknumber(numberText);
							adapter.notifyDataSetChanged();;
						}else if(cb_phone.isChecked()){
							dao.update(numberText, numberText, "1");
							BlockNumber blockNumber = (BlockNumber) blockNumberList
									.getItemAtPosition(position);
							blockNumber.setMode(1);
							blockNumber.setBlocknumber(numberText);
							adapter.notifyDataSetChanged();;
						}else{
							dao.update(numberText, numberText, "0");
							BlockNumber blockNumber = (BlockNumber) blockNumberList
									.getItemAtPosition(position);
							blockNumber.setMode(0);
							blockNumber.setBlocknumber(numberText);
							adapter.notifyDataSetChanged();;
						}
					}
				}
				
			}
			
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}
	
}
