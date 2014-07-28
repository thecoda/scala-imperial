package akka.enhancement

import akka.actor.Actor


trait PublicAroundReceive extends Actor {
  def publicAroundReceive(receive: Actor.Receive, msg: Any): Unit = {
    super.aroundReceive(receive, msg)
  }

  override protected[akka] def aroundReceive(receive: Actor.Receive, msg: Any): Unit = {
    publicAroundReceive(receive, msg)
  }

}
