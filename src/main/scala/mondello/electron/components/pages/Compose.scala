package mondello.electron.components.pages

import knockout.{KoComponent, KoComputed}
import mondello.proxies.DockerCompose

import scala.scalajs.js.{Any, Dictionary}
import scalatags.Text.all._

object Compose extends KoComponent("docker-compose") {

  var dockerCompose:KoComputed[DockerCompose] = null

  override def viewModel(params: Dictionary[Any]): Unit = {
    dockerCompose = params("dockerCompose").asInstanceOf[KoComputed[DockerCompose]]
  }

  override def template: String = {
    div(`class`:="window-content",
      div(`class`:="pane-group"
        /*
        ImagesBrowser.tag(`class`:="pane pane-sm sidebar",
          KoText.all.params:="loadingImages: loadingImages, selectedImage: selectedImage, images: images"),
        SelectedImage.tag(`class`:="pane padded-more",
          KoText.all.params:="selectedImage: selectedImage")
       */
      )
      /*
      ,
      LaunchConfigurationDialog.tag(),
      PullImageDialog.tag(),
      ImageFooter.tag(`class`:="toolbar-footer",
        KoText.all.params:="selectedImage: selectedImage")
        */
    ).toString()
  }
}
