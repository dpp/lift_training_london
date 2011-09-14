package code
package snippet

import net.liftweb.http.SHtml
import net.liftweb.http._
import js._
import JE._
import JsCmds._
/**
 * Created by IntelliJ IDEA.
 * User: dpp
 * Date: 9/14/11
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */

import model._

object MyCount extends SessionVar[Int](0)
object MyReq extends RequestVar(0)
object MyTransient extends TransientRequestVar(0)

class JaxA {
  var y = 0
  def render = {

    SHtml.ajaxButton(<b>Press Me</b>,
      () => {

        y += 1
        MyCount.atomicUpdate(_ + 1)
        MyReq.atomicUpdate(_ + 1)
        MyTransient.atomicUpdate(i => i + 1)
        SetHtml("ajax_answer",
        <i>Local var {y.toString} session {MyCount.toString}
          Req Var {MyReq.toString} Trans {MyTransient.toString}
        <lift:MyThing/></i>)
      })
  }
}

object MyThing {
  def render = {
    MyTransient.atomicUpdate(_ + 1)
    <b>Transient req var again {MyTransient.toString}</b>
  }
}