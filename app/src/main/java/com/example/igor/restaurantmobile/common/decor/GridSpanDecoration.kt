package ro.lensa.common.decor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class GridSpanDecoration(private val padding: Int, private val bottomPadding: Boolean) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val gridLayoutManager = parent.layoutManager as GridLayoutManager?

        val spanCount = gridLayoutManager!!.spanCount

        val params = view.layoutParams as GridLayoutManager.LayoutParams

        val spanIndex = params.spanIndex
        val spanSize = params.spanSize

        if (spanIndex != 0) outRect.left = padding / 2
        if (spanIndex + spanSize != spanCount) outRect.right = padding / 2

        if (bottomPadding) outRect.bottom = padding

    }

}