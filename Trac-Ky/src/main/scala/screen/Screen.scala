package screen

private[screen] object Conf {
  import com.typesafe.config._
  private val config = ConfigFactory.load()
  val dX = config.getInt("display.x")
  val dY = config.getInt("display.y")
  val dW = config.getInt("display.w")
  val dH = config.getInt("display.h")
  val pX = config.getInt("display.paneX")
  val pY = config.getInt("display.paneY")
  val pW = config.getInt("display.paneW")
  val pH = config.getInt("display.paneH")
  val loginUrl = config.getString("kancolle.url.login")
  val loginUrlStartWith = config.getString("kancolle.url.startWith.login")
  val gameUrlStartWith = config.getString("kancolle.url.startWith.game")
  val id = config.getString("kancolle.account.id")
  val pass = config.getString("kancolle.account.pass")
  val accountJS = "var login_id = document.getElementById('login_id');login_id.value = '" + id + "' ;" +
                  "var login_pass = document.getElementById('password');login_pass.value = '" + pass + "';" +
  							  "var save_login_id = document.getElementById('save_login_id');save_login_id.checked='checked';"+
  							  "var save_password = document.getElementById('save_password');save_password.checked='checked';"+
  							  "var use_auto_login = document.getElementById('use_auto_login');use_auto_login.checked='checked';"
}

class PlayScreen {
  import javax.swing.JFrame
  import chrriis.dj.nativeswing.swtimpl.NativeInterface
  import chrriis.common.UIUtils
  import javax.swing.SwingUtilities

  val display = new Display
  val frame = new JFrame("Kancolle Browser")

  def showScreen(): Unit = {
    NativeInterface.open()
    UIUtils.setPreferredLookAndFeel()
    SwingUtilities.invokeLater(new Runnable() {
      override def run() ={
        frame.setAlwaysOnTop(true)
        frame.setUndecorated(true)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.getContentPane().setLayout(null)
        frame.getContentPane().add(display)
        frame.setSize(Conf.dW, Conf.dH)
        frame.setLocation(Conf.dX, Conf.dY);
        frame.setResizable(false);
        frame.setVisible(true);
        }
    })
  }

  def close() = {
    frame.dispose()
    controllScreen(display.disposeNativePeer())
    NativeInterface.close()
  }
  def navigate(url: String) = controllScreen(display.navigate(url))
  def reload() = controllScreen(display.reloadPage())
  private def controllScreen(c: => Unit) =
    SwingUtilities.invokeLater(new Runnable(){override def run()=c})
}

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
private[screen] class Display extends JWebBrowser {

  this.addWebBrowserListener(new WebBrowserAdapter() {
    override def locationChanged(e: WebBrowserNavigationEvent) = {
      getResourceLocation match {
        case url if url.startsWith(Conf.loginUrlStartWith) => executeJavascript(Conf.accountJS)
        case url if url.startsWith(Conf.gameUrlStartWith) =>
          Option(Jsoup.parse(getHTMLContent).select("iframe#game_frame").first)
            .foreach(u => navigate(u.attr("src")))
        case _ =>
      }
    }
  })

  this.setBounds(Conf.pX, Conf.pY, Conf.pH,Conf.pW)
  this.setBarsVisible(false)
  this.setMenuBarVisible(false)
  this.setButtonBarVisible(false)
  this.setLocationBarVisible(false)
  this.setStatusBarVisible(false)
  this.navigate(Conf.loginUrl)
}