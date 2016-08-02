package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.widgets

import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.view.{LayoutInflater, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.LauncherPresenter
import com.fortysevendeg.ninecardslauncher.process.device.models.Widget
import com.fortysevendeg.ninecardslauncher2.TR
import com.fortysevendeg.ninecardslauncher2.TypedResource._
import macroid.ActivityContextWrapper

case class WidgetsAdapter(widgets: Seq[Widget], widgetContentWidth: Int, widgetContentHeight: Int)
  (implicit activityContext: ActivityContextWrapper, uiContext: UiContext[_], presenter: WidgetsPresenter, launcherPresenter: LauncherPresenter)
  extends RecyclerView.Adapter[ViewHolderWidgetsLayoutAdapter] {

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderWidgetsLayoutAdapter = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.widget_item, parent, false)
    ViewHolderWidgetsLayoutAdapter(view)
  }

  override def getItemCount: Int = widgets.size

  override def onBindViewHolder(viewHolder: ViewHolderWidgetsLayoutAdapter, position: Int): Unit = {
    val publicCollection = widgets(position)
    viewHolder.bind(publicCollection, widgetContentWidth, widgetContentHeight).run
  }

  def getLayoutManager = new LinearLayoutManager(activityContext.application)

}
