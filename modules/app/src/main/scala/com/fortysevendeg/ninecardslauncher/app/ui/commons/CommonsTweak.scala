package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.{Paint, PorterDuff}
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable._
import android.os.Vibrator
import android.support.design.widget.Snackbar
import android.view.{View, ViewGroup}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ColorOps._
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.ListPopupWindow
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View.OnClickListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.{OnItemClickListener, OnItemSelectedListener}
import android.widget._
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.ViewOps._
import com.fortysevendeg.ninecardslauncher.app.ui.components.adapters.ThemeArrayAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.DrawerBackgroundDrawable
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.flexbox.FlexboxLayout
import macroid._

object CommonsTweak {

  def vBackgroundBoxWorkspace(color: Int, horizontalPadding: Int = 0, verticalPadding: Int = 0)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    val radius = resGetDimensionPixelSize(R.dimen.radius_default)
    Lollipop.ifSupportedThen {
      vBackgroundColor(color) +
        vClipBackground(radius, horizontalPadding = horizontalPadding, verticalPadding = verticalPadding) +
        vElevation(resGetDimensionPixelSize(R.dimen.elevation_box_workspaces))
    } getOrElse {
      val drawable = new DrawerBackgroundDrawable(color, horizontalPadding, verticalPadding, radius)
      vBackground(drawable)
    }
  }

  def vBackgroundCollection(indexColor: Int)(implicit contextWrapper: ContextWrapper): Tweak[View] =
    vBackgroundCircle(resGetColor(getIndexColor(indexColor)))

  def vBackgroundCircle(color: Int)(implicit contextWrapper: ContextWrapper): Tweak[View] = {
    def createShapeDrawable(c: Int) = {
      val drawableColor = new ShapeDrawable(new OvalShape())
      drawableColor.getPaint.setColor(c)
      drawableColor.getPaint.setStyle(Paint.Style.FILL)
      drawableColor.getPaint.setAntiAlias(true)
      drawableColor
    }

    def getDrawable(c: Int): Drawable = {
      val drawableColor = createShapeDrawable(c)
      val padding = resGetDimensionPixelSize(R.dimen.elevation_default)
      val drawableShadow = createShapeDrawable(resGetColor(R.color.shadow_default))
      val layer = new LayerDrawable(Array(drawableShadow, drawableColor))
      layer.setLayerInset(0, padding, padding, padding, 0)
      layer.setLayerInset(1, padding, 0, padding, padding)
      layer
    }
    val elevation = resGetDimensionPixelSize(R.dimen.elevation_default)

    vBackground(Lollipop ifSupportedThen {
      new RippleDrawable(
        new ColorStateList(Array(Array()), Array(color.dark(0.2f))),
        createShapeDrawable(color),
        javaNull)
    } getOrElse {
      val states = new StateListDrawable()
      states.addState(Array[Int](android.R.attr.state_pressed), getDrawable(color.dark()))
      states.addState(Array.emptyIntArray, getDrawable(color))
      states
    }) + (Lollipop ifSupportedThen vElevation(elevation) getOrElse Tweak.blank)
  }

  def vSetPosition(position: Int): Tweak[View] = vTag(R.id.position, position)

  def vSetType(t: String) = vTag(R.id.view_type, t)

  def vAddField[T](key: String, value: T) = Tweak[View] { view =>
    view.setTag(R.id.fields_map, view.getFieldsMap + ((key, value)))
  }

  def vRemoveField(key: String) = Tweak[View] { view =>
    view.setTag(R.id.fields_map, view.getFieldsMap - key)
  }

  def vUseLayerHardware = vTag(R.id.use_layer_hardware, "")

  def vLayerHardware(activate: Boolean) = Transformer {
    case v: View if v.hasLayerHardware => v <~ (if (activate) vLayerTypeHardware() else vLayerTypeNone())
  }


  def vListThemedPopupWindowShow(
    icons: Seq[Int] = Seq.empty,
    values: Seq[String],
    onItemClickListener: (Int) ⇒ Unit,
    width: Option[Int] = None,
    height: Option[Int] = None
  )(implicit contextWrapper: ContextWrapper, theme: NineCardsTheme) =
    Tweak[View] { view =>
      val listPopupWindow = new ListPopupWindow(contextWrapper.bestAvailable)
      listPopupWindow.setAdapter(new ThemeArrayAdapter(icons, values))
      listPopupWindow.setAnchorView(view)
      width foreach listPopupWindow.setWidth
      height foreach listPopupWindow.setHeight
      listPopupWindow.setModal(true)
      listPopupWindow.setOnItemClickListener(new OnItemClickListener {
        override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
          onItemClickListener(position)
          listPopupWindow.dismiss()
        }
      })
      listPopupWindow.show()
    }

}

object ExtraTweaks {

  // TODO - Move to macroid extras

  def uiShortToast2(msg: Int)(implicit c: ContextWrapper): Ui[Unit] =
    Ui(Toast.makeText(c.application, msg, Toast.LENGTH_SHORT).show())

  def uiLongToast2(msg: Int)(implicit c: ContextWrapper): Ui[Unit] =
    Ui(Toast.makeText(c.application, msg, Toast.LENGTH_LONG).show())

  def uiShortToast2(msg: String)(implicit c: ContextWrapper): Ui[Unit] =
    Ui(Toast.makeText(c.application, msg, Toast.LENGTH_SHORT).show())

  def uiLongToast2(msg: String)(implicit c: ContextWrapper): Ui[Unit] =
    Ui(Toast.makeText(c.application, msg, Toast.LENGTH_LONG).show())

  def vResize(size: Int): Tweak[View] = vResize(size, size)

  def vResize(width: Int, height: Int): Tweak[View] = Tweak[View] {
    view =>
      val params = view.getLayoutParams
      params.height = width
      params.width = height
      view.requestLayout()
  }

  def vgAddViewByIndexParams[V <: View](view: V, index: Int, params: ViewGroup.LayoutParams): Tweak[ViewGroup] =
    Tweak[ViewGroup](_.addView(view, index, params))

  def ivBlank: Tweak[ImageView] = Tweak[ImageView](_.setImageBitmap(javaNull))

  def vDisableHapticFeedback: Tweak[View] = Tweak[View](_.setHapticFeedbackEnabled(false))

  def uiVibrate(millis: Long = 100)(implicit contextWrapper: ContextWrapper): Ui[Any] = Ui {
    contextWrapper.application.getSystemService(Context.VIBRATOR_SERVICE) match {
      case vibrator: Vibrator => vibrator.vibrate(millis)
      case _ =>
    }
  }

  def etShowKeyboard(implicit contextWrapper: ContextWrapper) = Tweak[EditText] { editText =>
    editText.requestFocus()
    Option(contextWrapper.bestAvailable.getSystemService(Context.INPUT_METHOD_SERVICE)) foreach {
      case imm: InputMethodManager => imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
      case _ =>
    }
  }

  def dlOpenDrawerEnd: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.openDrawer(GravityCompat.END))

  def dlCloseDrawerEnd: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.closeDrawer(GravityCompat.END))

  def dlLockedClosedStart: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START))

  def dlLockedClosedEnd: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END))

  def dlLockedClosed: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED))

  def dlUnlocked: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED))

  def dlUnlockedStart: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START))

  def dlUnlockedEnd: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END))

  def dlLockedOpen: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN))

  def dlLockedOpenStart: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.START))

  def dlLockedOpenEnd: Tweak[DrawerLayout] = Tweak[DrawerLayout](_.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END))

  def dlSwapDrawer: Tweak[DrawerLayout] = Tweak[DrawerLayout] { view =>
    if (view.isDrawerOpen(GravityCompat.START)) {
      view.closeDrawer(GravityCompat.START)
    } else {
      view.openDrawer(GravityCompat.START)
    }
  }

  def dlSwapDrawerEnd: Tweak[DrawerLayout] = Tweak[DrawerLayout] { view =>
    if (view.isDrawerOpen(GravityCompat.END)) {
      view.closeDrawer(GravityCompat.END)
    } else {
      view.openDrawer(GravityCompat.END)
    }
  }

  def tvUnderlineText(text: String): Tweak[TextView] = Tweak[TextView] { tv =>
    val content = new SpannableString(text)
    content.setSpan(new UnderlineSpan(), 0, text.length, 0)
    tv.setText(content)
  }

  def sSelection(position: Int) = Tweak[Spinner](_.setSelection(position))

  def sItemSelectedListener(onItem: (Int => Unit)) = Tweak[Spinner](_.setOnItemSelectedListener(new OnItemSelectedListener {
    override def onNothingSelected(parent: AdapterView[_]): Unit = {}

    override def onItemSelected(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = onItem(position)
  }))

  def sChangeDropdownColor(color: Int) = Tweak[Spinner](_.getBackground.setColorFilter(color, PorterDuff.Mode.SRC_ATOP))

  def tvHintColor(color: Int): Tweak[TextView] = Tweak[TextView](_.setHintTextColor(color))

  def vSnackbarLongAction(res: Int, buttonText: Int, f: () ⇒ Unit) = Tweak[W] { view ⇒
    Ui(Snackbar.make(view, res, Snackbar.LENGTH_LONG).setAction(buttonText, new OnClickListener {
      override def onClick(v: View): Unit = f()
    }).show()).run
  }

  def tvAllCaps2(allCaps: Boolean = true): Tweak[TextView] = Tweak[TextView](_.setAllCaps(allCaps))

}

object CommonsResourcesExtras {

  def resGetQuantityString(resourceId: Int, quantity: Int)(implicit c: ContextWrapper): String =
    c.bestAvailable.getResources.getQuantityString(resourceId, quantity)

  def resGetQuantityString(resourceId: Int, quantity: Int, formatArgs: AnyRef*)(implicit c: ContextWrapper): String =
    c.bestAvailable.getResources.getQuantityString(resourceId, quantity, formatArgs: _*)

}