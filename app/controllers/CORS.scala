package controllers

import play.api.mvc.{Action, Controller, Cookie}

class CORS extends Controller {
  def callAPISimple = Action {
    Ok(views.html.cors.call_api_simple())
  }

  def callAPIPreflight = Action {
    Ok(views.html.cors.call_api_preflight())
  }

  def callAPICredentials = Action {
    Ok(views.html.cors.call_api_credentials()).withCookies(
      Cookie("session", "1a1a1a", domain = Some("mofu.poyo"))
    )
  }
}
