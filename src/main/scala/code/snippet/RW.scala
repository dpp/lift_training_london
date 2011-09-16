package code
package snippet

import scala.xml._
import net.liftweb._
import util._
import http._
import js._
import JsCmds._
import Helpers._

import reactive._
  import web._
    import html._


class RW extends Observing {

  def innerBindFunc(t: Long): NodeSeq=>NodeSeq =
    "#span" #> t.toString
  lazy val innerBindSignal = clockSig map innerBindFunc
  def render = "#div *" #> Span(innerBindSignal)

  // Create an EventStream that fires timer ticks for up to 10 minutes
  lazy val clockES = new Timer(0, 2000, {t => this; t>(10 minutes)})
 
  // Create a signal from the EventStream whose value, until
  // the first tick is received, is 0L
  lazy val clockSig = clockES.hold(0L)

  lazy val items = Var(List("First", "Second", "Third"))

  def selThing = ".sel" #> Select(items) &
  ".rest" #> (Text("foo") ++ SHtml.ajaxButton(<b>more</b>, () => {
    items.value = randomInt(50).toString :: items.value
    Noop
  })) & ".sel2" #> Select(items)

}
