/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.app.ui.commons.dialogs.publicollections

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.CommonsTweak._
import cards.nine.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import cards.nine.app.ui.commons.dialogs.{BaseActionFragment, Styles}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.commons.PaddingItemDecoration
import cards.nine.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.SharedCollection
import cards.nine.models.types._
import com.fortysevendeg.ninecardslauncher.R
import macroid.FullDsl._
import macroid._
import macroid.extras.RecyclerViewTweaks._
import macroid.extras.ResourcesExtras._
import macroid.extras.TextViewTweaks._
import macroid.extras.UIActionsExtras._
import macroid.extras.ViewTweaks._

trait PublicCollectionsUiActions extends Styles with AppNineCardsIntentConversions {

  self: BaseActionFragment with PublicCollectionsDOM with PublicCollectionsListener =>

  lazy val (categoryNamesMenu, categories) = {
    val categoriesSorted = NineCardsCategory.appsCategories map { category =>
      (resGetString(category.getStringResource) getOrElse category.name, category)
    } sortBy (_._1)
    (categoriesSorted map (_._1), categoriesSorted map (_._2))
  }

  def initialize(): TaskService[Unit] =
    ((toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.publicCollections) <~
      dtbExtended <~
      dtbAddExtendedView(tabsMenu) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (typeFilter <~
        On.click {
          val values =
            Seq(resGetString(R.string.top), resGetString(R.string.latest))
          typeFilter <~ vListThemedPopupWindowShow(values = values, onItemClickListener = {
            case 0 =>
              loadPublicCollectionsByTypeSharedCollection(TopSharedCollection)
            case _ =>
              loadPublicCollectionsByTypeSharedCollection(LatestSharedCollection)
          }, width = Some(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)))
        }) ~
      (categoryFilter <~
        On.click {
          categoryFilter <~ vListThemedPopupWindowShow(
            values = categoryNamesMenu,
            onItemClickListener = (position: Int) => {
              categories.lift(position) foreach loadPublicCollectionsByCategory
            },
            width = Some(resGetDimensionPixelSize(R.dimen.width_list_popup_menu)),
            height = Some(resGetDimensionPixelSize(R.dimen.height_list_popup_menu)))
        }) ~
      (recycler <~
        recyclerStyle <~
        rvAddItemDecoration(new PaddingItemDecoration))).toService()

  def showErrorLoadingCollectionInScreen(): TaskService[Unit] =
    (showError() ~
      showMessageInScreen(
        R.string.errorLoadingPublicCollections,
        error = true,
        action = loadPublicCollections())).toService()

  def showErrorSavingCollectionInScreen(): TaskService[Unit] =
    (showError() ~
      showMessageInScreen(
        R.string.errorSavingPublicCollections,
        error = true,
        action = loadPublicCollections())).toService()

  def showEmptyMessageInScreen(): TaskService[Unit] =
    (showError() ~
      showMessageInScreen(R.string.emptyPublicCollections, error = false, loadPublicCollections()))
      .toService()

  def showContactUsError(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService()

  def loadPublicCollections(sharedCollections: Seq[SharedCollection]): TaskService[Unit] = {
    val adapter = SharedCollectionsAdapter(sharedCollections, onAddCollection, onShareCollection)
    (showContent() ~
      (recycler <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter))).toService()
  }

  def showLoading(): TaskService[Unit] =
    ((loading <~ vVisible) ~ (recycler <~ vGone) ~ (errorContent <~ vGone)).toService()

  def updateCategory(category: NineCardsCategory): TaskService[Unit] =
    (categoryFilter <~ tvText(resGetString(category.getStringResource) getOrElse category.name))
      .toService()

  def updateTypeCollection(typeSharedCollection: TypeSharedCollection): TaskService[Unit] =
    (typeSharedCollection match {
      case TopSharedCollection    => typeFilter <~ tvText(R.string.top)
      case LatestSharedCollection => typeFilter <~ tvText(R.string.latest)
    }).toService()

  def close(): TaskService[Unit] = unreveal().toService()

  private[this] def showContent(): Ui[Any] =
    (loading <~ vGone) ~ (recycler <~ vVisible) ~ (errorContent <~ vGone)

  private[this] def showError(): Ui[Any] =
    (loading <~ vGone) ~ (recycler <~ vGone) ~ (errorContent <~ vVisible)

}
