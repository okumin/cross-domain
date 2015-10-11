package controllers

import play.api.mvc.{Action, Controller}

class JSONP extends Controller {
  def callAPI = Action {
    Ok(views.html.jsonp.call_api())
  }
}
