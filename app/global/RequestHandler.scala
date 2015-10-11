package global

import com.google.inject.Inject
import play.api.http.HttpRequestHandler
import play.api.mvc.{Action, Handler, RequestHeader, Results}
import play.api.routing.Router

class RequestHandler @Inject()(router: Router) extends HttpRequestHandler {
  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) = {
    if (!request.path.startsWith("/assets") && request.path != "/favicon.ico") {
      play.Logger.info(request.toString())
      request.queryString.toSeq.sortBy(_._1).foreach {
        case (k, v) => play.Logger.info(s"query: $k -> $v")
      }
      request.headers.toMap.toSeq.sortBy(_._1).foreach {
        case (k, v) => play.Logger.info(s"header: $k -> $v")
      }
    }
    router.routes.lift(request) match {
      case Some(handler) => (request, handler)
      case None => (request, Action(Results.NotFound))
    }
  }
}
