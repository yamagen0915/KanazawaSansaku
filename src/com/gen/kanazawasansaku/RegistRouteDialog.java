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
	
	// DialogFragment��getFragmentManager��ǂ�ł��擾�ł��Ȃ������̂ŁAshow���\�b�h��Override���Ď����ŕێ�����B
	// onCreateDialog���Ȃ�getActivity�ł�����ۂ�
	private FragmentManager fragmentManager = null;
	
	// �����I�Ɉ����Ȃ��̃R���X�g���N�^���`
	public RegistRouteDialog () {}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogView = inflater.inflate(R.layout.dialog_regist_route, null);
		
		Dialog dialog = new AlertDialog.Builder(getActivity())
			.setTitle("���[�g�̓o�^")
			.setView(dialogView)
			.setCancelable(false)
			.setPositiveButton("�o�^", onPositiveButtonClick)
			.setNegativeButton("�L�����Z��", onNegativeButtonClick)
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
				.setTitle("����")
				.setCancelable(false)
				.setMessage("�L�^�������[�g�̏�񂪂Ȃ��Ȃ��Ă��܂��܂��B\n�{���ɃL�����Z�����Ă���낵���ł����H")
				.setPositiveButton("�L�����Z������", null)
				.setNegativeButton("�߂�", onConfirmNegativeButtonClick)
				.create();
			
			dialogInstance.setCanceledOnTouchOutside(false);
			dialogInstance.show();
			
		}
		
	};
	
	private final OnClickListener onConfirmNegativeButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// RegisRouteDialog��������x�\������
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
