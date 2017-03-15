package g.s.l.gslcustomview.ui.item_animatable_layout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import g.s.l.gslcustomview.R;
import g.s.l.utils.DisplayUtil;
import g.s.l.utils.LogUtils;
import g.s.l.utils.ReflectUtil;
import g.s.l.utils.Strings;

/**
 * Created by Deemo on 16/9/8.
 * (～ o ～)~zZ
 */
public class ItemAnimatableLayout extends ViewGroup {


    static final int TOUCH_MODE_REST = -1;
    static final int TOUCH_MODE_DOWN = 0;
    static final int TOUCH_MODE_TAP = 1;
    static final int TOUCH_MODE_DONE_WAITING = 2;

    private final String SET = "set";
    private final String GET = "get";

    public static final int ITEM_GRAVITY_DEFAULT = -1;
    public static final int ITEM_GRAVITY_CENTER = 0;
    public static final int ITEM_GRAVITY_CENTER_VERTICAL = 1;
    public static final int ITEM_GRAVITY_CENTER_HORIZONTAL = 2;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_CHAOS = 2;

    private ItemAnimatableAdapter mAdapter;
    private int mItemCount, mOldItemCount;

    private SparseArray<ItemAnimator> mSa = new SparseArray<>();

    private int mColumns, mLines;
    private int mHSpacing, mVSpacing;

    private int mChildMaxWidth, mChildMaxHeight;

    private int mItemGravity;
    private boolean mItemAlignParentRight, mItemAlignParentBottom;

    private int mAnimationState = STATE_CHAOS;
    private AnimatorSet mAnimatorSet;

    private boolean isDetachingFromWindow;

    private boolean mDataChanged;

    private CheckForTap mCheckForTap;
    private CheckForLongPress mCheckForLongPress;
    private int mTouchMode = TOUCH_MODE_REST;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnChildClickListener mOnChildClickListener;

    private AnimatedDataSetObserver mAdapterObserver;


    public ItemAnimatableLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ItemAnimatableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ItemAnimatableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemAnimatableLayout, defStyleAttr, 0);
        mColumns = ta.getInt(R.styleable.ItemAnimatableLayout_columns, 2);
        mLines = ta.getInt(R.styleable.ItemAnimatableLayout_lines, 2);
        mHSpacing = (int) ta.getDimension(R.styleable.ItemAnimatableLayout_hSpacing, 8);
        mVSpacing = (int) ta.getDimension(R.styleable.ItemAnimatableLayout_vSpacing, 8);
        mItemGravity = ta.getInt(R.styleable.ItemAnimatableLayout_item_gravity, ITEM_GRAVITY_DEFAULT);
        mItemAlignParentRight = ta.getBoolean(R.styleable.ItemAnimatableLayout_item_align_parent_right, false);
        mItemAlignParentBottom = ta.getBoolean(R.styleable.ItemAnimatableLayout_item_align_parent_bottom, false);
        ta.recycle();
    }

    int tempPosition;

    public void setAdapter(ItemAnimatableAdapter adapter, int position) {
        mAdapter = adapter;
        tempPosition = position;
        inflateChild();
        requestLayout();
        invalidate();
    }

    public ItemAnimatableAdapter getAdapter() {
        return mAdapter;
    }

    public void setOnItemClickListener(OnItemClickListener lis) {
        mOnItemClickListener = lis;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener lis) {
        mOnItemLongClickListener = lis;
    }

    public int getState() {
        return mAnimationState;
    }

    public void setState(int state) {
        if (state < 1 || state > 2) {
            return;
        }
        mAnimationState = state;
    }

    private void inflateChild() {
        int count;
        if (mAdapter == null || (count = mAdapter.getCount()) == 0)
            return;
        for (int i = 0; i < count; i++) {
            View child = obtainView(i);
            if (child == null) {
                throw new RuntimeException("child is null");
            }
            child.setVisibility(View.VISIBLE);
            if (mOnChildClickListener == null) {
                mOnChildClickListener = new OnChildClickListener();
            }
            child.setLayerType(LAYER_TYPE_HARDWARE, null);
            child.setTag(R.id.tag_position, i);
            child.setOnClickListener(mOnChildClickListener);
            setViewState(child, i);
            equipItemAnimator(child, i);
        }
        int childCount = getChildCount();
        mOldItemCount = mItemCount;
        mItemCount = mAdapter.getCount();
        if (mItemCount < childCount) {
            for (int i = mItemCount; i < childCount; i++) {
                getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    private ItemAnimator equipItemAnimator(View v, int i) {
        ItemAnimator ia = mSa.get(i);
        if (ia == null) {
            ia = new ItemAnimator(i);
            mSa.put(i, ia);
        }
        ViewAnimationData data = mAdapter.getViewAnimationData(i);
        if (ia.target != v || ia.position != i || data != ia.animationData)
            ia.equip(mAnimationState);
        return ia;
    }

    private void setViewState(View v, int position) {

        if (mAnimationState == STATE_NORMAL || position < 0 || position >= mAdapter.getCount())
            return;
        resetOrSetViewState(v, position, false);
    }

    private void resetOrSetViewState(View v, int position, boolean reset) {
        ViewAnimationData data = mAdapter.getViewAnimationData(position);
        if (data == null || data.getCount() == 0)
            return;
        LogUtils.e(this, "resetOrSetViewState position is " + position);
        Set<String> keys = data.getkeySet();
        for (String key : keys) {
            Method method = ReflectUtil.getMethod(v.getClass(), getMethodName(SET, key), float.class);
            try {
                method.invoke(v, reset ? 0 : data.get(key));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void getViewState(View child, ViewAnimationData data, ViewAnimationData canceled) {
        if (data == null || data.getCount() == 0)
            return;
        Set<String> keys = data.getkeySet();
        for (String key : keys) {
            Method method = ReflectUtil.getMethod(child.getClass(), getMethodName(GET, key));
            try {
                float f = (float) method.invoke(child);
                canceled.add(key, f);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //获取属性动画set或get方法
    private String getMethodName(String profix, String propName) {
        return profix + Strings.firstCharToUpperCase(propName);
    }

    /**
     * 获取当前位置view，如果view已经存在则回收利用
     *
     * @param position view位置
     * @return 返回新建或重复利用的view
     */
    private View obtainView(int position) {
        View child = null;
        int childCount = getChildCount();
        if (childCount == 0 || position >= childCount) {
            child = mAdapter.getView(position, null, this);
            addView(child, position);
        } else {
            child = mAdapter.getView(position, getChildAt(position), this);
            if (mAnimationState == STATE_NORMAL)
                resetView(child, position);
        }
        return child;
    }

    private void resetView(View view, int position) {
        resetOrSetViewState(view, position, true);
    }

    /**
     * 开关动画
     *
     * @param state 开关动画状态
     */
    public void trigger(int state) {
        if (mAnimationState == state || mSa.size() == 0)
            return;
        mAnimationState = state;
        int count = getChildCount();
        if (mSa.size() != count) {
            LogUtils.d(this, "animator data unSync");
            return;
        }
        startAnimatorSet();
    }

    /**
     * 初始化animatorSet，
     */
    private void startAnimatorSet() {
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.addListener(mAnimatorSetListener);
            for (int i = 0; i < mItemCount; i++) {
                ItemAnimator ia = mSa.get(i);
                mAnimatorSet.playTogether(ia.resetAnimators());
            }
            long DEFAULT_ANIMATION_DURATION = 500;
            mAnimatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
        } else {
            boolean canceled;
            if (canceled = mAnimatorSet.isRunning()) {
                mAnimatorSet.cancel();
            }
            for (int i = 0; i < mItemCount; i++) {
                ItemAnimator ia = mSa.get(i);
                if (canceled)
                    ia.cancel();
                ia.resetAnimators();
            }
        }
        mAnimatorSet.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtils.e(this, "-----------------onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();

        // FIXME: 16/9/10 如果父布局想让你多大就多大，那么得自己提供一个合理的大小，比如说通过测量子view计算当前布局的大小
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = (int) DisplayUtil.getScreenSize()[0];
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            height = (int) DisplayUtil.getScreenSize()[1];
        }

        if (count > 0) {
            mChildMaxWidth = (int) ((width - getPaddingLeft() - getPaddingRight() - mHSpacing * (mColumns - 1)) / mColumns * 1.0f);
            mChildMaxHeight = (int) ((height - getPaddingBottom() - getPaddingTop() - mVSpacing * (mLines - 1)) / mLines * 1.0f);
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                LayoutParams layoutParams = child.getLayoutParams();
                int childWSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(mChildMaxWidth, MeasureSpec.EXACTLY), 0, layoutParams.width);
                int childHSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(mChildMaxHeight, MeasureSpec.EXACTLY), 0, layoutParams.height);
                child.measure(childWSpec, childHSpec);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtils.e(this, "--------------onLayout");
        final int childCount = getChildCount();
        int index = 0, hOffset = l + getPaddingLeft(), vOffset = getPaddingTop();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                layoutChild(child, hOffset, vOffset);
                hOffset = (index + 1) % mColumns == 0 ? l + getPaddingLeft() : hOffset + mChildMaxWidth + mHSpacing;
                vOffset = (index + 1) % mColumns == 0 ? vOffset + mChildMaxHeight + mVSpacing : vOffset;
                index++;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * @param child   当前需要布局的子view
     * @param hOffset 当前子view在水平位置上的基础偏移量
     * @param vOffset 当前子view在竖直位置上的基础偏移量
     */
    private void layoutChild(View child, int hOffset, int vOffset) {
        int cw = child.getMeasuredWidth();
        int ch = child.getMeasuredHeight();

        if (!(cw >= mChildMaxWidth)) {
            if (mItemGravity == ITEM_GRAVITY_CENTER || mItemGravity == ITEM_GRAVITY_CENTER_HORIZONTAL) {
                hOffset = (int) (hOffset + (mChildMaxWidth - cw) / 2.0f);
            } else if (mItemAlignParentRight) {
                hOffset += mChildMaxWidth - cw;
            }
        }

        if (!(ch > +mChildMaxHeight)) {
            if (mItemGravity == ITEM_GRAVITY_CENTER || mItemGravity == ITEM_GRAVITY_CENTER_VERTICAL) {
                vOffset = (int) (vOffset + (mChildMaxHeight - ch) / 2.0f);
            } else if (mItemAlignParentBottom) {
                vOffset += mChildMaxHeight - ch;
            }
        }

        child.layout(hOffset, vOffset, hOffset + cw, vOffset + ch);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // FIXME: 16/9/12 由于无法解决子view在animator之后的事件响应问题，所以这里暂时先这么写了
        if (true)
            return super.onTouchEvent(event);

        if (!isEnabled()) {// copy from AbsListView
            return isClickable() || isLongClickable();
        }
        if (isDetachingFromWindow) {
            return false;
        }
        final int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                onTouchUp();
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel();
                break;
        }
        return true;
    }

    private int mMotionPosition;

    private void onTouchDown(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        mMotionPosition = pointToPosition(x, y);
        if (mMotionPosition >= 0 && getAdapter().isEnabled(mMotionPosition)) {
            mTouchMode = TOUCH_MODE_DOWN;
            View child = getChildAt(mMotionPosition);
            if (child.getVisibility() == VISIBLE) {
                if (!mDataChanged) {
                    child.setPressed(true);
                    if (mCheckForTap == null) {
                        mCheckForTap = new CheckForTap();
                    }
                    postDelayed(mCheckForTap, ViewConfiguration.getTapTimeout());
                }
            }
        } else {
            removeCallbacks(mCheckForTap);
        }
    }

    private PerformClick mPerformClick;
    private Runnable mTouchModeRest;

    private void onTouchUp() {
        switch (mTouchMode) {
            case TOUCH_MODE_DOWN:
            case TOUCH_MODE_TAP:
            case TOUCH_MODE_DONE_WAITING:
                final int motionPosition = mMotionPosition;
                final View child = getChildAt(motionPosition);
                if (child != null) {
                    if (mTouchMode == TOUCH_MODE_DOWN)
                        child.setPressed(false);
                    if (!child.hasFocusable()) {
                        if (mPerformClick == null) {
                            mPerformClick = new PerformClick();
                        }
                        if (mTouchMode == TOUCH_MODE_DOWN || mTouchMode == TOUCH_MODE_TAP) {
                            removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mCheckForTap : mCheckForLongPress);
                            if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
                                mTouchMode = TOUCH_MODE_TAP;
                                if (mTouchModeRest != null)
                                    removeCallbacks(mTouchModeRest);
                                mTouchModeRest = () -> {
                                    mTouchModeRest = null;
                                    mTouchMode = TOUCH_MODE_REST;
                                    child.setPressed(false);
                                    if (!mDataChanged && !isDetachingFromWindow) {
                                        mPerformClick.run();
                                    }
                                };
                                postDelayed(mTouchModeRest, ViewConfiguration.getPressedStateDuration());
                            } else {
                                mTouchMode = TOUCH_MODE_REST;
                            }
                        } else if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
                            mPerformClick.run();
                        }
                    }
                }
                mTouchMode = TOUCH_MODE_REST;
                break;
        }
    }

    private void onTouchCancel() {
        mTouchMode = TOUCH_MODE_REST;
        final View child = getChildAt(mMotionPosition);
        if (child != null) {
            child.setPressed(false);
            removeCallbacks(mCheckForLongPress);
            removeCallbacks(mCheckForTap);
        }
    }

    private Rect mTouchFrame;

    private int pointToPosition(int x, int y) {

        if (mTouchFrame == null) {
            mTouchFrame = new Rect();
        }
        Rect frame = mTouchFrame;
        Matrix matrix = new Matrix();
        float[] point = new float[2];
        point[0] = x;
        point[1] = y;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            frame.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            if (child.getVisibility() == VISIBLE) {
                // FIXME: 16/9/12 这种计算方式算出了坐标不对，查看viewGroup的isTransformedTouchPointInView()方法无果
                child.getMatrix().invert(matrix);
                float[] temp = java.util.Arrays.copyOf(point, 2);
                matrix.mapPoints(temp);
                if (frame.contains((int) temp[0], (int) temp[1])) {
                    return i;
                }
            }
        }
        return -1;
    }

    boolean performLongPress(final View child, final int longPressed, final long longPressId) {
        boolean handled = false;
        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(this, child, longPressed, longPressId);
        }
        return handled;
    }

    boolean performItemClick(View child, int position, long itemId) {
        if (mOnItemClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            mOnItemClickListener.onItemClick(this, child, position, itemId);
            if (child != null) {
                child.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        LogUtils.e(this, "onAttachedToWindow position " + tempPosition);
        if (mAdapter != null && mAdapterObserver == null) {
            mAdapterObserver = new AnimatedDataSetObserver();
            mAdapter.registerDataSetObserver(mAdapterObserver);
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();
            mDataChanged = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isDetachingFromWindow = true;
//        LogUtils.e(this, "onDetachedFromWindow position " + tempPosition);
        // TODO: 16/9/12 回收资源 adapter的notifyDataSetChanged方法会在onAttachedToWindow之前执行，所以这里需要注释掉，暂时没有找到合适的方式处理此问题
//        if (mAdapter != null && mAdapterObserver != null) {
//            mAdapter.unregisterDataSetObserver(mAdapterObserver);
//            mAdapterObserver = null;
//        }

        if (mPerformClick != null) {
            removeCallbacks(mPerformClick);
        }

        if (mTouchModeRest != null) {
            removeCallbacks(mTouchModeRest);
            mTouchModeRest.run();
        }

        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }

        isDetachingFromWindow = false;
    }

    private class AnimatedDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            LogUtils.e(this, "onChanged");
            mDataChanged = true;
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();


            if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
                mAnimatorSet.cancel();
                mAnimatorSet = null;
            }
            inflateChild();
            requestLayout();
        }
    }


    // 事件监听器
    private class OnChildClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (view == null)
                return;
            Object tag = view.getTag(R.id.tag_position);
            if (tag == null)
                return;
            int position = (int) tag;
            if (mAdapter != null && mAdapter.isEnabled(position) && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(ItemAnimatableLayout.this, view, position, mAdapter.getItemId(position));
            }
        }
    }

    private Animator.AnimatorListener mAnimatorSetListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                child.setLayerType(LAYER_TYPE_HARDWARE, null);
//            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                child.setLayerType(LAYER_TYPE_NONE, null);
//            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                child.setLayerType(LAYER_TYPE_NONE, null);
//            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    // 执行点击事件
    private class PerformClick implements Runnable {

        @Override
        public void run() {
            if (mDataChanged)
                return;
            final ItemAnimatableAdapter adapter = mAdapter;
            final int motionPosition = mMotionPosition;
            final int count = adapter.getCount();
            if (count > 0 && motionPosition >= 0 && motionPosition < count) {
                final View view = getChildAt(motionPosition);
                if (view != null) {
                    performItemClick(view, motionPosition, adapter.getItemId(motionPosition));
                }
            }
        }
    }

    /**
     * 判断点击事件超时的Runnable
     */
    private final class CheckForTap implements Runnable {

        @Override
        public void run() {
            if (mTouchMode == TOUCH_MODE_DOWN) {
                mTouchMode = TOUCH_MODE_TAP;
                final View child = getChildAt(mMotionPosition);
                if (child != null && !child.hasFocusable()) {
                    if (!mDataChanged) {
                        child.setPressed(true);
                        if (isLongClickable()) {
                            final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                            if (mCheckForLongPress != null) {
                                mCheckForLongPress = new CheckForLongPress();
                                postDelayed(mCheckForLongPress, longPressTimeout);
                            }
                        } else {
                            mTouchMode = TOUCH_MODE_DONE_WAITING;
                        }
                    } else {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                }
            }
        }
    }

    private final class CheckForLongPress implements Runnable {

        @Override
        public void run() {
            final int motionPosition = mMotionPosition;
            final View child = getChildAt(motionPosition);
            if (child != null) {
                final long longPressId = getAdapter().getItemId(motionPosition);
                boolean handled = false;
                if (!mDataChanged) {
                    handled = performLongPress(child, motionPosition, longPressId);
                }
                if (handled) {
                    mTouchMode = TOUCH_MODE_REST;
                    setPressed(false);
                    child.setPressed(false);
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
            }
        }
    }

    /**
     * 属性动画数据包装类
     */
    public static class ViewAnimationData {

        private HashMap<String, Float> props;

        public ViewAnimationData() {
            props = new HashMap<>();
        }

        public ViewAnimationData add(String prop, float values) {
            props.put(prop, values);
            return this;
        }

        public float get(String prop) {
            return !props.containsKey(prop) ? 0 : props.get(prop);
        }

        public int getCount() {
            return props.size();
        }

        public Set<String> getkeySet() {
            return props.keySet();
        }
    }

    /**
     * item动画封装
     */
    private class ItemAnimator {

        private int position;
        private View target;
        private ViewAnimationData animationData, canceledAnimationData = new ViewAnimationData();
        private HashMap<String, Animator> animators;
        private int mState;
        private boolean isLastAnimatorCanceled;

        public ItemAnimator(int position) {
            this.position = position;
            animators = new HashMap<>();
        }

        public ItemAnimator equip(int state) {
            target = getChildAt(position);
            animationData = mAdapter.getViewAnimationData(position);
            mState = state;
            makeAnimators();
            return this;
        }

        private void makeAnimators() {
            Set<String> keys = animationData.getkeySet();
            for (String key : keys) {
                makeAnimatorInner(key, animationData.get(key));
            }
        }

        private void makeAnimatorInner(String key, float value) {
            ObjectAnimator animator = (ObjectAnimator) animators.get(key);
            if (animator == null) {
                animator = new ObjectAnimator();
            }
            animator.setTarget(target);
            animator.setPropertyName(key);
            float start = mState == STATE_NORMAL ? 0 : value;
            float end = mState == STATE_NORMAL ? value : 0;
            animator.setFloatValues(start, end);
            animators.put(key, animator);
        }

        public Collection<Animator> resetAnimators() {
            if (mAnimationState != mState) {
                mState = mAnimationState;
                Set<String> keys = animators.keySet();
                for (String key : keys) {
                    ObjectAnimator animator = (ObjectAnimator) animators.get(key);
                    float valueData = animationData.get(key);
                    float cancelData = canceledAnimationData.get(key);
                    //如果是从normal变chaos则start为0或者上次cancel的值
                    //如果是从chaos变normal则start为真实终点或上次cancel的值
                    float start, end;
                    if (mState == STATE_NORMAL) {
                        start = isLastAnimatorCanceled ? cancelData : valueData;
                        end = 0;
                    } else {
                        start = isLastAnimatorCanceled ? cancelData : 0;
                        end = valueData;
                    }
                    animator.setFloatValues(start, end);
                }
            }
            isLastAnimatorCanceled = false;

            return animators.values();
        }

        /**
         * 如果取消掉了动画，则保存当前属性的值
         */
        public void cancel() {
            isLastAnimatorCanceled = true;
            getViewState(target, animationData, canceledAnimationData);
        }
    }

    // 事件回调接口
    public interface OnItemClickListener {
        void onItemClick(View parent, View v, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View parent, View v, int position, long id);
    }
}
