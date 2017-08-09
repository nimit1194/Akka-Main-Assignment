
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration.DurationInt


trait UserAccountService {

  def createAccount(listOfUsers: List[List[String]]) = {

    val system = ActorSystem("UserAccountGeneratorSystem")
    val ref = system.actorOf(AccountGeneratorActor.props)

    implicit val timeout = Timeout(1000 seconds)
    val accountsList = listOfUsers.map(ref ? _)
    print(accountsList)
  }

}