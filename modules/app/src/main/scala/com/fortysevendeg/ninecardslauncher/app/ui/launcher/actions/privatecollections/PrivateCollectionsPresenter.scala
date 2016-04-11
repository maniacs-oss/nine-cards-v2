package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, CollectionException}
import com.fortysevendeg.ninecardslauncher.process.commons.models.{PrivateCollection, Collection}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentConversions, MomentException}
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class PrivateCollectionsPresenter(actions: PrivateCollectionsActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with Conversions
  with MomentConversions {

  def initialize(): Unit = {
    loadPrivateCollections()
    actions.initialize().run
  }

  def loadPrivateCollections(): Unit = {
    Task.fork(getPrivateCollections.run).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = (privateCollections: Seq[PrivateCollection]) => actions.addPrivateCollections(privateCollections),
      onException = (ex: Throwable) => actions.showContactUsError())
  }

  def saveCollection(privateCollection: PrivateCollection): Unit = {
    Task.fork(addCollection(privateCollection).run).resolveAsyncUi(
      onResult = (c) => actions.addCollection(c) ~ actions.close(),
      onException = (ex) => actions.showContactUsError())
  }

  private[this] def getPrivateCollections:
  ServiceDef2[Seq[PrivateCollection], AppException with CollectionException with MomentException] =
    for {
      collections <- di.collectionProcess.getCollections
      apps <- di.deviceProcess.getSavedApps(GetByName)
      unformedApps = toSeqUnformedApp(apps)
      newCollections <- di.collectionProcess.generatePrivateCollections(unformedApps)
      newMomentCollections <- di.momentProcess.generatePrivateMoments(unformedApps map toApp, newCollections.length)
    } yield {
      val privateCollections = newCollections filterNot { newCollection =>
        newCollection.appsCategory match {
          case Some(category) => (collections flatMap (_.appsCategory)) contains category
          case _ => false
        }
      }
      val privateMoments = newMomentCollections filterNot (newMomentCollection => (collections map (_.collectionType)) contains newMomentCollection.collectionType)
      privateCollections ++ privateMoments
    }

  private[this] def addCollection(privateCollection: PrivateCollection):
  ServiceDef2[Collection, CollectionException with CardException] =
    for {
      collection <- di.collectionProcess.addCollection(toAddCollectionRequest(privateCollection))
      cards <- di.collectionProcess.addCards(collection.id, privateCollection.cards map toAddCollectionRequest)
    } yield collection.copy(cards = cards)


}

trait PrivateCollectionsActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def addPrivateCollections(privateCollections: Seq[PrivateCollection]): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def close(): Ui[Any]
}