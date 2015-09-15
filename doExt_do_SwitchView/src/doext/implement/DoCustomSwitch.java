package doext.implement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import core.helper.DoUIModuleHelper;

@SuppressLint("DrawAllocation")
public class DoCustomSwitch extends View {

	public static final int SHAPE_RECT = 1;
	public static final int SHAPE_CIRCLE = 2;
	private static final int RIM_SIZE = 3;

	// open的背景颜色值 默认值：00FF00，close的背景颜色值 默认值：888888，slider的背景颜色值 默认值：FFFFFF
	private int openColor = Color.parseColor("#00FF00");
	private int closeColor = Color.parseColor("#888888");
	private int sliderBarColor = Color.WHITE;

	// 3 attributes
//	private int color_theme;
	private boolean isOpen;
	private int shape;
	// varials of drawing
	private Paint paint;
	private Rect backRect;
	private Rect frontRect;
	private int alpha;
	private int max_left;
	private int min_left;
	private int frontRect_left;
	private int frontRect_left_begin = RIM_SIZE;
	private int eventStartX;
	private int eventLastX;
	private int diffX = 0;

	private boolean isMove;

	private OnCheckedChangeListener listener;

	public interface OnCheckedChangeListener {
		public void onCheckedChanged(boolean isChecked);
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		this.listener = listener;
	}

	public DoCustomSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		listener = null;
		paint = new Paint();
	}

	public DoCustomSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DoCustomSwitch(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureDimension(280, widthMeasureSpec);
		int height = measureDimension(140, heightMeasureSpec);
		if (shape == SHAPE_CIRCLE) {
			if (width < height)
				width = height * 2;
		}
		setMeasuredDimension(width, height);
		initDrawingVal();
	}

	public void initDrawingVal() {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		backRect = new Rect(0, 0, width, height);
		min_left = RIM_SIZE;
		if (shape == SHAPE_RECT)
			max_left = width / 2;
		else
			max_left = width - (height - 2 * RIM_SIZE) - RIM_SIZE;
		if (isOpen) {
			frontRect_left = max_left;
			alpha = 255;
		} else {
			frontRect_left = RIM_SIZE;
			alpha = 0;
		}
		frontRect_left_begin = frontRect_left;
	}

	public int measureDimension(int defaultSize, int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (shape == SHAPE_RECT) {
			paint.setColor(closeColor);
			canvas.drawRect(backRect, paint);
			paint.setColor(openColor);
			paint.setAlpha(alpha);
			canvas.drawRect(backRect, paint);

			int w = getMeasuredWidth() / 2;

			int l = frontRect_left;
			if (frontRect_left > w) {
				l = w;
			} else if (frontRect_left < min_left) {
				l = min_left;
			}

			frontRect = new Rect(l, RIM_SIZE, l + w - RIM_SIZE, getMeasuredHeight() - RIM_SIZE);
			paint.setColor(sliderBarColor);
			canvas.drawRect(frontRect, paint);
		} else {
			// 画圆形
			int radius;
			radius = backRect.height() / 2 - RIM_SIZE;
			paint.setColor(closeColor);
			canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);
			paint.setColor(openColor);
			paint.setAlpha(alpha);
			canvas.drawRoundRect(new RectF(backRect), radius, radius, paint);

			int l = frontRect_left;
			int maxRight = backRect.width() - backRect.height() + RIM_SIZE;

			if (frontRect_left > maxRight) {
				l = maxRight;
			}

			if (frontRect_left < min_left) {
				l = min_left;
			}

			frontRect = new Rect(l, RIM_SIZE, l + backRect.height() - 2 * RIM_SIZE, backRect.height() - RIM_SIZE);
			paint.setColor(sliderBarColor);
			canvas.drawRoundRect(new RectF(frontRect), radius, radius, paint);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isMove) {
			return true;
		}
		int action = MotionEventCompat.getActionMasked(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			eventStartX = (int) event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			eventLastX = (int) event.getRawX();
			diffX = eventLastX - eventStartX;
			int tempX = diffX + frontRect_left_begin;
			tempX = (tempX > max_left ? max_left : tempX);
			tempX = (tempX < min_left ? min_left : tempX);
			if (tempX >= min_left && tempX <= max_left) {
				frontRect_left = tempX;
				alpha = (int) (255 * (float) tempX / (float) max_left);
				invalidateView();
			}
			break;
		case MotionEvent.ACTION_UP:
			int wholeX = (int) (event.getRawX() - eventStartX);
			frontRect_left_begin = frontRect_left;
			boolean toRight = (frontRect_left_begin > max_left / 2 ? true : false);
			if (Math.abs(wholeX) < 3) {
				toRight = !toRight;
			}
			moveToDest(toRight);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * draw again
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	@SuppressLint("HandlerLeak")
	public void moveToDest(final boolean toRight) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				listener.onCheckedChanged(msg.what == 1);
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (toRight) {
					while (frontRect_left <= max_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
						invalidateView();
						frontRect_left += 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						isMove = true;
					}
					isMove = false;
					alpha = 255;
					frontRect_left = max_left;
					isOpen = true;
					if (listener != null)
						handler.sendEmptyMessage(1);
					frontRect_left_begin = max_left;

				} else {
					while (frontRect_left >= min_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
						invalidateView();
						frontRect_left -= 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						isMove = true;
					}
					isMove = false;
					alpha = 0;
					frontRect_left = min_left;
					isOpen = false;
					if (listener != null)
						handler.sendEmptyMessage(0);
					frontRect_left_begin = min_left;

				}
			}
		}).start();

	}

	public void setChecked(boolean isOpen) {
		this.isOpen = isOpen;
		initDrawingVal();
		invalidateView();
		if (listener != null) {
			listener.onCheckedChanged(isOpen);
		}
	}

	public void setShapeType(int shapeType) {
		this.shape = shapeType;
	}

	public void setColors(String colors) {
		String[] colorStr = colors.split(",");
		switch (colorStr.length) {
		case 1:
			this.openColor = DoUIModuleHelper.getColorFromString(colorStr[0], this.openColor);
			break;
		case 2:
			this.openColor = DoUIModuleHelper.getColorFromString(colorStr[0], this.openColor);
			this.closeColor = DoUIModuleHelper.getColorFromString(colorStr[1], this.closeColor);
			break;
		case 3:
			this.openColor = DoUIModuleHelper.getColorFromString(colorStr[0], this.openColor);
			this.closeColor = DoUIModuleHelper.getColorFromString(colorStr[1], this.closeColor);
			this.sliderBarColor = DoUIModuleHelper.getColorFromString(colorStr[2], this.sliderBarColor);
			break;
		}
	}
}
