package screen

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.typesafe.config._

class PlayScreen {
  private val config = ConfigFactory.load()
  private val dX = config.getInt("display.x")
  private val dY = config.getInt("display.y")
  private val dW = config.getInt("display.w")
  private val dH = config.getInt("display.h")

  val display = new Display
  import javax.swing.JFrame

  val frame = new JFrame("Kancolle Browser")

  import chrriis.dj.nativeswing.swtimpl.NativeInterface
  import chrriis.common.UIUtils
  import javax.swing.SwingUtilities

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
        frame.setSize(dW, dH)
        frame.setLocation(dX, dY);
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
  private def controllScreen(c: => Unit) = SwingUtilities.invokeLater(new Runnable(){override def run()=c})
}

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
sealed class Display extends JWebBrowser {
  private val config = ConfigFactory.load()
  private val pX = config.getInt("display.painX")
  private val pY = config.getInt("display.painY")
  private val pW = config.getInt("display.painW")
  private val pH = config.getInt("display.painH")
  private val loginUrl = config.getString("kancolle.url.login")
  private val loginUrlStartWith = config.getString("kancolle.url.startWith.login")
  private val gameUrlStartWith = config.getString("kancolle.url.startWith.game")
  private val id = config.getString("kancolle.account.id")
  private val pass = config.getString("kancolle.account.pass")

  val accountJS = s"var login_id = document.getElementById('login_id');login_id.value = '$id' ;" +
                  s"var login_pass = document.getElementById('password');login_pass.value = '$pass';" +
  							  "var save_login_id = document.getElementById('save_login_id');save_login_id.checked='checked';"+
  							  "var save_password = document.getElementById('save_password');save_password.checked='checked';"+
  							  "var use_auto_login = document.getElementById('use_auto_login');use_auto_login.checked='checked';"

  import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
  import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
  this.addWebBrowserListener(new WebBrowserAdapter() {
    override def locationChanged(e: WebBrowserNavigationEvent) = {
      getResourceLocation match {
        case url if url.startsWith(loginUrlStartWith) => executeJavascript(accountJS)
        case url if url.startsWith(gameUrlStartWith) =>
          Option(Jsoup.parse(getHTMLContent).select("iframe#game_frame").first).foreach(u => navigate(u.attr("src")))
        case _ =>
      }
    }
  })

  this.setBounds(pX, pY, pH, pW)
  this.setBarsVisible(false)
  this.setMenuBarVisible(false)
  this.setButtonBarVisible(false)
  this.setLocationBarVisible(false)
  this.setStatusBarVisible(false)
  this.navigate(loginUrl)
}