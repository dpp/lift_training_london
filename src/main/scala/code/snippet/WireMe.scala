package code
package snippet

import model._

import net.liftweb.http._
import js._
import JsCmds._
import net.liftweb.util._
import net.liftweb.common._
import Helpers._
import xml.NodeSeq

/**
 * Created by IntelliJ IDEA.
 * User: dpp
 * Date: 9/15/11
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */

class WireORama {
  val quantity = ValueCell(0)
  val price = ValueCell(1d)
  val extension = price.lift(quantity)((p, q) => {
    println("Recaling extension "+p+" "+q)
    p * q})
}


object WiredInfo extends SessionVar(new WireORama)

object RM extends SmallRequestMemoize[Long, Box[User]]

trait SmallRequestMemoize[A, B] extends RequestMemoize[A, B] with SmallCache

trait SmallCache {
  self: MemoizeVar[_, _] =>
  override def cacheSize = SmallCache.defaultCacheSize
}

object SmallCache {
  @volatile var defaultCacheSize = 20
}


class WireMe {
  def user(id: Long): Box[User] = RM(id, User.find(id))


  def memo: NodeSeq => NodeSeq = {
    var cnt = 0
    SHtml.idMemoize(inner =>
      (ns: NodeSeq) => {
        println("We're doing a transform on template "+ns)
      val f = ".num *" #> cnt &
      ".button [onclick]" #> SHtml.ajaxInvoke(() => {
        cnt += 1
        inner.setHtml()
      })
      f(ns)
      }
    )
  }


  def addOne = SHtml.ajaxButton(<b>Add to Qnty</b>,
  () => {
    WiredInfo.get.quantity.atomicUpdate(_ + 1)
    Noop
  })
  
  def setPrice = SHtml.ajaxText(WiredInfo.get.price.get.toString,
  s => {
    Helpers.asDouble(s).foreach(d => WiredInfo.get.price.set(d))
    Noop
  })

  def qnty = WiringUI.asText(WiredInfo.get.quantity)

  def pr = WiringUI.asText(WiredInfo.get.price)

  def ext = WiringUI.asText(WiredInfo.get.extension)


}