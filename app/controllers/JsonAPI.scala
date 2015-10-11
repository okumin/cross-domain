package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class JsonAPI extends Controller {
  def user(id: Int) = Action { request =>
    val json = Json.obj("id" -> id, "name" -> "okumin")
    request.getQueryString("callback") match {
      case Some(callback) => Ok(s"$callback($json)")
        .withHeaders(CONTENT_TYPE -> "text/javascript; charset=utf-8")
      case None => Ok(json)
    }
  }
}
