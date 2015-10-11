package controllers

import play.api.mvc.{Action, Controller}

class Domain extends Controller {
  def outer = Action {
    Ok(views.html.domain.outer_iframe())
  }

  def outerNG = Action {
    Ok(views.html.domain.outer_iframe_ng())
  }

  def inner = Action {
    Ok(views.html.domain.inner())
  }

  def callAPI = Action {
    Ok(views.html.domain.call_api())
  }
}
