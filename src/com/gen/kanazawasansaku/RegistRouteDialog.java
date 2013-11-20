package com.gen.kanazawasansaku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class RegistRouteDialog extends DialogFragment {
	
	public static final String TAG = "RegistRouteDialog";
	
	private OnRegistRouteListener onRegistRouteListener = null;
	
	private View dialogView;
	
	// DialogFragmentでgetFragmentManagerを読んでも取得できなかったので、showメソッドをOverrideして自分で保持する。
	// onCreateDialog内ならgetActivityできるっぽい
	private FragmentManager fragmentManager = null;
	
	// 明示的に引数なしのコンストラクタを定義
	public RegistRouteDialog () {}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogView = inflater.inflate(R.layout.dialog_regist_route, null);
		
		Dialog dialog = new AlertDialog.Builder(getActivity())
			.setTitle("ルートの登録")
			.setView(dialogView)
			.setCancelable(false)
			.setPositiveButton("登録", onPositiveButtonClick)
			.setNegativeButton("キャンセル", onNegativeButtonClick)
			.create();
		
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	private final OnClickListener onPositiveButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (onRegistRouteListener != null) onRegistRouteListener.onRegist(dialogView); 
		}
	};
	
	private final OnClickListener onNegativeButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			Dialog dialogInstance = new AlertDialog.Builder(getActivity())
				.setTitle("注意")
				.setCancelable(false)
				.setMessage("記録したルートの情報がなくなってしまいます。\n本当にキャンセルしてもよろしいですか？")
				.setPositiveButton("キャンセルする", null)
				.setNegativeButton("戻る", onConfirmNegativeButtonClick)
				.create();
			
			dialogInstance.setCanceledOnTouchOutside(false);
			dialogInstance.show();
			
		}
		
	};
	
	private final OnClickListener onConfirmNegativeButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// RegisRouteDialogをもう一度表示する
			show(fragmentManager, TAG);
		}
		
	};
	
	@SuppressWarnings("unused")
	private final OnClickListener onConfirmPositiveButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (onRegistRouteListener != null) onRegistRouteListener.onCancel();
		}
	};
	
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
		this.fragmentManager = manager;
	};
	
	public void setOnRegistRouteListener (OnRegistRouteListener listener) {
		this.onRegistRouteListener = listener;
	} 
	
	public interface OnRegistRouteListener {
		public void onRegist (View view);
		public void onCancel ();
	}
	
	
}
