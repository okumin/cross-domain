package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

class JsonAPI extends Controller {
  def user(id: Int) = Action { request =>
    val json = Json.obj("id" -> id, "name" -> "okumin")
    request.getQueryString("callback") match {
      case Some(callback) => Ok(s"$callback($json)")
        .withHeaders(CONTENT_TYPE -> "text/javascript; charset=utf-8")
      case None => Ok(json)
    }
  }

  def cors(f: Request[AnyContent] => Result): Action[AnyContent] = Action { request =>
    request.headers.get("Origin") match {
      case None => f(request)
      case Some("http://foo.mofu.poyo") =>
        f(request).withHeaders(
          "Access-Control-Allow-Origin" -> s"http://foo.mofu.poyo",
          "Access-Control-Allow-Credentials" -> "true"
        )
      case Some(_) => BadRequest(Json.obj("message" -> "Origin is not allowed."))
    }
  }

  val option: Action[AnyContent] = Action { request =>
    if (request.headers.get("Origin").exists(_ != "http://foo.mofu.poyo")) {
      BadRequest(Json.obj("message" -> "Origin is not allowed."))
    } else {
      Ok(Results.EmptyContent()).withHeaders(
        "Access-Control-Allow-Origin" -> s"http://foo.mofu.poyo",
        "Access-Control-Allow-Methods" -> "PUT, POST, GET, OPTIONS",
        "Access-Control-Allow-Headers" ->  "X-Requested-With",
        "Access-Control-Max-Age" -> "0"
      )
    }
  }
  def optionsPOST = option
  def options(id: Int) = option

  def getUser(id: Int) = cors { _ =>
    val json = Json.obj("id" -> id, "name" -> "okumin")
    Ok(json)
  }

  def postUser = cors { implicit request =>
    Form(single("name" -> text)).bindFromRequest().fold(
      _ => InternalServerError(Json.obj("message" -> "Form error")),
      name => Ok(Json.obj("id" -> 9999, "name" -> name))
    )
  }

  def putUser(id: Int) = cors { implicit request =>
    Form(single("name" -> text)).bindFromRequest().fold(
      _ => InternalServerError(Json.obj("message" -> "Form error")),
      name => Ok(Json.obj("id" -> id, "name" -> name))
    )
  }
}
