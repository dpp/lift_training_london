package code
package snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import code.lib._
import Helpers._

class HelloWorld {
  val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy: NodeSeq => NodeSeq =
    ns => {
      println("I'm transforming "+ns)
      ("#time *" #> date.map(_.toString)).apply(ns)
    }

  /*def howdy(html: NodeSeq): NodeSeq = {
    println("Dude... I got "+html)
    ("#time *" #> date.map(_.toString)).apply(html)
  }*/

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}
