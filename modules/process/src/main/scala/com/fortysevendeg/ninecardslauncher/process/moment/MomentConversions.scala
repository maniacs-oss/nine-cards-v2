package com.fortysevendeg.ninecardslauncher.process.moment

import com.fortysevendeg.ninecardslauncher.process.collection.models.UnformedApp
import com.fortysevendeg.ninecardslauncher.process.commons.CommonConversions
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Moment, MomentTimeSlot, PrivateCard}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.moment.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{App => ServicesApp, Moment => ServicesMoment, MomentTimeSlot => ServicesMomentTimeSlot}

trait MomentConversions extends CommonConversions {

  def toMomentSeq(servicesMomentSeq: Seq[ServicesMoment]) = servicesMomentSeq map toMoment

  def toApp(servicesApp: ServicesApp) = App(
      name = servicesApp.name,
      packageName = servicesApp.packageName,
      className = servicesApp.className,
      imagePath = servicesApp.imagePath)

  def toApp(unformedApp: UnformedApp) = App(
      name = unformedApp.name,
      packageName = unformedApp.packageName,
      className = unformedApp.className,
      imagePath = unformedApp.imagePath)

  def toAddCardRequestSeq(items: Seq[App]): Seq[AddCardRequest] =
    items.zipWithIndex map (zipped => toAddCardRequestFromAppMoment(zipped._1, zipped._2))

  def toAddCardRequestFromAppMoment(item: App, position: Int) = AddCardRequest(
    position = position,
    term = item.name,
    packageName = Option(item.packageName),
    cardType = AppCardType.name,
    intent = nineCardIntentToJson(toNineCardIntent(item)),
    imagePath = item.imagePath)

  def toAddMomentRequest(moment: Moment) =
    AddMomentRequest(
      collectionId = moment.collectionId,
      timeslot = moment.timeslot map toServicesMomentTimeSlot,
      wifi = moment.wifi,
      headphone = moment.headphone,
      momentType = moment.momentType map (_.name))

  def toAddMomentRequest(moment: NineCardsMoment) =
    AddMomentRequest(
      collectionId = None,
      timeslot = toServicesMomentTimeSlotSeq(moment),
      wifi = toWifiSeq(moment),
      headphone = false,
      momentType = Option(moment.name))

  def toServicesMomentTimeSlot(timeSlot: MomentTimeSlot) =
    ServicesMomentTimeSlot(
      from = timeSlot.from,
      to = timeSlot.to,
      days = timeSlot.days)

  def toWifiSeq(moment: NineCardsMoment) =
    moment match {
      case HomeMorningMoment => Seq.empty
      case WorkMoment => Seq.empty
      case HomeNightMoment => Seq.empty
    }

  def toServicesMomentTimeSlotSeq(moment: NineCardsMoment) =
    moment match {
      case HomeMorningMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case WorkMoment => Seq(ServicesMomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case HomeNightMoment => Seq(ServicesMomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), ServicesMomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
    }

  def toPrivateCard(app: App) =
    PrivateCard(
      term = app.name,
      packageName = Some(app.packageName),
      cardType = AppCardType,
      intent = toNineCardIntent(app),
      imagePath = app.imagePath)

}
