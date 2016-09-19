package com.fortysevendeg.ninecardslauncher.app.ui.collections.snails

import android.animation.{Animator, AnimatorListenerAdapter}
import android.graphics.Point
import android.view.View
import android.view.animation.{AccelerateDecelerateInterpolator, DecelerateInterpolator}
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SnailsCommons._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons.SpeedAnimations
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, ContextWrapper, Snail, Tweak}

import scala.concurrent.Promise

object CollectionsSnails {

  def animationEnterTitle(implicit context: ContextWrapper) = {
    val distance = resGetDimensionPixelSize(R.dimen.padding_default)
    val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
    vTranslationY(distance) +
      vVisible +
      vAlpha(0) ++
      applyAnimation(
      duration = Option(duration),
      y = Option(0),
      alpha = Option(1))
  }

  def animationOutTitle(implicit context: ContextWrapper) = {
    val distance = resGetDimensionPixelSize(R.dimen.padding_default)
    val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
    applyAnimation(
      duration = Option(duration),
      y = Option(distance),
      alpha = Option(0)) +
      vGone
  }

  def animationTitle[W <: View](
    fromLeft: Boolean,
    transformation: Tweak[W])(implicit context: ContextWrapper) = {
    val distance = if (fromLeft) -resGetDimensionPixelSize(R.dimen.padding_default) else resGetDimensionPixelSize(R.dimen.padding_default)
    val duration = resGetInteger(R.integer.anim_duration_icon_collection_detail)
    applyAnimation(
        duration = Option(duration),
        x = Option(-distance),
        alpha = Option(0),
        scaleX = Option(0.7f),
        scaleY = Option(0.7f)) +
      vTranslationX(distance) +
      transformation ++
      applyAnimation(
        duration = Option(duration),
        x = Option(0),
        alpha = Option(1),
        scaleX = Option(1f),
        scaleY = Option(1f)) +
      vRotation(0)
  }

  def enterToolbar(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] = {
    val display = activityContextWrapper.getOriginal.getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    val height = size.y
    val times = height.toFloat / resGetDimension(R.dimen.height_toolbar_collection_details)
    vScaleY(times) ++ applyAnimation(scaleY = Some(1))
  }

  def exitToolbar(implicit activityContextWrapper: ActivityContextWrapper): Snail[View] = {
    val display = activityContextWrapper.getOriginal.getWindowManager.getDefaultDisplay
    val size = new Point()
    display.getSize(size)
    val height = size.y
    val times = (height.toFloat / resGetDimension(R.dimen.height_toolbar_collection_details)) * 2
    applyAnimation(scaleY = Some(times))
  }

  def enterViews(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      view.setAlpha(0)
      view
        .animate
        .setDuration(SpeedAnimations.getDuration)
        .setInterpolator(new DecelerateInterpolator())
        .alpha(1f)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

  def exitViews(implicit context: ContextWrapper): Snail[View] = Snail[View] {
    view =>
      view.clearAnimation()
      view.setLayerType(View.LAYER_TYPE_HARDWARE, javaNull)
      val animPromise = Promise[Unit]()
      val move = resGetDimensionPixelSize(R.dimen.space_enter_views_collection_detail)
      view
        .animate
        .setDuration(SpeedAnimations.getDuration)
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .translationY(move)
        .alpha(0)
        .setListener(new AnimatorListenerAdapter {
          override def onAnimationEnd(animation: Animator): Unit = {
            super.onAnimationEnd(animation)
            view.setLayerType(View.LAYER_TYPE_NONE, javaNull)
            animPromise.trySuccess(())
          }
        }).start()
      animPromise.future
  }

}
