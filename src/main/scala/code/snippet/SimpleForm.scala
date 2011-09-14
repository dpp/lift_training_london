package code.snippet

/**
 * Created by IntelliJ IDEA.
 * User: dpp
 * Date: 9/14/11
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */

import net.liftweb._
import util._
import Helpers._
import http._

class SimpleForm extends StatefulSnippet {
  override def dispatch = {case _ => render}
  var name = ""
  var age = 0
  def render =
    ".name" #> SHtml.text(name, name = _) &
    ".age" #> SHtml.text(age.toString,
      s => age = Helpers.asInt(s) openOr 0) &
  ":submit" #> SHtml.onSubmitUnit(() =>
  {if (age < 10) S.error("Too young") else
  if (name.length > 2) {S.notice("Thanks "+name)
    S.redirectTo("/")
  } else {S.error("Name too short")}
  })
}