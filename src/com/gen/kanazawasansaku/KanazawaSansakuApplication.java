package com.gen.kanazawasansaku;

import android.app.Application;

public class KanazawaSansakuApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// �f�[�^�x�[�X��������
		DbAccess.initInstance(getApplicationContext());
	}
	
}
