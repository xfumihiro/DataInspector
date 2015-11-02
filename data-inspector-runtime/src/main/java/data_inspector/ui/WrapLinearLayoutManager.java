package data_inspector.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

// http://stackoverflow.com/questions/26649406/nested-recycler-view-height-doesnt-wrap-its-content/28510031#28510031
public class WrapLinearLayoutManager extends LinearLayoutManager {

  public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  private int[] measuredDimension = new int[2];

  @Override
  public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec,
      int heightSpec) {
    final int widthMode = View.MeasureSpec.getMode(widthSpec);
    final int heightMode = View.MeasureSpec.getMode(heightSpec);
    final int widthSize = View.MeasureSpec.getSize(widthSpec);
    final int heightSize = View.MeasureSpec.getSize(heightSpec);
    int width = 0;
    int height = 0;
    for (int i = 0; i < getItemCount(); i++) {
      measureScrapChild(recycler, i,
          View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), measuredDimension);

      if (getOrientation() == HORIZONTAL) {
        width = width + measuredDimension[0];
        if (i == 0) {
          height = measuredDimension[1];
        }
      } else {
        height = height + measuredDimension[1];
        if (i == 0) {
          width = measuredDimension[0];
        }
      }
    }

    // If child view is more than screen size, there is no need to make it wrap content. We can use original onMeasure() so we can scroll view.
    if (height < heightSize && width < widthSize) {

      switch (widthMode) {
        case View.MeasureSpec.EXACTLY:
          width = widthSize;
        case View.MeasureSpec.AT_MOST:
        case View.MeasureSpec.UNSPECIFIED:
      }

      switch (heightMode) {
        case View.MeasureSpec.EXACTLY:
          height = heightSize;
        case View.MeasureSpec.AT_MOST:
        case View.MeasureSpec.UNSPECIFIED:
      }

      setMeasuredDimension(width, height);
    } else {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
  }

  private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
      int heightSpec, int[] measuredDimension) {

    View view = recycler.getViewForPosition(position);

    // For adding Item Decor Insets to view
    super.measureChildWithMargins(view, 0, 0);
    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
        getPaddingLeft() + getPaddingRight() + getDecoratedLeft(view) + getDecoratedRight(view),
        p.width);
    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
        getPaddingTop() + getPaddingBottom() + getPaddingBottom() + getDecoratedBottom(view),
        p.height);
    view.measure(childWidthSpec, childHeightSpec);

    // Get decorated measurements
    measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin;
    measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin;
    recycler.recycleView(view);
  }
}
