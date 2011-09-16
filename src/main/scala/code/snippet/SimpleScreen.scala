package code.snippet

import net.liftweb.http._
import net.liftweb.wizard._
import net.liftweb.common._
/**
 * Created by IntelliJ IDEA.
 * User: dpp
 * Date: 9/14/11
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */

class SimpleScreen extends Wizard {
  val screen1 = new Screen {
    val name = field("Name", "", toLower,
      valMinLen(3, S ? "Name too short"))

    val age = field("Age", 0,
      minVal(10, "You must be 10 years old"))

    val likesCats = field("Do you like cats?", false)
  }

  val screen2 = new Screen {
    val whenClause = mothersAge.lift(_ > 50)
    val mothersName: Field[Box[String]] = optional(field("Mother's name", "")) when (whenClause)
    val mothersAge = field("Mother's age", 0,
      minVal(screen1.age.get + 10, "Mother too young"))

    override def nextScreen = if (screen1.likesCats) Empty else catHaters
  }

  val catHaters = new Screen {
    val reallyHatesCats = field("Do you really hate cats?", false)
  }

  def finish() {
    S.notice("Thanks " + screen1.name + " who is " +
      (screen1.age.get * 5) + " years old")
  }
}