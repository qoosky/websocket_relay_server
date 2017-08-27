package qoosky.cloudapi

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.server.Directives._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import org.slf4j.LoggerFactory

class WebService(system: ActorSystem) {

  val logger = LoggerFactory.getLogger("WebService")
  val webSocketBufferSize = 5
  var actuatorId = 0
  var keypadId = 0

  val supervisor: ActorRef = system.actorOf(Props[Supervisor], name = "Supervisor")

  val fromWebSocket: Flow[Message, ActorMessage, _] = Flow[Message].collect {
    case TextMessage.Strict(txt) => WebSocketMessage(txt) // Ignore incomplete (part of a big message) or binary messages.
  }

  val backToWebSocket: Flow[ActorMessage, Message, _] = Flow[ActorMessage].map {
    case WebSocketMessage(txt) => TextMessage(txt)
    case x: ActorMessage => {
      logger.error("Expected WebSocketMessage, but unexpected ActorMessage was sent: %s" format x)
      throw new RuntimeException
    }
  }

  // Raspberry Pi, Arduino, Android Things...
  def actuatorWebSocketService: Flow[Message, Message, _] = {
    val actuator: ActorRef = system.actorOf(Props[ActuatorActor], name = "ActuatorActor-%d".format(actuatorId))
    val fromActor: Source[ActorMessage, _] = Source.actorRef[ActorMessage](bufferSize = webSocketBufferSize, OverflowStrategy.dropHead).mapMaterializedValue{ webSocket: ActorRef =>
      supervisor ! NewActuator(actuator, webSocket)
    }
    val toActor: Sink[ActorMessage, _] = Sink.actorRef[ActorMessage](actuator, Disconnected)
    val in: Sink[Message, _] = Flow[Message].via(fromWebSocket).to(toActor)
    val out: Source[Message, _] = fromActor.via(backToWebSocket)
    actuatorId += 1
    Flow.fromSinkAndSource(in, out)
  }

  // JavaScript Client (AngularJS)
  def keypadWebSocketService: Flow[Message, Message, _] = {
    val keypad: ActorRef = system.actorOf(Props[KeypadActor], name = "KeypadActor-%d".format(keypadId))
    val fromActor: Source[ActorMessage, _] = Source.actorRef[ActorMessage](bufferSize = webSocketBufferSize, OverflowStrategy.dropHead).mapMaterializedValue{ webSocket: ActorRef =>
      supervisor ! NewKeypad(keypad, webSocket)
    }
    val toActor: Sink[ActorMessage, _] = Sink.actorRef[ActorMessage](keypad, Disconnected)
    val in: Sink[Message, _] = Flow[Message].via(fromWebSocket).to(toActor)
    val out: Source[Message, _] = fromActor.via(backToWebSocket)
    keypadId += 1
    Flow.fromSinkAndSource(in, out)
  }

  def route = path("") {
    get {
      complete("The latest API version is /v1")
    }
  } ~
  pathPrefix("v1") {
    pathEndOrSingleSlash {
      complete("Available API endpoints: /v1/controller/actuator/ws")
    } ~
    pathPrefix("controller" / "actuator" / "ws") {
      pathEndOrSingleSlash {
        handleWebSocketMessages(actuatorWebSocketService)
      }
    } ~
    pathPrefix("controller" / "keypad" / "ws") {
      pathEndOrSingleSlash {
        handleWebSocketMessages(keypadWebSocketService)
      }
    }
  }
}
