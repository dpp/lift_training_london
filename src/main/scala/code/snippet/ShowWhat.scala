package code.snippet

/**
 * Created by IntelliJ IDEA.
 * User: dpp
 * Date: 9/14/11
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */

import bootstrap.liftweb._
import net.liftweb._
import common.{Empty, Box}
import util._
import Helpers._

class ShowWhat(w: What) {
  def this() = this(Both)
  def render = "*" #> w.showString
}