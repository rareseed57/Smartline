package com.smartline.smartline;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class NonSwipeableViewPager extends ViewPager
{
    private float initialXValue;
    private SwipeDirection direction;

    public NonSwipeableViewPager(Context context)
    {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (this.IsSwipeAllowed(event))
        {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public void setOffscreenPageLimit(int limit)
    {
        super.setOffscreenPageLimit(limit);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        if (this.IsSwipeAllowed(event))
        {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    private boolean IsSwipeAllowed(MotionEvent event)
    {
        if(this.direction == SwipeDirection.all) return true;

        if(direction == SwipeDirection.none )//disable any swipe
            return false;

        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if(event.getAction()==MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                if (diffX > 0 && direction == SwipeDirection.right ) {
                    // swipe from left to right detected
                    return false;
                }else if (diffX < 0 && direction == SwipeDirection.left ) {
                    // swipe from right to left detected
                    return false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }



    public void setAllowedSwipeDirection(SwipeDirection dir) {
        direction = dir;
    }

    public void refresh()
    {
        getAdapter().notifyDataSetChanged();
    }
}


