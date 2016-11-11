package cards.nine.app.ui.launcher.actions.addmoment

import cards.nine.app.ui.commons.actions.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.types.{DialogToolbarTitle, NineCardsMoment}
import com.fortysevendeg.ninecardslauncher.R
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ViewTweaks._

trait AddMomentUiActions
  extends Styles {

  self: BaseActionFragment with AddMomentDOM with AddMomentListener =>

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary, DialogToolbarTitle) <~
      dtbChangeText(R.string.addMoment) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~ recyclerStyle <~ rvAddItemDecoration(new AddMomentItemDecoration))).toService

  def addMoments(moments: Seq[NineCardsMoment]): TaskService[Unit] = {
    val adapter = new AddMomentAdapter(moments, addMoment)
    ((recycler <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loading <~ vGone)).toService
  }

  def showLoading(): TaskService[Unit] = ((loading <~ vVisible) ~ (recycler <~ vGone)).toService

  def showEmptyMessageInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.emptyAddMoment, error = false, loadMoments()).toService

  def showErrorLoadingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorLoadingAddMoment, error = true, loadMoments()).toService

  def showErrorSavingCollectionInScreen(): TaskService[Unit] =
    showMessageInScreen(R.string.errorSavingAddMoment, error = true, loadMoments()).toService

  def close(): TaskService[Unit] = unreveal().toService

}
