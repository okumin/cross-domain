package controllers

import play.api.mvc.{Action, Controller}

class PostMessage extends Controller {
  def outer = Action {
    Ok(views.html.postmessage.outer_iframe())
  }

  def inner = Action {
    Ok(views.html.postmessage.inner())
  }

  def callAPI = Action {
    Ok(views.html.postmessage.call_api())
  }

  def innerCallAPI = Action {
    Ok(views.html.postmessage.inner_call_api())
  }
}
