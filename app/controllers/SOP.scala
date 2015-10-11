package controllers

import play.api.mvc.{Action, Controller}

class SOP extends Controller {
  def outer = Action {
    Ok(views.html.sop.outer_iframe())
  }

  def callAPI = Action {
    Ok(views.html.sop.call_api())
  }
}
