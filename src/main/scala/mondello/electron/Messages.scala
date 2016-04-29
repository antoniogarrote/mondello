package mondello.electron

import io.atom.electron.{Event, IPCMain, IPCRenderer}

object Messages {
  val MONDELLO_SETTINGS_PATH = "mondello-settings-path"

  def mondelloSettingsPath:String = IPCRenderer.sendSync[Unit,String](MONDELLO_SETTINGS_PATH,())

  def subscribeMondelloSettingsPath(value:String):Unit = {
    IPCMain.onMessage[Unit,String](MONDELLO_SETTINGS_PATH, {(evt:Event[String],_:Unit) =>
      evt.returnValue = value
    })
  }

}
