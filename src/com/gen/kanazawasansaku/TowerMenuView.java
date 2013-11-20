package com.gen.kanazawasansaku;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.gen.kanazawasansaku.utils.DisplayUtils;
import com.gen.kanazawasansaku.utils.Utils;

public class TowerMenuView extends LinearLayout {
	
	// �X���C�h���鑁��
	private static final int ANIMATION_DELAY_MILLIS = 500;
	
	private static final String WHEN_CLOSE = "WhenClose";
	private static final String WHEN_OPEN  = "WhenOpen";
	
	private Scroller scroller;
	private GestureDetector gestureDetector;
	
	private ImageView headerView;
	
	private Integer scrollH = null;
	
	// �A�j���[�V�������͊J���ł��Ȃ悤�ɂ��邽�߂̃t���O
	private boolean isAnimating = false;
	private boolean isClose 	= true;
	
	private HashMap<String, Bitmap> cachedBitmapHeader = new HashMap<String, Bitmap>();
	
	public TowerMenuView (Context context) {
		this(context, null);
	}

	public TowerMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);

		scroller 		= new Scroller(context);
		gestureDetector = new GestureDetector(context, onGestureListener);
		
		createCacheBitmapHeader();

		// ��ʂ̂P�ԉ��܂ňړ�, -5�͔������̌���
		scroller.startScroll(0, - DisplayUtils.mesureDisplayH(context), 0, 0);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		
		//��x�������s����Ȃ��悤�ɂ���
		if (headerView != null) return ;
		
		// �w�b�_�[�̃C�x���g�o�^�Ƃ�
		//headerView = (ImageView) findViewById(R.id.imageRouteListHeader);
		headerView.setImageBitmap(cachedBitmapHeader.get(WHEN_CLOSE));
		headerView.setOnClickListener(onMenuClickListener);
		
		// �X���C�v�C�x���g�̂��߂̃^�b�`���X�i��o�^
		headerView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		// �w�b�_�[�̑傫�����擾����
		int headerH = cachedBitmapHeader.get(WHEN_CLOSE).getHeight();
		
		// -5�͔�������������
		scrollH = DisplayUtils.mesureDisplayH(getContext()) - headerH - 5;
		scroller.startScroll(0, scroller.getCurrY(), 0, headerH);
	}
	
	/**
	 * �X���C�v�ŊJ�ł���悤�ɂ���B
	 */
	private SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
		
		// �X���C�v�C�x���g�Ŏg�p����萔
	    private static final int SWIPE_MIN_DISTANCE 	  = 120;
	    private static final int SWIPE_MAX_OFF_PATH 	  = 250;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		
		@Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try {
            	// �c�̈ړ��������傫������ꍇ�͖���
                if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) return false;
                if (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) return false;

                // �J�n�ʒu����I���ʒu�̈ړ��������w��l���傫��
                // ���̕ϐ���false = ���邽�߂̃W�F�X�`�� �ł͂Ȃ����Ƃɒ���
                boolean isOpenGesture = (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE);
                if (isOpenGesture) open();
                
                boolean isCloseGesture = (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE);
                if (isCloseGesture) close();

            } catch (Exception e) {
                // nothing
            }
            return false;
        }
	};
	
	public void open () {
		
		// �A�j���[�V�������������͊��ɊJ���Ă���Ƃ��͉������Ȃ�
		if (isAnimating || !isClose) return ;
		
		isClose     = false;
		isAnimating = true;
		headerView.setImageBitmap(cachedBitmapHeader.get(WHEN_OPEN));
		
		Utils.Log.d("openY : " + scroller.getCurrY());
		
		scroller.startScroll(
				/* startX = */ scroller.getCurrX(), 
				/* startY = */ scroller.getCurrY(),
				/* dx     = */ 0,
				/* dy     = */ scrollH,
				/* delay  = */ ANIMATION_DELAY_MILLIS);
		postInvalidate();
	}
	
	public void close () {
		// �A�j���[�V�������������͊��ɕ��Ă���Ƃ��͉������Ȃ�
		if (isAnimating || isClose) return ;
		
		isClose     = true;
		isAnimating = true;
		
		headerView.setImageBitmap(cachedBitmapHeader.get(WHEN_CLOSE));
		
		scroller.startScroll(
				/* startX = */ scroller.getCurrX(), 
				/* startY = */ scroller.getCurrY(),
				/* dx     = */ 0,
				/* dy     = */ -scrollH,
				/* delay  = */ ANIMATION_DELAY_MILLIS);
		postInvalidate();
	}
	
	@Override
	public void computeScroll () {
		
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		} else {
			// �A�j���[�V�������I��������t���O��������
			isAnimating = false;
		}
		super.computeScroll();
	}
	
	private OnClickListener onMenuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (isClose) open();
			else         close();
		}
	};
	
	private void createCacheBitmapHeader () {
		// ��ʂ̑傫�����킹�āA�摜�����T�C�Y���Ă���L���b�V������B
		Bitmap bitmapHeaderWhenClose = Utils.resizeBitmapFitWidth(
				/* displayWidth = */ DisplayUtils.mesureDisplayW(getContext()), 
				/* sorceImage   = */ BitmapFactory.decodeResource(getResources(), R.drawable.route_list_head_when_close));
		cachedBitmapHeader.put(WHEN_CLOSE, bitmapHeaderWhenClose);
		
		Bitmap bitmapHeaderWhenOpen = Utils.resizeBitmapFitWidth(
				/* displayWidth = */ DisplayUtils.mesureDisplayW(getContext()), 
				/* sorceImage   = */ BitmapFactory.decodeResource(getResources(), R.drawable.route_list_head_when_open));
		cachedBitmapHeader.put(WHEN_OPEN, bitmapHeaderWhenOpen);
	}
	
}
