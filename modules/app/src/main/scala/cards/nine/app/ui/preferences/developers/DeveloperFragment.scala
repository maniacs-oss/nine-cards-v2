package cards.nine.app.ui.preferences.developers

import android.app.Fragment
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{CheckBoxPreference, Preference, PreferenceFragment}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class DeveloperFragment extends PreferenceFragment with Contexts[Fragment] with FindPreferences {

  lazy val dom = DeveloperDOM(this)

  lazy val preferencesJobs = new DeveloperJobs(new DeveloperUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach (_.setTitle(getString(R.string.developerPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_dev)

    preferencesJobs.initialize().resolveAsync()

    dom.appsCategorizedPreferences.setOnPreferenceClickListener(new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        getFragmentManager
          .beginTransaction()
          .addToBackStack(AppsCategorized.name)
          .replace(android.R.id.content, new AppsListFragment)
          .commit()
        true
      }
    })

  }

}

case class DeveloperDOM(dom: FindPreferences) {

  def appsCategorizedPreferences = dom.find[Preference](AppsCategorized)
  def overrideBackendV2UrlPreference =
    dom.find[CheckBoxPreference](OverrideBackendV2Url)
  def backendV2UrlPreference       = dom.find[Preference](BackendV2Url)
  def androidTokenPreferences      = dom.find[Preference](AndroidToken)
  def deviceCloudIdPreferences     = dom.find[Preference](DeviceCloudId)
  def currentDensityPreferences    = dom.find[Preference](CurrentDensity)
  def probablyActivityPreference   = dom.find[Preference](ProbablyActivity)
  def headphonesPreference         = dom.find[Preference](Headphones)
  def locationPreference           = dom.find[Preference](Location)
  def weatherPreference            = dom.find[Preference](Weather)
  def restartApplicationPreference = dom.find[Preference](RestartApplication)
  def clearCacheImagesPreference   = dom.find[Preference](ClearCacheImages)
  def isStethoActivePreference     = dom.find[CheckBoxPreference](IsStethoActive)

}