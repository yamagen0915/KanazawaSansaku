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
	
	// スライドする早さ
	private static final int ANIMATION_DELAY_MILLIS = 500;
	
	private static final String WHEN_CLOSE = "WhenClose";
	private static final String WHEN_OPEN  = "WhenOpen";
	
	private Scroller scroller;
	private GestureDetector gestureDetector;
	
	private ImageView headerView;
	
	private Integer scrollH = null;
	
	// アニメーション中は開閉ができなようにするためのフラグ
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

		// 画面の１番下まで移動, -5は微調整の結果
		scroller.startScroll(0, - DisplayUtils.mesureDisplayH(context), 0, 0);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		
		//一度しか実行されないようにする
		if (headerView != null) return ;
		
		// ヘッダーのイベント登録とか
		//headerView = (ImageView) findViewById(R.id.imageRouteListHeader);
		headerView.setImageBitmap(cachedBitmapHeader.get(WHEN_CLOSE));
		headerView.setOnClickListener(onMenuClickListener);
		
		// スワイプイベントのためのタッチリスナを登録
		headerView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		// ヘッダーの大きさを取得する
		int headerH = cachedBitmapHeader.get(WHEN_CLOSE).getHeight();
		
		// -5は微調整した結果
		scrollH = DisplayUtils.mesureDisplayH(getContext()) - headerH - 5;
		scroller.startScroll(0, scroller.getCurrY(), 0, headerH);
	}
	
	/**
	 * スワイプで開閉できるようにする。
	 */
	private SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
		
		// スワイプイベントで使用する定数
	    private static final int SWIPE_MIN_DISTANCE 	  = 120;
	    private static final int SWIPE_MAX_OFF_PATH 	  = 250;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		
		@Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try {
            	// 縦の移動距離が大きすぎる場合は無視
                if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) return false;
                if (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) return false;

                // 開始位置から終了位置の移動距離が指定値より大きい
                // この変数がfalse = 閉じるためのジェスチャ ではないことに注意
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
		
		// アニメーション中もしくは既に開いているときは何もしない
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
		// アニメーション中もしくは既に閉じているときは何もしない
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
			// アニメーションが終了したらフラグを下げる
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
		// 画面の大きさ合わせて、画像をリサイズしてからキャッシュする。
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
