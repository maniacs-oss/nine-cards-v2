package com.fortysevendeg.ninecardslauncher.app.ui.components.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.support.v7.widget.{GridLayoutManager, RecyclerView}
import android.util.{Log, AttributeSet}
import android.view.ViewGroup.LayoutParams
import android.view.animation.GridLayoutAnimationController.AnimationParameters
import android.view.{MotionEvent, View}
import com.fortysevendeg.ninecardslauncher.commons._
import macroid.ContextWrapper

class CollectionRecyclerView(context: Context, attr: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ContextWrapper)
  extends RecyclerView(context, attr, defStyleAttr) {

  def this(context: Context)(implicit contextWrapper: ContextWrapper) = this(context, javaNull, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ContextWrapper) = this(context, attr, 0)

  var statuses = CollectionRecyclerStatuses()

  def createScrollListener(
    scrolled: (Int, Int, Int) => Int,
    scrollStateChanged: (Int, RecyclerView, Int) => Unit) = {
    val sl = new NineOnScrollListener {
      var scrollY = scrollListener map (_.scrollY) getOrElse 0
      override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int): Unit = {
        super.onScrolled(recyclerView, dx, dy)
        Log.d("9cards", s"onScrolled: $dy")
        scrollY = scrolled(scrollY, dx, dy)
      }
      override def onScrollStateChanged(recyclerView: RecyclerView, newState: Int): Unit = {
        super.onScrollStateChanged(recyclerView, newState)
        Log.d("9cards", s"onScrollStateChanged: $newState")
        scrollStateChanged(scrollY, recyclerView, newState)
      }
    }
    clearOnScrollListeners()
    addOnScrollListener(sl)
    scrollListener = Option(sl)
  }

  var scrollListener: Option[NineOnScrollListener] = None

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = if(statuses.disableScroll) {
    true
  } else {
    super.dispatchTouchEvent(ev)
  }

  override def attachLayoutAnimationParameters(child: View, params: LayoutParams, index: Int, count: Int): Unit =
    (statuses.enableAnimation, Option(getAdapter), Option(getLayoutManager)) match {
      case (true, Some(_), Some(layoutManager: GridLayoutManager)) =>
        val animationParams = Option(params.layoutAnimationParameters) match {
          case Some(animParams: AnimationParameters) => animParams
          case _ =>
            val animParams = new AnimationParameters()
            params.layoutAnimationParameters = animParams
            animParams
        }
        val columns = layoutManager.getSpanCount
        animationParams.count = count
        animationParams.index = index
        animationParams.columnsCount = columns
        animationParams.rowsCount = count / columns
        val invertedIndex = count - 1 - index
        animationParams.column = columns - 1 - (invertedIndex % columns)
        animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns
      case _ => super.attachLayoutAnimationParameters(child, params, index, count)
    }

  abstract class NineOnScrollListener
    extends OnScrollListener {
    var scrollY: Int
  }

}

case class CollectionRecyclerStatuses(
  disableScroll: Boolean = false,
  enableAnimation: Boolean = false)