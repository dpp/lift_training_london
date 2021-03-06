package bootstrap.liftweb

import net.liftweb._
import util._
import json._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._
import xml.NodeSeq
import reactive.web.Reactions


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(() => vendor.closeAllConnections_!())

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User)

    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      Menu.i("Sometimes") / "sometimes" >> If(shouldDisplay _,
        S ? "Can't view now"),

      Menu.i("Reactive") / "reactive",

      Menu.i("Form") / "form",

      Menu.i("top") / "top" submenus (
        Menu.i("About") / "about" >> Hidden >> LocGroup("bottom"),
        Menu.i("Contact") / "contact"
        ),

      Menu.param[What]("What", "What", {
        case "one" => Full(First)
        case "two" => Full(Second)
        case "both" => Full(Both)
        case "who" => Full(Both)
        case _ => Empty
      }, w => w.showString) / "what" >> If(() => true, "huh") >>
        Value(First),

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu.i("Static") / "static" / **)

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    LiftRules.dispatch.append(code.lib.FullRest)

    /*
    LiftRules.dispatch.append {
      case r if {println("Content type "+r.contentType+" json "+r.json+" body "+
        (new String(r.body.open_!))); false} => null

    }*/

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)

    // Reactive Web
    Reactions.init(true)

    LiftRules.dispatch.append(CVServer)
  }

  def example: NodeSeq => NodeSeq = (for {
    user <- User.currentUser
  } yield ".name" #> user.firstName) openOr "*" #> "Not logged in"

  def shouldDisplay: Boolean = Helpers.randomInt(10) > 5
}

sealed trait What {
  def showString: String
}

case object First extends What {
  def showString: String = "one"
}

case object Second extends What {
  def showString: String = "two"
}

case object Both extends What {
  def showString: String = "both"
}

import rest._

object CVServer extends RestHelper {
  object MyVar extends SessionVar(45)
  object MyVar2 extends SessionVar("")

  serve {
    case "cv_int" :: param Get _ =>
      param.flatMap(v => Helpers.asInt(v).toList).foreach(MyVar.set _)
    <int>{MyVar.get.toString}</int>
    case "cv_str" :: param Get _ => param.foreach(MyVar2.set _)
    <str>{MyVar2.get}</str>
  }
}