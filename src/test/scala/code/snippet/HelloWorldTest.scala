package code {
package snippet {

import org.specs._
import org.specs.runner.JUnit4
import org.specs.runner.ConsoleRunner
import net.liftweb._
import http._
import net.liftweb.util._
import net.liftweb.common._
import org.specs.matcher._
import org.specs.specification._
import Helpers._
import lib._


//class HelloWorldTestSpecsAsTest extends JUnit4(HelloWorldTestSpecs)
//object HelloWorldTestSpecsRunner extends ConsoleRunner(HelloWorldTestSpecs)

object HelloWorldTestSpecs extends Specification {
  val session = new LiftSession("", randomString(20), Empty)
  val stableTime = now

  val req: Req = Req.nil.withNewPath(ParsePath("foo" :: "bar" :: Nil,
  "", true, false)) 

  override def executeExpectations(ex: Examples, t: =>Any): Any = {
    S.init(req, session) {
      DependencyFactory.time.doWith(stableTime) {
        super.executeExpectations(ex, t)
      }
    }
  }


  "HelloWorld Snippet" should {
    setSequential()

    "Put the time in the node" in {
      val hello = new HelloWorld
      Thread.sleep(1000) // make sure the time changes

      val str = hello.howdy(<span>Welcome to your Lift app at <span id="time">Time goes here</span></span>).toString

      str.indexOf(stableTime.toString) must be >= 0
      str.indexOf("Welcome to") must be >= 0
    }

        "Has expected path" in {
      S.request.
        map(_.path.partPath.mkString("/",
        "/", "")).open_! must_== "/foo/bar"
    }
  }
}

}
}
