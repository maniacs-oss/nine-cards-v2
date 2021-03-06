package cards.nine.commons.test.data

import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.models.types.CardType
import cards.nine.models.{Card, CardData, NineCardsIntentConversions}

trait CardTestData extends NineCardsIntentConversions {

  def card(num: Int = 0) =
    Card(
      id = cardId + num,
      position = cardPosition + num,
      term = term,
      packageName = Option(cardPackageName + num),
      cardType = CardType(cardType),
      intent = jsonToNineCardIntent(intent),
      imagePath = Option(cardImagePath),
      notification = Option(notification))

  val card: Card         = card(0)
  val seqCard: Seq[Card] = Seq(card(0), card(1), card(2))

  val cardData: CardData         = card.toData
  val seqCardData: Seq[CardData] = seqCard map (_.toData)

  val cardPackageSeq: Seq[String]     = seqCard flatMap (_.packageName)
  val cardDataPackageSeq: Seq[String] = seqCardData flatMap (_.packageName)

  val cardIdSeq: Seq[Int] = seqCard map (_.id)

  val cardPackageSet: Set[String] = cardPackageSeq.toSet

}
